package com.example.stacksave.model

data class UserProfile(
    val uid: String = "", val displayName: String = "", val email: String = "",
    val language: String = "en", val notificationsEnabled: Boolean = true,
    val offlineSyncEnabled: Boolean = true
)
