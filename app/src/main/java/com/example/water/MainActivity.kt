
package com.example.water

import android.app.Activity
import android.os.Bundle
import android.hardware.*

class MainActivity : Activity(), SensorEventListener {
    private lateinit var view: WaterView
    private lateinit var sensorManager: SensorManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        view = WaterView(this)
        setContentView(view)
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
    }

    override fun onResume() {
        super.onResume()
        sensorManager.registerListener(
            this,
            sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
            SensorManager.SENSOR_DELAY_GAME
        )
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(e: SensorEvent) {
        view.setGravity(-e.values[0], e.values[1])
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
}
