package com.example.auto_weefee

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
fun HomeScreen(onAddZone: () -> Unit) {
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = onAddZone) {
                Icon(Icons.Default.Add, contentDescription = "Add Zone")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            Text(
                text = "Auto WeeFee Zones",
                style = MaterialTheme.typography.headlineMedium
            )

            Spacer(modifier = Modifier.height(45.dp))

            Text("No zones yet", style = MaterialTheme.typography.bodyLarge)
        }
    }
}