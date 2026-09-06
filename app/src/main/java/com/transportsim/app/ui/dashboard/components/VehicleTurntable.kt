package com.transportsim.app.ui.dashboard.components

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.transportsim.app.ui.theme.Cyan
import com.transportsim.app.ui.theme.Gold
import com.transportsim.domain.models.FleetVehicle
import com.transportsim.domain.models.VehicleStatus
import io.github.sceneview.Scene
import io.github.sceneview.math.Position
import io.github.sceneview.math.Rotation
import io.github.sceneview.node.ModelNode
import io.github.sceneview.rememberEngine
import io.github.sceneview.rememberModelLoader

/**
 * A 3D vehicle turntable using SceneView with navigation and status.
 */
@Composable
fun VehicleTurntable(
    fleetVehicle: FleetVehicle?,
    onNext: () -> Unit,
    onPrevious: () -> Unit,
    showControls: Boolean = true,
    modifier: Modifier = Modifier
) {
    val engine = com.transportsim.app.LocalSceneEngine.current
    val modelLoader = com.transportsim.app.LocalModelLoader.current
    
    // Use a Ref to keep track of rotation without triggering recomposition on every frame
    val rotationRef = remember { object { var value = 0f } }

    val modelPath = fleetVehicle?.catalogEntry?.assetFileBase?.let { "3d/vehicles/$it" }
    val fallbackPath = "3d/vehicles/taxi_estate.glb"
    
    val modelNode = remember(modelPath) {
        if (modelPath != null) {
            try {
                // Try primary model
                val modelInstance = try {
                    modelLoader.createModelInstance(assetFileLocation = modelPath)
                } catch (e: Exception) {
                    Log.w("VehicleTurntable", "Primary model not found ($modelPath), using fallback")
                    modelLoader.createModelInstance(assetFileLocation = fallbackPath)
                }
                
                if (modelInstance != null) {
                    ModelNode(
                        modelInstance = modelInstance,
                        scaleToUnits = 1.0f,
                        centerOrigin = Position(x = 0.0f, y = 0.0f, z = 0.0f)
                    )
                } else null
            } catch (e: Exception) {
                Log.e("VehicleTurntable", "Failed to load both primary and fallback models", e)
                null
            }
        } else null
    }

    // Fix Memory Leak: Destroy the node when it's replaced or disposed
    DisposableEffect(modelNode) {
        onDispose {
            modelNode?.destroy()
        }
    }

    // Animation Loop: Update rotation directly on the node to avoid recomposition
    LaunchedEffect(modelNode) {
        if (modelNode == null) return@LaunchedEffect
        while (true) {
            withFrameNanos {
                rotationRef.value = (rotationRef.value + 0.8f) % 360f
                modelNode.rotation = Rotation(x = 0f, y = rotationRef.value, z = 0f)
            }
        }
    }

    // Remember child nodes list to avoid object allocation in Scene
    val childNodes = remember(modelNode) {
        if (modelNode != null) listOf(modelNode) else emptyList()
    }

    Box(modifier = modifier.fillMaxSize()) {
        // 3D Scene or Placeholder
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            if (modelNode != null) {
                // Environment & Lighting based on status
                val environmentAlpha = when (fleetVehicle?.status) {
                    VehicleStatus.ACTIVE -> 1.0f // Bright key light
                    VehicleStatus.IDLE -> 0.7f   // Warm studio lighting
                    VehicleStatus.GARAGE -> 0.5f // Clinical white / dim
                    else -> 0.6f
                }
                
                Scene(
                    modifier = Modifier.fillMaxSize(),
                    engine = engine,
                    modelLoader = modelLoader,
                    childNodes = childNodes,
                    // Pulse red logic can be added here if supported by the SceneView version
                )
            } else {
                Text(
                    text = if (modelPath == null) "No vehicle selected" else "🚗 Missing Model",
                    color = Color.Gray,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        // Navigation Arrows
        if (showControls) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.Center)
                    .padding(horizontal = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                NavigationArrow(icon = Icons.Default.ChevronLeft, onClick = onPrevious)
                NavigationArrow(icon = Icons.Default.ChevronRight, onClick = onNext)
            }
        }

        // Status Bar (Top)
        if (fleetVehicle != null && showControls) {
            val (statusText, statusColor) = when {
                fleetVehicle.status == VehicleStatus.ACTIVE -> "ACTIVE" to Cyan
                fleetVehicle.isOwned -> "PURCHASED" to Color.White
                else -> "LOCKED" to Gold
            }

            Surface(
                color = Color.Black.copy(alpha = 0.7f),
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 8.dp)
                    .clip(RoundedCornerShape(4.dp))
            ) {
                Text(
                    text = statusText,
                    color = statusColor,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                )
            }

            // Name Bar (Bottom)
            Text(
                text = fleetVehicle.catalogEntry.displayName,
                color = Color.White,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 8.dp)
                    .background(Color.Black.copy(alpha = 0.5f), RoundedCornerShape(4.dp))
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            )
        }
    }
}

@Composable
private fun NavigationArrow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit
) {
    Surface(
        shape = CircleShape,
        color = Color.Black.copy(alpha = 0.3f),
        modifier = Modifier
            .size(32.dp)
            .clip(CircleShape)
            .clickable { onClick() }
            .border(1.dp, Color.White.copy(alpha = 0.2f), CircleShape)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.padding(4.dp)
        )
    }
}
