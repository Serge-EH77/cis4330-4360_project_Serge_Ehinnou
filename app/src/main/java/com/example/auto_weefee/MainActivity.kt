package com.example.auto_weefee

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.auto_weefee.ui.ZoneEditorScreen
import com.example.auto_weefee.ui.theme.Auto_WeeFeeTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            Auto_WeeFeeTheme {
                val navController = rememberNavController()

                NavHost(
                    navController = navController,
                    startDestination = "home"
                ) {
                    composable("home") {
                        HomeScreen(
                            onAddZone = { navController.navigate("editor") }
                        )
                    }
                    composable("editor") {
                        ZoneEditorScreen()
                    }
                }
            }
        }
    }
}

@Composable
fun HomeScreen() {
    var isScanning by remember { mutableStateOf(false) }
    var result by remember { mutableStateOf<String?>(null) }

    Column(Modifier.padding(16.dp)) {
        Text("Activity Prediction", style = MaterialTheme.typography.headlineMedium)

        Spacer(Modifier.height(32.dp))

        if (!isScanning) {
            Button(onClick = {
                isScanning = true
                startSensorCollection()
            }) {
                Text("Start Scanning")
            }
        } else {
            Button(onClick = {
                isScanning = false
                val features = stopSensorCollectionAndExtractFeatures()
                result = runModel(features)
            }) {
                Text("Stop Scanning")
            }
        }

        Spacer(Modifier.height(32.dp))

        result?.let {
            Text("Prediction: $it", style = MaterialTheme.typography.headlineLarge)
        }
    }
}


