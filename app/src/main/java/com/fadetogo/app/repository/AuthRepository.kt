package com.fadetogo.app.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.fadetogo.app.model.User
import kotlinx.coroutines.tasks.await

class AuthRepository {

    // These are our connections to Firebase
    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    // This gets the currently logged in user
    val currentUser get() = auth.currentUser

    // REGISTER - creates a new account in Firebase Auth
    // then saves the user profile to Firestore
    suspend fun register(
        email: String,
        password: String,
        name: String,
        phone: String,
        role: String
    ): Result<User> {
        return try {
            // Step 1: Create the account in Firebase Auth
            val authResult = auth.createUserWithEmailAndPassword(email, password).await()
            val uid = authResult.user?.uid ?: throw Exception("User ID not found")

            // Step 2: Build the user object using our User model
            val user = User(
                uid = uid,
                name = name,
                email = email,
                phone = phone,
                role = role,
                isAvailable = false
            )

            // Step 3: Save that user object to Firestore
            // so we can retrieve their role and info later
            firestore.collection("users")
                .document(uid)
                .set(user)
                .await()

            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // LOGIN - signs in with email and password
    // then fetches the user profile from Firestore to get their role
    suspend fun login(email: String, password: String): Result<User> {
        return try {
            // Step 1: Sign in through Firebase Auth
            val authResult = auth.signInWithEmailAndPassword(email, password).await()
            val uid = authResult.user?.uid ?: throw Exception("User ID not found")

            // Step 2: Fetch their profile from Firestore
            // this is how we know if they are a barber or customer
            val document = firestore.collection("users")
                .document(uid)
                .get()
                .await()

            val user = document.toObject(User::class.java)
                ?: throw Exception("User profile not found")

            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // LOGOUT - signs the user out of Firebase Auth
    fun logout() {
        auth.signOut()
    }

    // GET USER PROFILE - fetches a user's profile by their uid
    suspend fun getUserProfile(uid: String): Result<User> {
        return try {
            val document = firestore.collection("users")
                .document(uid)
                .get()
                .await()

            val user = document.toObject(User::class.java)
                ?: throw Exception("User profile not found")

            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // FORGOT PASSWORD - sends a password reset email through Firebase Auth
    // Firebase handles the entire reset flow, we just trigger the email
    suspend fun sendPasswordResetEmail(email: String): Result<Unit> {
        return try {
            auth.sendPasswordResetEmail(email).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}