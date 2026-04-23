package com.example.auto_weefee

object ModelRunner {

    // Replace this with ONNX inference later
    fun predict(features: FloatArray): String {
        if (features.isEmpty()) return "unknown"

        val accelMean = features[0]
        return if (accelMean > 5f) "driving" else "classroom"
    }
}