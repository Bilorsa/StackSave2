package com.example.stacksave.data
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
class StackSaveMessagingService : FirebaseMessagingService() {
    override fun onMessageReceived(message: RemoteMessage) { show(message.notification?.title ?: "StackSave", message.notification?.body ?: "New deal update") }
    private fun show(title:String, body:String) { val m=getSystemService(NotificationManager::class.java); val id="stacksave_deals"; if(Build.VERSION.SDK_INT>=26)m.createNotificationChannel(NotificationChannel(id,"Deal updates",NotificationManager.IMPORTANCE_DEFAULT)); m.notify(System.currentTimeMillis().toInt(),NotificationCompat.Builder(this,id).setSmallIcon(android.R.drawable.ic_dialog_info).setContentTitle(title).setContentText(body).setAutoCancel(true).build()) }
}
