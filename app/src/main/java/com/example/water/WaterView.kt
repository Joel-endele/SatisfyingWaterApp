
package com.example.water

import android.content.Context
import android.graphics.*
import android.view.View
import kotlin.math.*

class WaterView(ctx: Context) : View(ctx) {

    private val columns = 180
    private val surface = FloatArray(columns)
    private val velocity = FloatArray(columns)


    private var gravityX = 0f
    private var gravityY = 0f

    // Wasser-Feeling
    private val stiffness = 0.025f      // Oberflächenspannung
    private val damping = 0.985f        // Energieverlust
    private val spread = 0.25f          // Wellenausbreitung

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.rgb(60, 180, 255)
        style = Paint.Style.FILL
    }

    fun setGravity(x: Float, y: Float) {
        gravityX = x
        gravityY = y
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        for (i in surface.indices) {
            surface[i] = h * 0.6f
            velocity[i] = 0f
        }
    }

    override fun onDraw(c: Canvas) {
        c.drawColor(Color.rgb(8, 16, 32))

        for (i in 1 until columns - 1) {
            val target = (surface[i - 1] + surface[i + 1]) * 0.5f
            val force = (target - surface[i]) * stiffness
            velocity[i] += force
            velocity[i] *= damping
            surface[i] += velocity[i]
        }

        val tilt = gravityX * 8f
        for (i in surface.indices) {
            surface[i] += tilt * ((i.toFloat() / columns) - 0.5f)
        }

        repeat(3) {
            for (i in 1 until columns - 1) {
                val d = spread * (surface[i] - surface[i + 1])
                surface[i] -= d
                surface[i + 1] += d
            }
            for (i in columns - 2 downTo 1) {
                val d = spread * (surface[i] - surface[i - 1])
                surface[i] -= d
                surface[i - 1] += d
            }
        }

        val path = Path()
        val step = width.toFloat() / (columns - 1)

        path.moveTo(0f, surface[0])
        for (i in 1 until columns) {
            path.lineTo(i * step, surface[i])
        }
        path.lineTo(width.toFloat(), this.height.toFloat())
        path.lineTo(0f, this.height.toFloat())
        path.close()

        c.drawPath(path, paint)
        invalidate()
    }
}
