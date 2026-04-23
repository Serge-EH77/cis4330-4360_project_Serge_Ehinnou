package com.example.auto_weefee

import android.content.Context
import ai.onnxruntime.OnnxTensor
import ai.onnxruntime.OrtEnvironment
import ai.onnxruntime.OrtSession
import java.nio.FloatBuffer

object ModelRunner {

    private lateinit var env: OrtEnvironment
    private lateinit var session: OrtSession

    fun initialize(context: Context) {
        env = OrtEnvironment.getEnvironment()
        val modelBytes = context.assets.open("context_model.onnx").readBytes()
        session = env.createSession(modelBytes)
    }

    fun predict(features: FloatArray): String {
        if (!::session.isInitialized) return "unknown"

        val inputName = session.inputNames.iterator().next()
        val shape = longArrayOf(1, features.size.toLong())

        val fb = FloatBuffer.wrap(features)
        val tensor = OnnxTensor.createTensor(env, fb, shape)

        val results = session.run(mapOf(inputName to tensor))

        @Suppress("UNCHECKED_CAST")
        val output = results[0].value as Array<FloatArray>

        val probs = output[0]
        val classroomProb = probs[0]
        val drivingProb = probs[1]

        return if (drivingProb > classroomProb) "driving" else "classroom"
    }
}
