package com.example.auto_weefee

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class PredictionUiState(
    val isScanning: Boolean = false,
    val prediction: String? = null
)

class PredictionViewModel(app: Application) : AndroidViewModel(app) {

    private val _uiState = MutableStateFlow(PredictionUiState())
    val uiState: StateFlow<PredictionUiState> = _uiState

    private val sensorCollector = SensorCollector(app)
    init {
        ModelRunner.initialize(app)
    }

    fun startScanning() {
        _uiState.value = PredictionUiState(isScanning = true)
        sensorCollector.start()
    }

    fun stopScanningAndPredict() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isScanning = false)

            val window = sensorCollector.stopAndGetWindow()

            val features = withContext(Dispatchers.Default) {
                FeatureExtractor.extract(window)
            }

            val prediction = withContext(Dispatchers.Default) {
                ModelRunner.predict(features)
            }

            _uiState.value = _uiState.value.copy(prediction = prediction)
        }
    }
}
