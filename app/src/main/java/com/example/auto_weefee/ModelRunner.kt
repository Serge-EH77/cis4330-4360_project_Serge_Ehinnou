package com.example.auto_weefee

import android.content.Context
import android.util.Log
import ai.onnxruntime.OnnxTensor
import ai.onnxruntime.OrtEnvironment
import ai.onnxruntime.OrtSession
import java.nio.FloatBuffer

object ModelRunner {

    private lateinit var env: OrtEnvironment
    private lateinit var session: OrtSession

    fun initialize(context: Context) {
        try {
            env = OrtEnvironment.getEnvironment()
            val modelBytes = context.assets.open("context_model.onnx").readBytes()
            session = env.createSession(modelBytes)

            println("ONNX model loaded successfully!")
            println("INPUTS: ${session.inputNames}")
            val inputName = session.inputNames.first()
            println("INPUT INFO: ${session.inputInfo[inputName]}")
            println("OUTPUT INFO: ${session.outputInfo}")

        } catch (e: Exception) {
            println("ONNX load failed: ${e.message}")
        }
    }

    private fun decodeOnnxString(value: Any?): String? {
        if (value is Array<*>) {
            val first = value.firstOrNull() ?: return null

            if (first is String) return first
            if (first is ByteArray) return first.toString(Charsets.UTF_8)
            if (first is java.nio.ByteBuffer) {
                val bytes = ByteArray(first.remaining())
                first.get(bytes)
                return String(bytes, Charsets.UTF_8)
            }

            if (first is Array<*>) {
                val inner = first.firstOrNull()
                if (inner is java.nio.ByteBuffer) {
                    val bytes = ByteArray(inner.remaining())
                    inner.get(bytes)
                    return String(bytes, Charsets.UTF_8)
                }
            }
        }

        return null
    }

    fun predict(features: FloatArray): String {
        if (!::session.isInitialized) return "unknown"

        val inputName = session.inputNames.first()
        val shape = longArrayOf(1, features.size.toLong())

        val fb = FloatBuffer.wrap(features)
        val tensor = OnnxTensor.createTensor(env, fb, shape)

        val results = session.run(mapOf(inputName to tensor))
        val raw = results[0].value

        // skl2onnx RandomForestClassifier always outputs Array<String>
        if (raw is Array<*>) {
            val first = raw.firstOrNull()
            if (first is String) {
                return first   // "driving" or "classroom"
            }
        }

        return "unknown"
    }


}
