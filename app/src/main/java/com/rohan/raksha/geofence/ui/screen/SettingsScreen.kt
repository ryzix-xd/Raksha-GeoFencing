package com.rohan.raksha.geofence.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.rohan.raksha.geofence.util.MiuiHelper

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    Scaffold(
        topBar = { TopAppBar(title = { Text("Settings") }) }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).padding(16.dp)) {
            if (MiuiHelper.isMiui()) {
                Text("MIUI / HyperOS Detected", style = MaterialTheme.typography.titleMedium)
                Text("Please enable Autostart and No Restrictions in Battery Saver for reliable geofencing.")
                Spacer(modifier = Modifier.height(8.dp))
                Button(onClick = {
                    try {
                        context.startActivity(MiuiHelper.getAutostartIntent())
                    } catch (e: Exception) { }
                }) {
                    Text("Open Autostart Settings")
                }
            }
        }
    }
}
