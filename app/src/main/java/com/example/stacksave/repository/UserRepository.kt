package com.example.stacksave.repository
import android.content.Context
import com.example.stacksave.model.UserProfile
import com.example.stacksave.util.FirebaseManager
import kotlinx.coroutines.tasks.await
class UserRepository(private val context: Context) {
    suspend fun saveProfile(profile: UserProfile): Result<Unit> = try {
        val db = FirebaseManager.firestore(context)
        if (db != null && profile.uid.isNotBlank()) db.collection("users").document(profile.uid).set(profile).await()
        Result.success(Unit)
    } catch (e: Exception) { Result.failure(e) }
    suspend fun loadProfile(uid: String): Result<UserProfile?> = try {
        val db = FirebaseManager.firestore(context) ?: return Result.success(null)
        Result.success(db.collection("users").document(uid).get().await().toObject(UserProfile::class.java))
    } catch (e: Exception) { Result.failure(e) }
}
