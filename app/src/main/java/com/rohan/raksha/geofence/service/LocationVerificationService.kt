package com.rohan.raksha.geofence.service

import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.location.Location
import android.os.IBinder
import android.util.Log
import kotlinx.coroutines.*
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.rohan.raksha.geofence.data.AppDatabase
import com.rohan.raksha.geofence.geofence.GeofenceConstants
import com.rohan.raksha.geofence.notifications.NotificationHelper
import com.rohan.raksha.geofence.util.DistanceUtil
import kotlinx.coroutines.tasks.await

class LocationVerificationService : Service() {
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private lateinit var notificationHelper: NotificationHelper

    override fun onCreate() {
        super.onCreate()
        notificationHelper = NotificationHelper(this)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val locationId = intent?.getIntExtra("location_id", -1) ?: -1
        val transitionType = intent?.getStringExtra("transition_type") ?: ""
        
        if (locationId == -1) {
            stopSelf()
            return START_NOT_STICKY
        }

        val notification = notificationHelper.createVerificationNotification()
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            startForeground(GeofenceConstants.NOTIFICATION_ID_FOREGROUND, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_LOCATION)
        } else {
            startForeground(GeofenceConstants.NOTIFICATION_ID_FOREGROUND, notification)
        }

        scope.launch {
            try {
                verifyLocation(locationId, transitionType)
            } catch (e: Exception) {
                Log.e("LocationVerification", "Error during verification", e)
            } finally {
                stopSelf(startId)
            }
        }
        return START_NOT_STICKY
    }

    private suspend fun verifyLocation(locationId: Int, transitionType: String) {
        val db = AppDatabase.getInstance(this)
        val savedLoc = db.savedLocationDao().getById(locationId) ?: return
        
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        var location: Location? = null
        try {
            val token = com.google.android.gms.tasks.CancellationTokenSource().token
            location = fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, token).await()
        } catch (e: SecurityException) {
            Log.e("LocationVerification", "Permission denied", e)
        }

        if (location == null) return

        val distance = DistanceUtil.distanceBetween(
            location.latitude, location.longitude,
            savedLoc.latitude, savedLoc.longitude
        )

        val prefs = getSharedPreferences("geofence_prefs", Context.MODE_PRIVATE)
        val lastTime = prefs.getLong("last_transition_${locationId}", 0L)
        val now = System.currentTimeMillis()
        if (now - lastTime < GeofenceConstants.GEOFENCE_TRANSITION_DEBOUNCE_MS) {
            return
        }

        if (transitionType == "EXIT" && distance >= savedLoc.exitRadiusMeters) {
            prefs.edit().putLong("last_transition_${locationId}", now).apply()
            notificationHelper.showExitNotification(savedLoc)
            if (savedLoc.shieldEnabled) {
                // Future Shield implementation logic
            }
        } else if (transitionType == "ENTER" && distance <= savedLoc.arrivalRadiusMeters) {
            prefs.edit().putLong("last_transition_${locationId}", now).apply()
            notificationHelper.showArrivalNotification(savedLoc)
        }
    }

    override fun onBind(intent: Intent?): IBinder? = null
    override fun onDestroy() {
        super.onDestroy()
        scope.cancel()
    }
}
