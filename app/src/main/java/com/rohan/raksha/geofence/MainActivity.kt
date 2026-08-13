package com.rohan.raksha.geofence

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.rohan.raksha.geofence.ui.screen.AddLocationScreen
import com.rohan.raksha.geofence.ui.screen.DebugScreen
import com.rohan.raksha.geofence.ui.screen.MainScreen
import com.rohan.raksha.geofence.ui.screen.SettingsScreen
import com.rohan.raksha.geofence.ui.theme.RakshaGeoFencingTheme
import com.rohan.raksha.geofence.ui.viewmodel.AddLocationViewModel
import com.rohan.raksha.geofence.ui.viewmodel.MainViewModel
import org.maplibre.android.MapLibre

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MapLibre.getInstance(this)
        
        setContent {
            RakshaGeoFencingTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    com.rohan.raksha.geofence.util.PermissionsWrapper {
                        val navController = rememberNavController()
                        NavHost(navController = navController, startDestination = "main") {
                            composable("main") {
                                val vm: MainViewModel = viewModel()
                                MainScreen(
                                    viewModel = vm,
                                    onNavigateAdd = { navController.navigate("add") },
                                    onNavigateSettings = { navController.navigate("settings") }
                                )
                            }
                            composable("add") {
                                val vm: AddLocationViewModel = viewModel()
                                AddLocationScreen(
                                    viewModel = vm,
                                    onBack = { navController.popBackStack() }
                                )
                            }
                            composable("settings") {
                                SettingsScreen(onBack = { navController.popBackStack() })
                            }
                            composable("debug") {
                                DebugScreen()
                            }
                        }
                    }
                }
            }
        }
    }
}
