package com.example.auto_weefee

import android.app.Application
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager

data class SensorSample(
    val timestamp: Long,
    val ax: Float,
    val ay: Float,
    val az: Float,
    val gx: Float,
    val gy: Float,
    val gz: Float
)

class SensorCollector(private val app: Application) : SensorEventListener {

    private val sensorManager =
        app.getSystemService(Application.SENSOR_SERVICE) as SensorManager

    private val samples = mutableListOf<SensorSample>()

    private var lastAccel = FloatArray(3)
    private var lastGyro = FloatArray(3)

    fun start() {
        samples.clear()

        sensorManager.registerListener(
            this,
            sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
            SensorManager.SENSOR_DELAY_GAME
        )

        sensorManager.registerListener(
            this,
            sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE),
            SensorManager.SENSOR_DELAY_GAME
        )
    }

    fun stopAndGetWindow(): List<SensorSample> {
        sensorManager.unregisterListener(this)
        return samples.toList()
    }

    override fun onSensorChanged(event: SensorEvent) {
        when (event.sensor.type) {
            Sensor.TYPE_ACCELEROMETER -> {
                lastAccel = event.values.clone()
            }
            Sensor.TYPE_GYROSCOPE -> {
                lastGyro = event.values.clone()
            }
        }

        samples.add(
            SensorSample(
                timestamp = System.currentTimeMillis(),
                ax = lastAccel[0],
                ay = lastAccel[1],
                az = lastAccel[2],
                gx = lastGyro[0],
                gy = lastGyro[1],
                gz = lastGyro[2]
            )
        )
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
}