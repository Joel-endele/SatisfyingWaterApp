
package com.example.water

import android.content.Context
import android.graphics.*
import android.view.View
import kotlin.math.*

class WaterView(ctx: Context) : View(ctx) {

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val drops = MutableList(700) {
        Drop(
            x = Math.random().toFloat() * 800f,
            y = Math.random().toFloat() * 1400f
        )
    }

    private var gx = 0f
    private var gy = 0f

    // Wasser-Parameter (spiel damit!)
    private val gravityStrength = 0.35f
    private val damping = 0.985f
    private val viscosity = 0.08f
    private val cohesionDistance = 28f

    fun setGravity(x: Float, y: Float) {
        gx = x * gravityStrength
        gy = y * gravityStrength
    }

    override fun onDraw(c: Canvas) {
        c.drawColor(Color.rgb(6, 14, 30))
        paint.color = Color.rgb(80, 200, 255)

        // Nachbarschaftseinfluss (Wasser klebt zusammen)
        for (i in drops.indices) {
            val a = drops[i]
            var fx = 0f
            var fy = 0f

            for (j in drops.indices step 6) {
                if (i == j) continue
                val b = drops[j]
                val dx = b.x - a.x
                val dy = b.y - a.y
                val dist = sqrt(dx*dx + dy*dy)

                if (dist in 1f..cohesionDistance) {
                    val force = (cohesionDistance - dist) / cohesionDistance
                    fx += dx * force * viscosity
                    fy += dy * force * viscosity
                }
            }

            a.vx += fx + gx
            a.vy += fy + gy
        }

        for (d in drops) {
            d.vx *= damping
            d.vy *= damping

            d.x += d.vx
            d.y += d.vy

            // Ränder → weiches Schwappen
            if (d.x < 0f) { d.x = 0f; d.vx *= -0.5f }
            if (d.x > width) { d.x = width.toFloat(); d.vx *= -0.5f }
            if (d.y < 0f) { d.y = 0f; d.vy *= -0.5f }
            if (d.y > height) { d.y = height.toFloat(); d.vy *= -0.5f }

            c.drawCircle(d.x, d.y, 4.8f, paint)
        }

        invalidate()
    }

    data class Drop(
        var x: Float,
        var y: Float,
        var vx: Float = 0f,
        var vy: Float = 0f
    )
}
