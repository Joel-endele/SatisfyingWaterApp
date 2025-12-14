// WaterGLSurfaceView.kt
package com.example.water

import android.content.Context
import android.opengl.GLSurfaceView
import android.util.AttributeSet

class WaterGLSurfaceView(context: Context, attrs: AttributeSet? = null) : GLSurfaceView(context, attrs) {
    private val renderer: WaterRenderer

    init {
        setEGLContextClientVersion(2)
        renderer = WaterRenderer()
        setRenderer(renderer)
        renderMode = RENDERMODE_CONTINUOUSLY
    }
}
