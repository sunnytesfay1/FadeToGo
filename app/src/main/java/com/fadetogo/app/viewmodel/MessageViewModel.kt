package com.fadetogo.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fadetogo.app.model.Message
import com.fadetogo.app.repository.MessageRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MessageViewModel : ViewModel() {

    private val repository = MessageRepository()

    // holds the list of messages in the current conversation
    // updates in real time as new messages arrive from Firestore
    private val _messages = MutableStateFlow<List<Message>>(emptyList())
    val messages: StateFlow<List<Message>> = _messages

    // holds the list of conversations for the inbox screen
    // each item is the most recent message from each conversation
    private val _conversations = MutableStateFlow<List<Message>>(emptyList())
    val conversations: StateFlow<List<Message>> = _conversations

    // tracks how many unread messages the user has
    // used to show notification badge on inbox icon
    private val _unreadCount = MutableStateFlow(0)
    val unreadCount: StateFlow<Int> = _unreadCount

    // tracks whether a message is being sent
    private val _isSending = MutableStateFlow(false)
    val isSending: StateFlow<Boolean> = _isSending

    // tracks loading state for inbox screen
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    // holds any error messages to show the user
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    // tracks the id of the user we are currently chatting with
    // used to know which conversation to load and listen to
    private var currentChatPartnerId: String? = null

    // SEND MESSAGE - called when user taps the send button in chat
    fun sendMessage(
        senderId: String,
        receiverId: String,
        content: String
    ) {
        // dont send empty messages
        if (content.isBlank()) return

        viewModelScope.launch {
            _isSending.value = true

            val message = Message(
                senderId = senderId,
                receiverId = receiverId,
                content = content.trim(),
                timestamp = System.currentTimeMillis(),
                isRead = false
            )

            val result = repository.sendMessage(message)

            result.onFailure { exception ->
                _errorMessage.value = exception.message
                    ?: "Could not send message. Please try again."
            }

            _isSending.value = false
        }
    }

    // LISTEN TO MESSAGES - starts a real time listener for a conversation
    // called when the user opens a chat screen
    // automatically updates the messages flow as new messages arrive
    fun listenToMessages(userId1: String, userId2: String) {
        currentChatPartnerId = userId2

        viewModelScope.launch {
            // collect the flow from the repository
            // every time Firestore updates this block runs automatically
            repository.getMessages(userId1, userId2).collect { messageList ->
                _messages.value = messageList
            }
        }
    }

    // LOAD CONVERSATIONS - fetches all conversations for the inbox screen
    fun loadConversations(userId: String) {
        viewModelScope.launch {
            _isLoading.value = true

            val result = repository.getConversations(userId)

            result.onSuccess { conversationList ->
                _conversations.value = conversationList
            }

            result.onFailure { exception ->
                _errorMessage.value = exception.message
                    ?: "Could not load conversations."
            }

            _isLoading.value = false
        }
    }

    // MARK AS READ - marks all messages in a conversation as read
    // called when the user opens a chat so the unread badge clears
    fun markMessagesAsRead(
        userId1: String,
        userId2: String,
        currentUserId: String
    ) {
        viewModelScope.launch {
            repository.markMessagesAsRead(userId1, userId2, currentUserId)

            // refresh unread count after marking as read
            loadUnreadCount(currentUserId)
        }
    }

    // LOAD UNREAD COUNT - fetches total unread messages across all conversations
    // called on home screen and dashboard to show badge on inbox icon
    fun loadUnreadCount(userId: String) {
        viewModelScope.launch {
            val result = repository.getUnreadCount(userId)

            result.onSuccess { count ->
                _unreadCount.value = count
            }
        }
    }

    // clears the current messages when leaving a chat screen
    // prevents old messages from flashing when opening a new conversation
    fun clearMessages() {
        _messages.value = emptyList()
        currentChatPartnerId = null
    }

    // clears error message after it has been shown to the user
    fun clearError() {
        _errorMessage.value = null
    }
}