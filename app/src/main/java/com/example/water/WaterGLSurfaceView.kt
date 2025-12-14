package com.example.water

import android.content.Context
import android.opengl.GLSurfaceView

class WaterGLSurfaceView(context: Context) : GLSurfaceView(context) {

    val renderer: WaterRenderer

    init {
        setEGLContextClientVersion(2)
        renderer = WaterRenderer()
        setRenderer(renderer)
        renderMode = RENDERMODE_CONTINUOUSLY
    }
}
