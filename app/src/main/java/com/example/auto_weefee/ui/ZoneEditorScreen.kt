package com.example.auto_weefee.ui
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
    var contextType by remember { mutableStateOf("driving") }
    var volume by remember { mutableStateOf(0f) }
    var dndEnabled by remember { mutableStateOf(false) }
    var autoReply by remember { mutableStateOf("") }

    Column(modifier = Modifier.padding(16.dp)) {

        Text("Context Type", style = MaterialTheme.typography.titleMedium)

        Row {
            listOf("driving", "classroom").forEach { type ->
                FilterChip(
                    selected = contextType == type,
                    onClick = { contextType = type },
                    label = { Text(type.replaceFirstChar { it.uppercase() }) }
                )
                Spacer(modifier = Modifier.width(8.dp))
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text("Volume: ${volume.toInt()}%")
        Slider(
            value = volume,
            onValueChange = { volume = it },
            valueRange = 0f..100f
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(checked = dndEnabled, onCheckedChange = { dndEnabled = it })
            Text("Enable Do Not Disturb")
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = autoReply,
            onValueChange = { autoReply = it },
            label = { Text("Auto-reply message") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(onClick = {
            // TODO: Save to database
        }) {
            Text("Save Zone")
        }
    }
}