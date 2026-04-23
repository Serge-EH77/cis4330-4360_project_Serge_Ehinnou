package com.example.auto_weefee

import android.Manifest
import android.app.Application
import android.content.Context
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.net.wifi.WifiManager
import androidx.core.content.ContextCompat
import kotlin.math.abs
import kotlin.math.log10
import kotlin.math.sqrt

data class SensorSample(
    val time: Long,
    val ax: Float, val ay: Float, val az: Float,
    val gx: Float, val gy: Float, val gz: Float,
    val tax: Float, val tay: Float, val taz: Float,
    val micDb: Float,
    val qx: Float, val qy: Float, val qz: Float, val qw: Float,
    val roll: Float, val pitch: Float, val yaw: Float,
    val wifiCount: Int,
    val wifiMaxRssi: Int
)

class SensorCollector(private val app: Application) : SensorEventListener {

    private val sensorManager =
        app.getSystemService(Context.SENSOR_SERVICE) as SensorManager

    private val wifiManager =
        app.getSystemService(Context.WIFI_SERVICE) as WifiManager

    private val samples = mutableListOf<SensorSample>()

    // Raw buffers
    private var accel = FloatArray(3)
    private var gyro = FloatArray(3)
    private var linear = FloatArray(3)
    private var quat = FloatArray(4)
    private var euler = FloatArray(3)

    // Mic
    private var audioRecord: AudioRecord? = null
    private var micThread: Thread? = null
    @Volatile private var micDb: Float = -90f

    fun start() {
        samples.clear()

        // Register sensors safely
        safeRegister(Sensor.TYPE_ACCELEROMETER)
        safeRegister(Sensor.TYPE_GYROSCOPE)
        safeRegister(Sensor.TYPE_LINEAR_ACCELERATION)
        safeRegister(Sensor.TYPE_ROTATION_VECTOR)

        startMicRecording()
    }

    fun stopAndGetWindow(): List<SensorSample> {
        sensorManager.unregisterListener(this)
        stopMicRecording()
        return samples.toList()
    }

    private fun safeRegister(type: Int) {
        val sensor = sensorManager.getDefaultSensor(type)
        if (sensor != null) {
            sensorManager.registerListener(
                this,
                sensor,
                SensorManager.SENSOR_DELAY_GAME
            )
        }
    }

    override fun onSensorChanged(event: SensorEvent) {
        when (event.sensor.type) {
            Sensor.TYPE_ACCELEROMETER -> accel = event.values.clone()
            Sensor.TYPE_GYROSCOPE -> gyro = event.values.clone()
            Sensor.TYPE_LINEAR_ACCELERATION -> linear = event.values.clone()

            Sensor.TYPE_ROTATION_VECTOR -> {
                val rv = event.values.clone()
                val q = FloatArray(4)
                SensorManager.getQuaternionFromVector(q, rv)
                quat = q

                val rotMat = FloatArray(9)
                SensorManager.getRotationMatrixFromVector(rotMat, rv)

                val orientation = FloatArray(3)
                SensorManager.getOrientation(rotMat, orientation)

                euler[0] = orientation[2]
                euler[1] = orientation[1]
                euler[2] = orientation[0]
            }
        }

        // WiFi safe read
        val wifiCount: Int
        val wifiMaxRssi: Int

        if (ContextCompat.checkSelfPermission(app, Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED
        ) {
            val scan = wifiManager.scanResults
            wifiCount = scan.size
            wifiMaxRssi = scan.maxOfOrNull { it.level } ?: -100
        } else {
            wifiCount = 0
            wifiMaxRssi = -100
        }

        samples.add(
            SensorSample(
                time = System.currentTimeMillis(),
                ax = accel[0], ay = accel[1], az = accel[2],
                gx = gyro[0], gy = gyro[1], gz = gyro[2],
                tax = linear[0], tay = linear[1], taz = linear[2],
                micDb = micDb,
                qx = quat[1], qy = quat[2], qz = quat[3], qw = quat[0],
                roll = euler[0], pitch = euler[1], yaw = euler[2],
                wifiCount = wifiCount,
                wifiMaxRssi = wifiMaxRssi
            )
        )
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    // -----------------------------
    // SAFE MICROPHONE
    // -----------------------------
    private fun startMicRecording() {
        if (ContextCompat.checkSelfPermission(app, Manifest.permission.RECORD_AUDIO)
            != PackageManager.PERMISSION_GRANTED
        ) {
            micDb = -90f
            return
        }

        val sampleRate = 8000
        val bufferSize = AudioRecord.getMinBufferSize(
            sampleRate,
            AudioFormat.CHANNEL_IN_MONO,
            AudioFormat.ENCODING_PCM_16BIT
        )

        if (bufferSize <= 0) {
            micDb = -90f
            return
        }

        audioRecord = AudioRecord(
            MediaRecorder.AudioSource.MIC,
            sampleRate,
            AudioFormat.CHANNEL_IN_MONO,
            AudioFormat.ENCODING_PCM_16BIT,
            bufferSize
        )

        if (audioRecord?.state != AudioRecord.STATE_INITIALIZED) {
            micDb = -90f
            return
        }

        audioRecord?.startRecording()

        micThread = Thread {
            val buffer = ShortArray(bufferSize)
            while (!Thread.currentThread().isInterrupted) {
                val read = audioRecord?.read(buffer, 0, buffer.size) ?: 0
                if (read > 0) {
                    val maxAmp = buffer.take(read).maxOf { abs(it.toInt()) }
                    micDb = if (maxAmp > 0) {
                        (20 * log10(maxAmp / 32767f)).coerceAtLeast(-90f)
                    } else -90f
                }
            }
        }
        micThread?.start()
    }

    private fun stopMicRecording() {
        micThread?.interrupt()
        micThread = null

        audioRecord?.stop()
        audioRecord?.release()
        audioRecord = null
    }
}
