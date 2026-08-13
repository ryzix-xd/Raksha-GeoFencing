package com.rohan.raksha.geofence.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.rohan.raksha.geofence.data.AppDatabase
import com.rohan.raksha.geofence.data.SavedLocation
import kotlinx.coroutines.launch

class AddLocationViewModel(application: Application) : AndroidViewModel(application) {
    private val db = AppDatabase.getInstance(application)

    fun saveLocation(name: String, lat: Double, lng: Double, exitRad: Float, arrRad: Float, shield: Boolean, onDone: () -> Unit) {
        viewModelScope.launch {
            val loc = SavedLocation(
                name = name,
                latitude = lat,
                longitude = lng,
                exitRadiusMeters = exitRad,
                arrivalRadiusMeters = arrRad,
                shieldEnabled = shield
            )
            db.savedLocationDao().insert(loc)
            onDone()
        }
    }
}
