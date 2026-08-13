package com.rohan.raksha.geofence.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.rohan.raksha.geofence.data.SavedLocation
import com.rohan.raksha.geofence.ui.viewmodel.MainViewModel
import org.maplibre.android.maps.MapLibreMap
import org.maplibre.android.maps.MapView
import org.maplibre.android.maps.Style

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(viewModel: MainViewModel, onNavigateAdd: () -> Unit, onNavigateSettings: () -> Unit) {
    val locations by viewModel.savedLocations.collectAsState()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Raksha Geo Fencing") },
                actions = {
                    IconButton(onClick = onNavigateSettings) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onNavigateAdd) {
                Icon(Icons.Default.Add, contentDescription = "Add Location")
            }
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize()) {
            Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
                AndroidView(
                    factory = { ctx ->
                        MapView(ctx).apply {
                            getMapAsync { map ->
                                map.setStyle(Style.Builder().fromUri("https://tiles.openfreemap.org/styles/liberty"))
                                // Add markers in future
                            }
                        }
                    },
                    modifier = Modifier.fillMaxSize()
                )
            }
            LazyColumn(modifier = Modifier.weight(1f).fillMaxWidth()) {
                items(locations) { loc ->
                    LocationCard(loc, onToggle = { viewModel.toggleEnabled(loc) }, onDelete = { viewModel.deleteLocation(loc) })
                }
            }
        }
    }
}

@Composable
fun LocationCard(location: SavedLocation, onToggle: () -> Unit, onDelete: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
        Row(modifier = Modifier.padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween) {
            Column {
                Text(location.name, style = MaterialTheme.typography.titleMedium)
                Text("Exit: ${location.exitRadiusMeters}m | Arr: ${location.arrivalRadiusMeters}m")
            }
            Column {
                Switch(checked = location.isEnabled, onCheckedChange = { onToggle() })
                Button(onClick = onDelete) { Text("Delete") }
            }
        }
    }
}
