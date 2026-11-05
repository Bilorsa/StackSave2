package com.example.stacksave.data
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
object LocalNotification {
    fun show(context:Context,title:String,body:String){val m=context.getSystemService(NotificationManager::class.java);val id="stacksave_demo";if(Build.VERSION.SDK_INT>=26)m.createNotificationChannel(NotificationChannel(id,"StackSave Demo",NotificationManager.IMPORTANCE_DEFAULT));m.notify(1001,NotificationCompat.Builder(context,id).setSmallIcon(android.R.drawable.ic_dialog_info).setContentTitle(title).setContentText(body).setAutoCancel(true).build())}
}
