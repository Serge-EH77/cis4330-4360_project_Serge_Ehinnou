package com.example.auto_weefee.ui
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue

@Composable
fun ZoneEditorScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Create Zone", style = MaterialTheme.typography.headlineMedium)

        Spacer(Modifier.height(16.dp))

        var ssid by remember { mutableStateOf("") }
        TextField(
            value = ssid,
            onValueChange = { ssid = it },
            label = { Text("SSID") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(16.dp))

        var volume by remember { mutableStateOf(0f) }
        Text("Volume: ${volume.toInt()}%")
        Slider(
            value = volume,
            onValueChange = { volume = it },
            valueRange = 0f..100f
        )

        Spacer(Modifier.height(16.dp))

        var dndEnabled by remember { mutableStateOf(false) }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(checked = dndEnabled, onCheckedChange = { dndEnabled = it })
            Text("Enable Do Not Disturb")
        }

        Spacer(Modifier.height(16.dp))

        var autoReply by remember { mutableStateOf("") }
        TextField(
            value = autoReply,
            onValueChange = { autoReply = it },
            label = { Text("Auto-reply message") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(24.dp))

        Button(
            onClick = { /* save logic */ },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Save Zone")
        }
    }
}