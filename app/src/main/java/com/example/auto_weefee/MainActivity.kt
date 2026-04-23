package com.example.auto_weefee

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelProvider
import com.example.auto_weefee.ui.theme.Auto_WeeFeeTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val viewModel = ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory.getInstance(application)
        )[PredictionViewModel::class.java]

        setContent {
            Auto_WeeFeeTheme {
                Surface(modifier = Modifier) {
                    PredictionScreen(viewModel)
                }
            }
        }
    }
}