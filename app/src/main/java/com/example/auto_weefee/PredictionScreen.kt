package com.example.auto_weefee


import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun PredictionScreen(viewModel: PredictionViewModel) {
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Activity Prediction", style = MaterialTheme.typography.headlineMedium)

        Spacer(Modifier.height(32.dp))

        if (!uiState.isScanning) {
            Button(onClick = { viewModel.startScanning() }) {
                Text("Start Scanning")
            }
        } else {
            Button(onClick = { viewModel.stopScanningAndPredict() }) {
                Text("Stop Scanning")
            }
        }

        Spacer(Modifier.height(24.dp))

        if (uiState.isScanning) {
            Text("Collecting sensor data…")
        }

        Spacer(Modifier.height(24.dp))

        uiState.prediction?.let {
            Text("Prediction: $it", style = MaterialTheme.typography.headlineLarge)
        }
    }
}