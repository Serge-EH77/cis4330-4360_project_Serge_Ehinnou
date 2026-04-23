package com.example.auto_weefee

import kotlin.math.sqrt

object FeatureExtractor {

    fun extract(window: List<SensorSample>): FloatArray {
        if (window.isEmpty()) return FloatArray(0)

        val ax = window.map { it.ax }
        val ay = window.map { it.ay }
        val az = window.map { it.az }
        val gx = window.map { it.gx }
        val gy = window.map { it.gy }
        val gz = window.map { it.gz }

        fun mean(list: List<Float>) = list.sum() / list.size
        fun std(list: List<Float>): Float {
            val m = mean(list)
            return sqrt(list.map { (it - m) * (it - m) }.sum() / list.size)
        }
        fun max(list: List<Float>) = list.maxOrNull() ?: 0f

        val accelMag = window.map { sqrt(it.ax*it.ax + it.ay*it.ay + it.az*it.az) }
        val gyroMag = window.map { sqrt(it.gx*it.gx + it.gy*it.gy + it.gz*it.gz) }

        return floatArrayOf(
            mean(accelMag), std(accelMag), max(accelMag),
            mean(gyroMag), std(gyroMag), max(gyroMag)
        )
    }
}