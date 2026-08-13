package com.rohan.raksha.geofence.geofence

import android.Manifest
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.content.ContextCompat
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingClient
import com.google.android.gms.location.GeofencingRequest
import com.google.android.gms.location.LocationServices
import com.rohan.raksha.geofence.data.SavedLocation

class GeofenceManager(private val context: Context) {
    private val client: GeofencingClient = LocationServices.getGeofencingClient(context)
    private val activeGeofenceIds = mutableSetOf<String>()

    private fun getPendingIntent(): PendingIntent {
        val intent = Intent(GeofenceConstants.ACTION_GEOFENCE_EVENT).apply {
            setPackage(context.packageName)
        }
        return PendingIntent.getBroadcast(
            context, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
        )
    }

    fun registerGeofences(locations: List<SavedLocation>) {
        if (!hasPermissions()) {
            Log.w("GeofenceManager", "Missing location permissions")
            return
        }
        val toRegister = locations.filter { it.isEnabled && it.id.toString() !in activeGeofenceIds }
        if (toRegister.isEmpty()) return

        val geofences = toRegister.map { loc ->
            Geofence.Builder()
                .setRequestId(loc.id.toString())
                .setCircularRegion(loc.latitude, loc.longitude, GeofenceConstants.SYSTEM_GEOFENCE_RADIUS_METERS)
                .setExpirationDuration(Geofence.NEVER_EXPIRE)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_EXIT or Geofence.GEOFENCE_TRANSITION_ENTER)
                .setLoiteringDelay(30_000)
                .build()
        }
        val request = GeofencingRequest.Builder()
            .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
            .addGeofences(geofences)
            .build()

        try {
            client.addGeofences(request, getPendingIntent()).addOnSuccessListener {
                toRegister.forEach { activeGeofenceIds.add(it.id.toString()) }
                Log.d("GeofenceManager", "Registered ${toRegister.size} geofences")
            }.addOnFailureListener { e ->
                Log.e("GeofenceManager", "Failed to register geofences", e)
            }
        } catch (e: SecurityException) {
            Log.e("GeofenceManager", "SecurityException missing permissions", e)
        }
    }

    fun removeGeofence(locationId: Int) {
        val id = locationId.toString()
        client.removeGeofences(listOf(id)).addOnSuccessListener {
            activeGeofenceIds.remove(id)
        }
    }

    fun removeAllGeofences() {
        client.removeGeofences(getPendingIntent()).addOnSuccessListener {
            activeGeofenceIds.clear()
        }
    }

    private fun hasPermissions(): Boolean {
        val fine = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        val bg = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED
        } else true
        return fine && bg
    }

    fun getActiveIds(): Set<String> = activeGeofenceIds.toSet()
}
