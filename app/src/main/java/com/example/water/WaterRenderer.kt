package com.example.water

import android.opengl.GLES20
import android.opengl.GLSurfaceView
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10
import kotlin.math.sign

class WaterRenderer : GLSurfaceView.Renderer {

    private val points = 120
    private val height = FloatArray(points)
    private val velocity = FloatArray(points)

    private var tiltX = 0f

    fun setTilt(x: Float, y: Float) {
        tiltX = -x   // Spiegelung FIX
    }

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        GLES20.glClearColor(0.02f, 0.08f, 0.15f, 1f)
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        GLES20.glViewport(0, 0, width, height)
    }

    override fun onDrawFrame(gl: GL10?) {
        updateWater()
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT)
        drawWater()
    }

    // ======================
    // 🌊 Wasserphysik
    // ======================
    private fun updateWater() {
        val gravity = tiltX * 0.015f
        val tension = 0.025f
        val damping = 0.985f

        for (i in 0 until points) {
            velocity[i] += gravity
            velocity[i] *= damping
            height[i] += velocity[i]
        }

        // Wellen-Ausbreitung
        repeat(8) {
            for (i in 1 until points - 1) {
                val diff = height[i - 1] + height[i + 1] - 2f * height[i]
                velocity[i] += diff * tension
            }
        }
    }

    // ======================
    // 🎨 Rendering (einfach, aber korrekt)
    // ======================
    private fun drawWater() {
        val vertices = FloatArray(points * 2)

        for (i in 0 until points) {
            val x = -1f + 2f * i / (points - 1)
            val y = height[i]
            vertices[i * 2] = x
            vertices[i * 2 + 1] = y
        }

        val buffer = java.nio.ByteBuffer
            .allocateDirect(vertices.size * 4)
            .order(java.nio.ByteOrder.nativeOrder())
            .asFloatBuffer()

        buffer.put(vertices).position(0)

        val vertexShader = """
            attribute vec2 aPos;
            void main() {
                gl_Position = vec4(aPos.x, aPos.y - 0.4, 0.0, 1.0);
            }
        """

        val fragmentShader = """
            precision mediump float;
            void main() {
                gl_FragColor = vec4(0.1, 0.5, 0.9, 1.0);
            }
        """

        val program = GLES20.glCreateProgram()
        val vs = loadShader(GLES20.GL_VERTEX_SHADER, vertexShader)
        val fs = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShader)

        GLES20.glAttachShader(program, vs)
        GLES20.glAttachShader(program, fs)
        GLES20.glLinkProgram(program)
        GLES20.glUseProgram(program)

        val posHandle = GLES20.glGetAttribLocation(program, "aPos")
        GLES20.glEnableVertexAttribArray(posHandle)
        GLES20.glVertexAttribPointer(posHandle, 2, GLES20.GL_FLOAT, false, 0, buffer)

        GLES20.glDrawArrays(GLES20.GL_LINE_STRIP, 0, points)

        GLES20.glDisableVertexAttribArray(posHandle)
    }

    private fun loadShader(type: Int, code: String): Int {
        val shader = GLES20.glCreateShader(type)
        GLES20.glShaderSource(shader, code)
        GLES20.glCompileShader(shader)
        return shader
    }
}
