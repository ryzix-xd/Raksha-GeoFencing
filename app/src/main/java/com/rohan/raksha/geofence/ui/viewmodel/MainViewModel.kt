package com.rohan.raksha.geofence.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.rohan.raksha.geofence.data.AppDatabase
import com.rohan.raksha.geofence.data.SavedLocation
import com.rohan.raksha.geofence.geofence.GeofenceManager
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val db = AppDatabase.getInstance(application)
    private val geofenceManager = GeofenceManager(application)

    val savedLocations: StateFlow<List<SavedLocation>> = db.savedLocationDao().getAll()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    init {
        viewModelScope.launch {
            savedLocations.collect { locations ->
                updateGeofences(locations)
            }
        }
    }

    private fun updateGeofences(locations: List<SavedLocation>) {
        geofenceManager.removeAllGeofences()
        geofenceManager.registerGeofences(locations)
    }

    fun toggleEnabled(location: SavedLocation) {
        viewModelScope.launch {
            val updated = location.copy(isEnabled = !location.isEnabled)
            db.savedLocationDao().update(updated)
        }
    }

    fun deleteLocation(location: SavedLocation) {
        viewModelScope.launch {
            db.savedLocationDao().delete(location)
            geofenceManager.removeGeofence(location.id)
        }
    }
}
