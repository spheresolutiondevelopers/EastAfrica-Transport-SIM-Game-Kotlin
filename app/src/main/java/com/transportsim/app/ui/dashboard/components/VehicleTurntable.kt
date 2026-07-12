package com.transportsim.app.ui.dashboard.components

import android.content.Context
import android.graphics.SurfaceTexture
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.unit.dp
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

/**
 * A 3D vehicle turntable that displays a rotating 3D model.
 * Supports drag-to-rotate and auto-rotation.
 */
@Composable
fun VehicleTurntable(
    vehicleId: Int?,
    isOwned: Boolean = true,
    onRotate: ((Float) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var turntableView by remember { mutableStateOf<TurntableGLView?>(null) }
    var isAutoRotate by remember { mutableStateOf(true) }
    var rotationAngle by remember { mutableStateOf(0f) }
    
    Box(modifier = modifier) {
        AndroidView(
            factory = { ctx ->
                TurntableGLView(ctx).apply {
                    turntableView = this
                    // Set initial vehicle (in a real app, load model based on vehicleId)
                    setVehicle(vehicleId ?: 0)
                    setAutoRotate(isAutoRotate)
                }
            },
            update = { view ->
                // Update if vehicle changes
                view.setVehicle(vehicleId ?: 0)
                view.setAutoRotate(isAutoRotate)
            },
            modifier = Modifier.fillMaxSize()
        )
        
        // Lock overlay if not owned
        if (!isOwned) {
            LockOverlay()
        }
        
        // Controls overlay (auto-rotate toggle, prev/next buttons)
        TurntableControls(
            isAutoRotate = isAutoRotate,
            onToggleAutoRotate = {
                isAutoRotate = !isAutoRotate
                turntableView?.setAutoRotate(isAutoRotate)
            },
            onPrev = {
                // Navigate previous vehicle
            },
            onNext = {
                // Navigate next vehicle
            },
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(8.dp)
        )
    }
}

@Composable
private fun TurntableControls(
    isAutoRotate: Boolean,
    onToggleAutoRotate: () -> Unit,
    onPrev: () -> Unit,
    onNext: () -> Unit,
    modifier: Modifier = Modifier
) {
    androidx.compose.foundation.layout.Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        // Auto-rotate toggle button
        androidx.compose.material3.IconButton(
            onClick = onToggleAutoRotate,
            modifier = Modifier.size(28.dp)
        ) {
            androidx.compose.material3.Icon(
                imageVector = if (isAutoRotate) 
                    androidx.compose.material.icons.Icons.Default.PlayArrow 
                    else androidx.compose.material.icons.Icons.Default.Pause,
                contentDescription = if (isAutoRotate) "Pause rotation" else "Auto-rotate",
                tint = Cyan
            )
        }
        // Previous button
        androidx.compose.material3.IconButton(
            onClick = onPrev,
            modifier = Modifier.size(28.dp)
        ) {
            androidx.compose.material3.Text("‹", color = Cyan, fontSize = 18.sp)
        }
        // Next button
        androidx.compose.material3.IconButton(
            onClick = onNext,
            modifier = Modifier.size(28.dp)
        ) {
            androidx.compose.material3.Text("›", color = Cyan, fontSize = 18.sp)
        }
    }
}

@Composable
private fun LockOverlay() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.6f)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text("🔒", fontSize = 24.sp)
            Text(
                text = "LOCKED",
                style = MaterialTheme.typography.labelLarge,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Purchase in Fleet",
                style = MaterialTheme.typography.labelSmall,
                color = Gold
            )
        }
    }
}

/**
 * Custom GLSurfaceView for 3D vehicle rendering.
 */
class TurntableGLView(context: Context, attrs: AttributeSet? = null) : GLSurfaceView(context, attrs) {
    
    private var renderer: TurntableRenderer? = null
    private var lastX: Float = 0f
    private var lastY: Float = 0f
    private var isDragging = false
    
    init {
        setEGLContextClientVersion(3)
        renderer = TurntableRenderer().apply {
            setVehicle(0)
        }
        setRenderer(renderer)
        renderMode = RENDERMODE_WHEN_DIRTY
    }
    
    fun setVehicle(vehicleId: Int) {
        renderer?.setVehicle(vehicleId)
        requestRender()
    }
    
    fun setAutoRotate(auto: Boolean) {
        renderer?.setAutoRotate(auto)
        if (auto) {
            renderMode = RENDERMODE_CONTINUOUSLY
        } else {
            renderMode = RENDERMODE_WHEN_DIRTY
        }
    }
    
    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                isDragging = true
                lastX = event.x
                lastY = event.y
                renderer?.setAutoRotate(false)
                setAutoRotate(false)
                return true
            }
            MotionEvent.ACTION_MOVE -> {
                if (isDragging) {
                    val dx = event.x - lastX
                    val dy = event.y - lastY
                    renderer?.rotate(dx * 0.5f, dy * 0.5f)
                    lastX = event.x
                    lastY = event.y
                    requestRender()
                }
                return true
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                isDragging = false
                // Optionally re-enable auto-rotate after a delay
                // For now, user must toggle manually
                return true
            }
        }
        return super.onTouchEvent(event)
    }
}

/**
 * OpenGL renderer for the turntable.
 * This is a simplified implementation that draws a colored cube.
 * In production, replace with glTF model loading and rendering.
 */
class TurntableRenderer : GLSurfaceView.Renderer {
    
    private var vehicleId: Int = 0
    private var autoRotate: Boolean = true
    private var angleX: Float = 20f
    private var angleY: Float = 0f
    
    // Shader program handle
    private var program: Int = 0
    
    // Vertex buffer
    private var vertexBuffer: FloatBuffer? = null
    private var colorBuffer: FloatBuffer? = null
    
    // Matrix
    private val viewMatrix = FloatArray(16)
    private val projMatrix = FloatArray(16)
    private val modelMatrix = FloatArray(16)
    private val mvpMatrix = FloatArray(16)
    
    // Colors for different vehicle types
    private val vehicleColors = mapOf(
        0 to floatArrayOf(0.3f, 0.6f, 0.9f, 1.0f),  // Bus - blue
        1 to floatArrayOf(0.9f, 0.7f, 0.2f, 1.0f),  // Matatu - gold
        2 to floatArrayOf(0.2f, 0.8f, 0.4f, 1.0f),  // Pickup - green
        3 to floatArrayOf(0.9f, 0.4f, 0.1f, 1.0f),  // Lorry - orange
        4 to floatArrayOf(0.6f, 0.3f, 0.8f, 1.0f),  // Boda - purple
        5 to floatArrayOf(1.0f, 0.6f, 0.8f, 1.0f)   // Taxi - pink
    )
    
    private val cubeVertices = floatArrayOf(
        // Front face
        -0.5f, -0.5f,  0.5f,
         0.5f, -0.5f,  0.5f,
         0.5f,  0.5f,  0.5f,
        -0.5f,  0.5f,  0.5f,
        // Back face
        -0.5f, -0.5f, -0.5f,
         0.5f, -0.5f, -0.5f,
         0.5f,  0.5f, -0.5f,
        -0.5f,  0.5f, -0.5f
    )
    
    private val cubeIndices = shortArrayOf(
        // Front
        0, 1, 2, 2, 3, 0,
        // Top
        3, 2, 6, 6, 7, 3,
        // Back
        7, 6, 5, 5, 4, 7,
        // Bottom
        4, 5, 1, 1, 0, 4,
        // Left
        4, 0, 3, 3, 7, 4,
        // Right
        1, 5, 6, 6, 2, 1
    )
    
    private val vertexShaderCode = """
        attribute vec4 vPosition;
        attribute vec4 vColor;
        uniform mat4 uMVPMatrix;
        varying vec4 vColorOut;
        void main() {
            gl_Position = uMVPMatrix * vPosition;
            vColorOut = vColor;
        }
    """.trimIndent()
    
    private val fragmentShaderCode = """
        precision mediump float;
        varying vec4 vColorOut;
        void main() {
            gl_FragColor = vColorOut;
        }
    """.trimIndent()
    
    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 0.0f)
        GLES20.glEnable(GLES20.GL_DEPTH_TEST)
        GLES20.glEnable(GLES20.GL_CULL_FACE)
        
        // Create shader program
        program = createProgram()
        
        // Set up vertex buffer
        vertexBuffer = ByteBuffer.allocateDirect(cubeVertices.size * 4)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer()
            .apply {
                put(cubeVertices)
                position(0)
            }
        
        // Color buffer will be set per frame
    }
    
    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        GLES20.glViewport(0, 0, width, height)
        val aspect = width.toFloat() / height.toFloat()
        android.opengl.Matrix.frustumM(projMatrix, 0, -aspect, aspect, -1f, 1f, 3f, 7f)
        android.opengl.Matrix.setLookAtM(viewMatrix, 0, 0f, 0f, 5f, 0f, 0f, 0f, 0f, 1f, 0f)
    }
    
    override fun onDrawFrame(gl: GL10?) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT or GLES20.GL_DEPTH_BUFFER_BIT)
        
        // Update rotation
        if (autoRotate) {
            angleY += 0.8f
        }
        
        // Get color for this vehicle
        val color = vehicleColors[vehicleId] ?: floatArrayOf(0.5f, 0.5f, 0.5f, 1.0f)
        
        // Set color buffer
        val colors = FloatArray(cubeVertices.size) { i ->
            when (i % 4) {
                0 -> color[0]
                1 -> color[1]
                2 -> color[2]
                3 -> color[3]
            }
        }
        colorBuffer = ByteBuffer.allocateDirect(colors.size * 4)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer()
            .apply {
                put(colors)
                position(0)
            }
        
        // Model matrix
        android.opengl.Matrix.setIdentityM(modelMatrix, 0)
        android.opengl.Matrix.rotateM(modelMatrix, 0, angleX, 1f, 0f, 0f)
        android.opengl.Matrix.rotateM(modelMatrix, 0, angleY, 0f, 1f, 0f)
        
        // MVP
        android.opengl.Matrix.multiplyMM(mvpMatrix, 0, viewMatrix, 0, modelMatrix, 0)
        android.opengl.Matrix.multiplyMM(mvpMatrix, 0, projMatrix, 0, mvpMatrix, 0)
        
        // Use shader
        GLES20.glUseProgram(program)
        
        // Vertex attributes
        val positionHandle = GLES20.glGetAttribLocation(program, "vPosition")
        GLES20.glEnableVertexAttribArray(positionHandle)
        GLES20.glVertexAttribPointer(positionHandle, 3, GLES20.GL_FLOAT, false, 0, vertexBuffer)
        
        val colorHandle = GLES20.glGetAttribLocation(program, "vColor")
        GLES20.glEnableVertexAttribArray(colorHandle)
        GLES20.glVertexAttribPointer(colorHandle, 4, GLES20.GL_FLOAT, false, 0, colorBuffer)
        
        // MVP uniform
        val mvpHandle = GLES20.glGetUniformLocation(program, "uMVPMatrix")
        GLES20.glUniformMatrix4fv(mvpHandle, 1, false, mvpMatrix, 0)
        
        // Draw
        GLES20.glDrawElements(GLES20.GL_TRIANGLES, cubeIndices.size, GLES20.GL_UNSIGNED_SHORT, 
            ByteBuffer.allocateDirect(cubeIndices.size * 2)
                .order(ByteOrder.nativeOrder())
                .asShortBuffer()
                .apply {
                    put(cubeIndices)
                    position(0)
                })
        
        GLES20.glDisableVertexAttribArray(positionHandle)
        GLES20.glDisableVertexAttribArray(colorHandle)
    }
    
    private fun createProgram(): Int {
        val vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode)
        val fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode)
        val program = GLES20.glCreateProgram()
        GLES20.glAttachShader(program, vertexShader)
        GLES20.glAttachShader(program, fragmentShader)
        GLES20.glLinkProgram(program)
        return program
    }
    
    private fun loadShader(type: Int, shaderCode: String): Int {
        val shader = GLES20.glCreateShader(type)
        GLES20.glShaderSource(shader, shaderCode)
        GLES20.glCompileShader(shader)
        return shader
    }
    
    fun setVehicle(id: Int) {
        vehicleId = id
    }
    
    fun setAutoRotate(auto: Boolean) {
        autoRotate = auto
    }
    
    fun rotate(dx: Float, dy: Float) {
        angleY += dx
        angleX = (angleX + dy).coerceIn(-80f, 80f)
    }
}