package com.rohan.raksha.geofence.notifications

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.rohan.raksha.geofence.MainActivity
import com.rohan.raksha.geofence.data.SavedLocation
import com.rohan.raksha.geofence.geofence.GeofenceConstants

class NotificationHelper(private val context: Context) {
    private val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    init {
        createChannels()
    }

    private fun createChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val geofenceChannel = NotificationChannel(
                GeofenceConstants.NOTIFICATION_CHANNEL_GEOFENCE,
                "Geofence Alerts",
                NotificationManager.IMPORTANCE_HIGH
            )
            val serviceChannel = NotificationChannel(
                GeofenceConstants.NOTIFICATION_CHANNEL_SERVICE,
                "Verification Service",
                NotificationManager.IMPORTANCE_LOW
            )
            notificationManager.createNotificationChannel(geofenceChannel)
            notificationManager.createNotificationChannel(serviceChannel)
        }
    }

    private fun getMainActivityPendingIntent(): PendingIntent {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        return PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
    }

    fun createVerificationNotification(): Notification {
        return NotificationCompat.Builder(context, GeofenceConstants.NOTIFICATION_CHANNEL_SERVICE)
            .setContentTitle("Verifying location...")
            .setSmallIcon(android.R.drawable.ic_menu_mylocation)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
    }

    fun showExitNotification(location: SavedLocation) {
        val builder = NotificationCompat.Builder(context, GeofenceConstants.NOTIFICATION_CHANNEL_GEOFENCE)
            .setContentTitle("Exited: ${location.name}")
            .setContentText("You have left ${location.name}.")
            .setSmallIcon(android.R.drawable.ic_dialog_map)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(getMainActivityPendingIntent())
            .setAutoCancel(true)

        notificationManager.notify(location.id * 2, builder.build())
    }

    fun showArrivalNotification(location: SavedLocation) {
        val builder = NotificationCompat.Builder(context, GeofenceConstants.NOTIFICATION_CHANNEL_GEOFENCE)
            .setContentTitle("Arrived: ${location.name}")
            .setContentText("You have arrived at ${location.name}.")
            .setSmallIcon(android.R.drawable.ic_dialog_map)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(getMainActivityPendingIntent())
            .setAutoCancel(true)

        notificationManager.notify(location.id * 2 + 1, builder.build())
    }
}
