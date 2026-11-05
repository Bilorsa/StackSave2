package com.example.stacksave.util

import android.content.Context
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

/**
 * Central Firebase access point.
 *
 * Firebase is normally initialised by FirebaseInitProvider using app/google-services.json.
 * initialize() is also called from MainActivity so configuration problems can be reported
 * clearly instead of silently treating a local session as a Firebase session.
 */
object FirebaseManager {
    fun initialize(context: Context): FirebaseApp? {
        return try {
            FirebaseApp.getApps(context).firstOrNull() ?: FirebaseApp.initializeApp(context)
        } catch (_: Exception) {
            null
        }
    }

    fun app(context: Context): FirebaseApp? = initialize(context)

    fun isConfigured(context: Context): Boolean {
        val firebaseApp = app(context) ?: return false
        return !firebaseApp.options.applicationId.isNullOrBlank() &&
            !firebaseApp.options.projectId.isNullOrBlank() &&
            !firebaseApp.options.apiKey.isNullOrBlank()
    }

    fun projectId(context: Context): String? =
        if (isConfigured(context)) app(context)?.options?.projectId else null

    fun status(context: Context): String {
        val firebaseApp = app(context)
        return when {
            firebaseApp == null -> "Firebase is not configured. Add the correct app/google-services.json file."
            !isConfigured(context) -> "Firebase configuration is incomplete. Download a fresh google-services.json from Firebase Console."
            else -> "Firebase project: ${firebaseApp.options.projectId}"
        }
    }

    fun auth(context: Context): FirebaseAuth? =
        if (isConfigured(context)) FirebaseAuth.getInstance(app(context)!!) else null

    fun firestore(context: Context): FirebaseFirestore? =
        if (isConfigured(context)) FirebaseFirestore.getInstance(app(context)!!) else null
}
