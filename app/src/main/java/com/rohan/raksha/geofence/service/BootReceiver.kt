package com.rohan.raksha.geofence.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import kotlinx.coroutines.*
import com.rohan.raksha.geofence.data.AppDatabase
import com.rohan.raksha.geofence.geofence.GeofenceManager

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Intent.ACTION_BOOT_COMPLETED) return
        val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
        scope.launch {
            val db = AppDatabase.getInstance(context)
            val locations = db.savedLocationDao().getEnabled()
            val manager = GeofenceManager(context)
            manager.registerGeofences(locations)
        }
    }
}
