package com.rohan.raksha.geofence.ui.screen

import android.Manifest
import android.content.pm.PackageManager
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.app.ActivityCompat
import com.rohan.raksha.geofence.ui.viewmodel.AddLocationViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.maplibre.android.camera.CameraUpdateFactory
import org.maplibre.android.geometry.LatLng
import org.maplibre.android.location.LocationComponentActivationOptions
import org.maplibre.android.location.modes.CameraMode
import org.maplibre.android.location.modes.RenderMode
import org.maplibre.android.maps.MapLibreMap
import org.maplibre.android.maps.MapView
import org.maplibre.android.maps.Style
import java.net.URL
import java.net.URLEncoder

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddLocationScreen(viewModel: AddLocationViewModel, onBack: () -> Unit) {
    var name by remember { mutableStateOf("") }
    var searchQuery by remember { mutableStateOf("") }
    var exitRadius by remember { mutableStateOf(100f) }
    var arrRadius by remember { mutableStateOf(100f) }
    var shield by remember { mutableStateOf(false) }

    var mapLibreMap by remember { mutableStateOf<MapLibreMap?>(null) }
    val scope = rememberCoroutineScope()

    val animatedExitRadius by animateFloatAsState(targetValue = exitRadius, animationSpec = tween(300))
    val animatedArrRadius by animateFloatAsState(targetValue = arrRadius, animationSpec = tween(300))

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add Location", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search places (e.g. Times Square)") },
                trailingIcon = {
                    IconButton(onClick = {
                        if (searchQuery.isNotEmpty()) {
                            scope.launch {
                                val url = "https://nominatim.openstreetmap.org/search?q=${URLEncoder.encode(searchQuery, "UTF-8")}&format=json"
                                val result = withContext(Dispatchers.IO) {
                                    try { URL(url).readText() } catch (e: Exception) { null }
                                }
                                if (result != null) {
                                    try {
                                        val array = JSONArray(result)
                                        if (array.length() > 0) {
                                            val obj = array.getJSONObject(0)
                                            val lat = obj.getString("lat").toDouble()
                                            val lon = obj.getString("lon").toDouble()
                                            mapLibreMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(LatLng(lat, lon), 15.0), 1000)
                                        }
                                    } catch (e: Exception) {}
                                }
                            }
                        }
                    }) {
                        Icon(Icons.Default.Search, contentDescription = "Search")
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.surfaceVariant,
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface
                ),
                singleLine = true
            )

            // Map Box
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .shadow(8.dp, RoundedCornerShape(24.dp))
                    .clip(RoundedCornerShape(24.dp))
            ) {
                AndroidView(
                    factory = { ctx ->
                        MapView(ctx).apply {
                            getMapAsync { map ->
                                mapLibreMap = map
                                map.setStyle(Style.Builder().fromUri("https://basemaps.cartocdn.com/gl/voyager-gl-style/style.json")) { style ->
                                    val locationComponent = map.locationComponent
                                    locationComponent.activateLocationComponent(
                                        LocationComponentActivationOptions.builder(ctx, style).build()
                                    )
                                    if (ActivityCompat.checkSelfPermission(ctx, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                                        locationComponent.isLocationComponentEnabled = true
                                        locationComponent.cameraMode = CameraMode.TRACKING
                                        locationComponent.renderMode = RenderMode.COMPASS
                                        locationComponent.zoomWhileTracking(15.0)
                                    }
                                }
                            }
                        }
                    },
                    modifier = Modifier.fillMaxSize()
                )
                
                // Static Center Pin
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = "Center Pin",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .size(48.dp)
                        .offset(y = (-24).dp) // Offset slightly so bottom points to center
                )
            }
            
            // Details and Settings in Scrollable Column
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1.2f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Card(
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(24.dp)) {
                        Text("Location Details", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        OutlinedTextField(
                            value = name, onValueChange = { name = it }, 
                            label = { Text("Name (e.g. Home, Work)") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Card(
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(24.dp)) {
                        Text("Geofence Radius", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Text("Exit Radius: ${animatedExitRadius.toInt()}m", color = MaterialTheme.colorScheme.onSurface)
                        Slider(
                            value = exitRadius, onValueChange = { exitRadius = it }, 
                            valueRange = 50f..1000f,
                            colors = SliderDefaults.colors(
                                thumbColor = MaterialTheme.colorScheme.primary,
                                activeTrackColor = MaterialTheme.colorScheme.primary
                            )
                        )
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        Text("Arrival Radius: ${animatedArrRadius.toInt()}m", color = MaterialTheme.colorScheme.onSurface)
                        Slider(
                            value = arrRadius, onValueChange = { arrRadius = it }, 
                            valueRange = 50f..1000f,
                            colors = SliderDefaults.colors(
                                thumbColor = MaterialTheme.colorScheme.secondary,
                                activeTrackColor = MaterialTheme.colorScheme.secondary
                            )
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(), 
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Activate Shield", fontWeight = FontWeight.Bold)
                                Text("Turn on automatically when exiting", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Switch(
                                checked = shield, 
                                onCheckedChange = { shield = it },
                                colors = SwitchDefaults.colors(checkedThumbColor = MaterialTheme.colorScheme.primary)
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                Button(
                    onClick = {
                        val target = mapLibreMap?.cameraPosition?.target
                        val lat = target?.latitude ?: 0.0
                        val lon = target?.longitude ?: 0.0
                        viewModel.saveLocation(
                            name.ifEmpty { "New Location" }, 
                            lat, lon, 
                            exitRadius, arrRadius, shield
                        ) {
                            onBack()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text("Save Location", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimary)
                }
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}
