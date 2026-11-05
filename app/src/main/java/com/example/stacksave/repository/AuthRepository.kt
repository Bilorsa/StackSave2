package com.example.stacksave.repository

import android.content.Context
import com.example.stacksave.util.FirebaseManager
import kotlinx.coroutines.tasks.await

class AuthRepository(private val context: Context) {
    private val auth get() = FirebaseManager.auth(context)

    fun isFirebaseConfigured() = FirebaseManager.isConfigured(context)
    fun firebaseStatus() = FirebaseManager.status(context)
    fun currentUid() = auth?.currentUser?.uid

    suspend fun login(email: String, password: String): Result<String> {
        return try {
        val firebaseAuth = auth
            ?: return Result.failure(IllegalStateException(FirebaseManager.status(context)))
        val user = firebaseAuth.signInWithEmailAndPassword(email, password).await().user
            ?: return Result.failure(IllegalStateException("Firebase signed in without returning a user."))
        Result.success(user.uid)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun register(email: String, password: String, displayName: String): Result<String> {
        return try {
        val firebaseAuth = auth
            ?: return Result.failure(IllegalStateException(FirebaseManager.status(context)))
        val user = firebaseAuth.createUserWithEmailAndPassword(email, password).await().user
            ?: return Result.failure(IllegalStateException("Firebase created the account without returning a user."))
        if (displayName.isNotBlank()) {
            val profileUpdates = com.google.firebase.auth.UserProfileChangeRequest.Builder()
                .setDisplayName(displayName)
                .build()
            user.updateProfile(profileUpdates).await()
        }
        Result.success(user.uid)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun signOut() {
        auth?.signOut()
    }
}
