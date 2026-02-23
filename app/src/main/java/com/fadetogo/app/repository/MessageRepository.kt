package com.fadetogo.app.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.fadetogo.app.model.Message
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class MessageRepository {

    private val firestore = FirebaseFirestore.getInstance()

    // SEND MESSAGE - saves a new message to Firestore
    // conversationId is a combination of both user ids sorted alphabetically
    // this ensures the same conversation id is generated regardless of who initiates
    suspend fun sendMessage(message: Message): Result<Unit> {
        return try {
            val conversationId = generateConversationId(
                message.senderId,
                message.receiverId
            )

            val docRef = firestore
                .collection("conversations")
                .document(conversationId)
                .collection("messages")
                .document()

            val messageWithId = message.copy(
                messageId = docRef.id,
                timestamp = System.currentTimeMillis()
            )

            docRef.set(messageWithId).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // GET MESSAGES - returns a real time flow of messages for a conversation
    // Flow is perfect here because messages need to appear instantly
    // without the user needing to refresh
    fun getMessages(
        userId1: String,
        userId2: String
    ): Flow<List<Message>> = callbackFlow {

        val conversationId = generateConversationId(userId1, userId2)

        // listen to Firestore in real time ordered by timestamp
        val listener = firestore
            .collection("conversations")
            .document(conversationId)
            .collection("messages")
            .orderBy("timestamp", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                val messages = snapshot?.documents?.mapNotNull {
                    it.toObject(Message::class.java)
                } ?: emptyList()

                // emit the new list of messages to the flow
                trySend(messages)
            }

        // when the flow is cancelled remove the Firestore listener
        // this prevents memory leaks when the user leaves the chat screen
        awaitClose { listener.remove() }
    }

    // GET CONVERSATIONS - returns all conversations for a user
    // used on the inbox screen to show a list of chats
    suspend fun getConversations(userId: String): Result<List<Message>> {
        return try {
            // fetch the most recent message from each conversation
            val snapshot = firestore
                .collection("conversations")
                .get()
                .await()

            val latestMessages = mutableListOf<Message>()

            for (doc in snapshot.documents) {
                val conversationId = doc.id

                // only include conversations this user is part of
                if (conversationId.contains(userId)) {
                    val messageSnapshot = firestore
                        .collection("conversations")
                        .document(conversationId)
                        .collection("messages")
                        .orderBy("timestamp", Query.Direction.DESCENDING)
                        .limit(1)
                        .get()
                        .await()

                    messageSnapshot.documents.firstOrNull()
                        ?.toObject(Message::class.java)
                        ?.let { latestMessages.add(it) }
                }
            }

            // sort by most recent first
            Result.success(latestMessages.sortedByDescending { it.timestamp })
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // MARK AS READ - updates all unread messages in a conversation
    suspend fun markMessagesAsRead(
        userId1: String,
        userId2: String,
        currentUserId: String
    ): Result<Unit> {
        return try {
            val conversationId = generateConversationId(userId1, userId2)

            // fetch all unread messages not sent by the current user
            val snapshot = firestore
                .collection("conversations")
                .document(conversationId)
                .collection("messages")
                .whereEqualTo("isRead", false)
                .whereNotEqualTo("senderId", currentUserId)
                .get()
                .await()

            // batch update all unread messages to read
            val batch = firestore.batch()
            snapshot.documents.forEach { doc ->
                batch.update(doc.reference, "isRead", true)
            }
            batch.commit().await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // GET UNREAD COUNT - returns number of unread messages for a user
    // used to show notification badges on the inbox icon
    suspend fun getUnreadCount(userId: String): Result<Int> {
        return try {
            val snapshot = firestore
                .collection("conversations")
                .get()
                .await()

            var unreadCount = 0

            for (doc in snapshot.documents) {
                if (doc.id.contains(userId)) {
                    val unreadSnapshot = firestore
                        .collection("conversations")
                        .document(doc.id)
                        .collection("messages")
                        .whereEqualTo("isRead", false)
                        .whereNotEqualTo("senderId", userId)
                        .get()
                        .await()

                    unreadCount += unreadSnapshot.size()
                }
            }

            Result.success(unreadCount)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // generates a consistent conversation id from two user ids
    // by sorting them alphabetically and joining with an underscore
    // so "userA_userB" and "userB_userA" always produce the same id
    private fun generateConversationId(userId1: String, userId2: String): String {
        return listOf(userId1, userId2).sorted().joinToString("_")
    }
}