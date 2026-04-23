package com.example.auto_weefee

import kotlin.math.sqrt

object FeatureExtractor {

    fun extract(window: List<SensorSample>): FloatArray {
        if (window.isEmpty()) return FloatArray(36)

        // Helpers
        fun mean(list: List<Float>): Float =
            if (list.isEmpty()) 0f else list.sum() / list.size

        fun std(list: List<Float>): Float {
            if (list.isEmpty()) return 0f
            val m = mean(list)
            val varSum = list.fold(0f) { acc, v ->
                val d = v - m
                acc + d * d
            }
            return sqrt(varSum / list.size)
        }

        fun max(list: List<Float>): Float =
            list.maxOrNull() ?: 0f

        fun meanInt(list: List<Int>): Float =
            if (list.isEmpty()) 0f else list.sum().toFloat() / list.size

        fun maxInt(list: List<Int>): Float =
            list.maxOrNull()?.toFloat() ?: 0f

        // Full window series
        val ax = window.map { it.ax }
        val ay = window.map { it.ay }
        val az = window.map { it.az }

        val gx = window.map { it.gx }
        val gy = window.map { it.gy }
        val gz = window.map { it.gz }

        val tax = window.map { it.tax }
        val tay = window.map { it.tay }
        val taz = window.map { it.taz }

        val accelMag = window.map { sqrt(it.ax * it.ax + it.ay * it.ay + it.az * it.az) }
        val gyroMag = window.map { sqrt(it.gx * it.gx + it.gy * it.gy + it.gz * it.gz) }
        val totalMag = window.map { sqrt(it.tax * it.tax + it.tay * it.tay + it.taz * it.taz) }

        val micDb = window.map { it.micDb }

        val qx = window.map { it.qx }
        val qy = window.map { it.qy }
        val qz = window.map { it.qz }
        val qw = window.map { it.qw }

        val roll = window.map { it.roll }
        val pitch = window.map { it.pitch }
        val yaw = window.map { it.yaw }

        val wifiCount = window.map { it.wifiCount }
        val wifiMaxRssi = window.map { it.wifiMaxRssi }

        // 1‑second orientation window
        val lastTs = window.last().time
        val oneSecWindow = window.filter { it.time >= lastTs - 1000 }

        val qx1s = oneSecWindow.map { it.qx }
        val qy1s = oneSecWindow.map { it.qy }
        val qz1s = oneSecWindow.map { it.qz }
        val qw1s = oneSecWindow.map { it.qw }

        val roll1s = oneSecWindow.map { it.roll }
        val pitch1s = oneSecWindow.map { it.pitch }
        val yaw1s = oneSecWindow.map { it.yaw }

        // Return EXACT 36 features in EXACT CSV order
        return floatArrayOf(
            // 1–3 accelerometer_x/y/z_mean
            mean(ax),
            mean(ay),
            mean(az),

            // 4–6 accelerometer magnitude mean/std/max
            mean(accelMag),
            std(accelMag),
            max(accelMag),

            // 7–9 gyroscope_x/y/z_mean
            mean(gx),
            mean(gy),
            mean(gz),

            // 10–12 gyroscope magnitude mean/std/max
            mean(gyroMag),
            std(gyroMag),
            max(gyroMag),

            // 13–14 microphone mean/max
            mean(micDb),
            max(micDb),

            // 15–18 orientation quaternion mean
            mean(qx),
            mean(qy),
            mean(qz),
            mean(qw),

            // 19–21 orientation roll/pitch/yaw mean
            mean(roll),
            mean(pitch),
            mean(yaw),

            // 22–25 orientation 1s quaternion mean
            mean(qx1s),
            mean(qy1s),
            mean(qz1s),
            mean(qw1s),

            // 26–28 orientation 1s roll/pitch/yaw mean
            mean(roll1s),
            mean(pitch1s),
            mean(yaw1s),

            // 29–31 total acceleration x/y/z mean
            mean(tax),
            mean(tay),
            mean(taz),

            // 32–34 total acceleration magnitude mean/std/max
            mean(totalMag),
            std(totalMag),
            max(totalMag),

            // 35–36 wifi count + wifi max rssi
            meanInt(wifiCount),
            maxInt(wifiMaxRssi)
        )
    }
}
