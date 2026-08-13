package com.rohan.raksha.geofence.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.rohan.raksha.geofence.ui.viewmodel.AddLocationViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddLocationScreen(viewModel: AddLocationViewModel, onBack: () -> Unit) {
    var name by remember { mutableStateOf("") }
    var lat by remember { mutableStateOf("0.0") }
    var lng by remember { mutableStateOf("0.0") }
    var exitRadius by remember { mutableStateOf(50f) }
    var arrRadius by remember { mutableStateOf(50f) }
    var shield by remember { mutableStateOf(false) }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Add Location") }) }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).padding(16.dp)) {
            OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Name") }, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(value = lat, onValueChange = { lat = it }, label = { Text("Latitude") }, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(value = lng, onValueChange = { lng = it }, label = { Text("Longitude") }, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(8.dp))
            Text("Exit Radius: ${exitRadius.toInt()}m")
            Slider(value = exitRadius, onValueChange = { exitRadius = it }, valueRange = 50f..1000f)
            Text("Arrival Radius: ${arrRadius.toInt()}m")
            Slider(value = arrRadius, onValueChange = { arrRadius = it }, valueRange = 50f..1000f)
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Activate Shield on Exit")
                Switch(checked = shield, onCheckedChange = { shield = it })
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    viewModel.saveLocation(name, lat.toDoubleOrNull() ?: 0.0, lng.toDoubleOrNull() ?: 0.0, exitRadius, arrRadius, shield) {
                        onBack()
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Save")
            }
        }
    }
}
