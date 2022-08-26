package com.example.quest

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.quest.databinding.ActivityCompassBinding

class CompassActivity : AppCompatActivity() ,SensorEventListener{
    lateinit var sensorManager: SensorManager
    lateinit var accelerometer: Sensor
    lateinit var magnetometer: Sensor

    lateinit var binding: ActivityCompassBinding

    var gravity: MutableList<Float> = mutableListOf()
    var magneticField: MutableList<Float> = mutableListOf()
    var rotationMatrix: FloatArray = FloatArray(9)
    var orientation: FloatArray  = FloatArray(3)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCompassBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)
        sensorManager.registerListener(this,accelerometer,SensorManager.SENSOR_DELAY_NORMAL)
        sensorManager.registerListener(this,magnetometer,SensorManager.SENSOR_DELAY_NORMAL)

    }

    override fun onSensorChanged(event: SensorEvent?) {

        if(event?.sensor == accelerometer){
            gravity.clear()
            gravity.addAll(event.values.toList())
        }
        if(event?.sensor == magnetometer){
            magneticField.clear()
            magneticField.addAll(event.values.toList())
        }
        if(gravity.isNotEmpty() && magneticField.isNotEmpty()) {
            SensorManager.getRotationMatrix(
                rotationMatrix, null, gravity.toFloatArray(), magneticField
                    .toFloatArray()
            )
            SensorManager.getOrientation(rotationMatrix, orientation)
            binding.imageView2.rotation = (-orientation[0] * 180 / 3.14159).toFloat()
        }
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {

    }
}