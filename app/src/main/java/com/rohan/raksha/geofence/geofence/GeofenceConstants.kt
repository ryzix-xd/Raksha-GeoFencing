package com.rohan.raksha.geofence.geofence

object GeofenceConstants {
    const val SYSTEM_GEOFENCE_RADIUS_METERS = 150f
    const val LOGICAL_EXIT_RADIUS_METERS = 50.0
    const val LOGICAL_ARRIVAL_RADIUS_METERS = 50.0
    const val GEOFENCE_TRANSITION_DEBOUNCE_MS = 10_000L
    const val LOCATION_VERIFICATION_TIMEOUT_MS = 30_000L
    const val MIN_ACCURACY_METERS = 100f
    const val NOTIFICATION_CHANNEL_GEOFENCE = "raksha_geofence"
    const val NOTIFICATION_CHANNEL_SERVICE = "raksha_service"
    const val NOTIFICATION_ID_FOREGROUND = 1001
    const val ACTION_GEOFENCE_EVENT = "com.rohan.raksha.geofence.ACTION_GEOFENCE_EVENT"
}
