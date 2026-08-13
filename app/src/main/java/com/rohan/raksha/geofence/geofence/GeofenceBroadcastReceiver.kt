package com.rohan.raksha.geofence.geofence

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.google.android.gms.location.GeofencingEvent
import com.google.android.gms.location.Geofence
import com.rohan.raksha.geofence.service.LocationVerificationService

class GeofenceBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val event = GeofencingEvent.fromIntent(intent) ?: return
        if (event.hasError()) {
            Log.e("GeofenceReceiver", "Geofencing error: ${event.errorCode}")
            return
        }
        val transition = event.geofenceTransition
        val triggeringGeofences = event.triggeringGeofences ?: return

        for (geofence in triggeringGeofences) {
            val locationId = geofence.requestId.toIntOrNull() ?: continue
            when (transition) {
                Geofence.GEOFENCE_TRANSITION_EXIT -> {
                    Log.d("GeofenceReceiver", "EXIT transition for location $locationId")
                    val serviceIntent = Intent(context, LocationVerificationService::class.java).apply {
                        putExtra("location_id", locationId)
                        putExtra("transition_type", "EXIT")
                    }
                    context.startForegroundService(serviceIntent)
                }
                Geofence.GEOFENCE_TRANSITION_ENTER -> {
                    Log.d("GeofenceReceiver", "ENTER transition for location $locationId")
                    val serviceIntent = Intent(context, LocationVerificationService::class.java).apply {
                        putExtra("location_id", locationId)
                        putExtra("transition_type", "ENTER")
                    }
                    context.startForegroundService(serviceIntent)
                }
            }
        }
    }
}
