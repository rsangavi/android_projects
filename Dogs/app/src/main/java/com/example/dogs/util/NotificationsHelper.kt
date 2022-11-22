package com.example.dogs.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.dogs.R
import com.example.dogs.view.MainActivity

class NotificationsHelper(val context: Context) {
    private val CHANNEL_ID = "Dogs Channel id"
    private val NOTIFICATION_ID = 123

    private fun createNotificationChannel() {
        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(CHANNEL_ID, CHANNEL_ID,
                    NotificationManager.IMPORTANCE_DEFAULT).apply {
                description = "Notification about dogs"
            }
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(notificationChannel)
        }
    }

    fun createNotification() {
        createNotificationChannel()
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(context, 0, intent, 0)
        val icon = BitmapFactory.decodeResource(context.resources, R.drawable.dog_noti_large_icon)

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.dog__noti_small_icon)
                .setLargeIcon(icon)
                .setContentTitle("Dog Notification...")
                .setContentText("Open you the Dog app...")
                .setStyle(
                        NotificationCompat.BigPictureStyle()
                                .bigPicture(icon)
                                .bigLargeIcon(null)
                )
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .build()

        NotificationManagerCompat.from(context).notify(NOTIFICATION_ID, notification)


    }
}