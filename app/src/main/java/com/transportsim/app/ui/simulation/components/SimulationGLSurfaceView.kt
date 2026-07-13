package com.transportsim.app.ui.simulation.components

import android.content.Context
import android.opengl.GLSurfaceView
import android.view.MotionEvent
import com.transportsim.bridge.SimulationSession
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class SimulationGLSurfaceView(context: Context) : GLSurfaceView(context) {
    
    private var session: SimulationSession? = null
    private val renderer = SimulationRenderer()
    
    init {
        setEGLContextClientVersion(3)
        setRenderer(renderer)
        renderMode = RENDERMODE_CONTINUOUSLY
    }
    
    fun setSession(session: SimulationSession?) {
        this.session = session
        if (session != null) {
            renderer.setSession(session)
            // Initialize renderer with the session
            session.initRenderer(this, width, height, context.assets)
        }
    }
    
    override fun onTouchEvent(event: MotionEvent): Boolean {
        // Pass touch events to native engine for camera control
        // In a real implementation, this would handle drag-to-look
        return super.onTouchEvent(event)
    }
    
    private inner class SimulationRenderer : GLSurfaceView.Renderer {
        
        private var session: SimulationSession? = null
        
        fun setSession(session: SimulationSession?) {
            this.session = session
        }
        
        override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
            // Native renderer will handle this via JNI
        }
        
        override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
            session?.resizeRenderer(width, height)
        }
        
        override fun onDrawFrame(gl: GL10?) {
            session?.renderFrame()
        }
    }
}