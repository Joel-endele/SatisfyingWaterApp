// WaterRenderer.kt
package com.example.water

import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10
import kotlin.math.sin

class WaterRenderer : GLSurfaceView.Renderer {
    private var time = 0f

    // Simple square covering the screen
    private val vertices = floatArrayOf(
        -1f, -1f,
        1f, -1f,
        -1f,  1f,
        1f,  1f
    )
    private val vertexBuffer = java.nio.ByteBuffer.allocateDirect(vertices.size * 4)
        .order(java.nio.ByteOrder.nativeOrder())
        .asFloatBuffer().apply { put(vertices); position(0) }

    private var program = 0
    private var timeHandle = 0

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        GLES20.glClearColor(0f, 0f, 0f, 1f)

        val vertexShaderCode = """
            attribute vec2 a_Position;
            varying vec2 v_Position;
            void main() {
                v_Position = a_Position;
                gl_Position = vec4(a_Position, 0.0, 1.0);
            }
        """.trimIndent()

        val fragmentShaderCode = """
            precision mediump float;
            uniform float u_Time;
            varying vec2 v_Position;

            void main() {
                float wave = sin((v_Position.x + u_Time) * 10.0) * 0.05;
                float y = v_Position.y + wave;
                float intensity = 0.5 + 0.5 * sin((v_Position.x + u_Time)*20.0);
                gl_FragColor = vec4(0.0, 0.3 + intensity*0.7, 0.7 + wave*5.0, 1.0);
            }
        """.trimIndent()

        val vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode)
        val fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode)

        program = GLES20.glCreateProgram().also {
            GLES20.glAttachShader(it, vertexShader)
            GLES20.glAttachShader(it, fragmentShader)
            GLES20.glLinkProgram(it)
        }

        timeHandle = GLES20.glGetUniformLocation(program, "u_Time")
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        GLES20.glViewport(0, 0, width, height)
    }

    override fun onDrawFrame(gl: GL10?) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT)

        GLES20.glUseProgram(program)
        GLES20.glUniform1f(timeHandle, time)

        val positionHandle = GLES20.glGetAttribLocation(program, "a_Position")
        GLES20.glEnableVertexAttribArray(positionHandle)
        GLES20.glVertexAttribPointer(positionHandle, 2, GLES20.GL_FLOAT, false, 0, vertexBuffer)

        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4)
        GLES20.glDisableVertexAttribArray(positionHandle)

        time += 0.02f
    }

    private fun loadShader(type: Int, code: String): Int {
        return GLES20.glCreateShader(type).also { shader ->
            GLES20.glShaderSource(shader, code)
            GLES20.glCompileShader(shader)
        }
    }
}
