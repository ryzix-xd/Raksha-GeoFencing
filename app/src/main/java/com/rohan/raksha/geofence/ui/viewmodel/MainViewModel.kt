package com.rohan.raksha.geofence.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.rohan.raksha.geofence.data.AppDatabase
import com.rohan.raksha.geofence.data.SavedLocation
import com.rohan.raksha.geofence.geofence.GeofenceManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val db = AppDatabase.getInstance(application)
    private val geofenceManager = GeofenceManager(application)

    private val _savedLocations = MutableStateFlow<List<SavedLocation>>(emptyList())
    val savedLocations: StateFlow<List<SavedLocation>> = _savedLocations.asStateFlow()

    init {
        loadLocations()
    }

    fun loadLocations() {
        viewModelScope.launch {
            val locations = db.savedLocationDao().getAll()
            _savedLocations.value = locations
            updateGeofences(locations)
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
            loadLocations()
        }
    }

    fun deleteLocation(location: SavedLocation) {
        viewModelScope.launch {
            db.savedLocationDao().delete(location)
            geofenceManager.removeGeofence(location.id)
            loadLocations()
        }
    }
}
