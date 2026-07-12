package com.transportsim.bridge.models

import com.transportsim.bridge.annotations.Keep
import com.squareup.moshi.JsonClass

@Keep
@JsonClass(generateAdapter = true)
data class NativeWorldObject(
    val objectId: Int,
    val objectType: String,
    val worldX: Float,
    val worldZ: Float,
    val worldY: Float,
    val rotationYDeg: Float,
    val scaleUniform: Float,
    val assetFile: String,
    val quadtreeCell: String,
    val isPhysicsCollidable: Boolean,
    val lodLevel: Int,
    val isGpuInstanced: Boolean,
    val rustParam: Float,
    val vertexColorR: Float,
    val vertexColorG: Float,
    val vertexColorB: Float,
    val detectionConfidence: Float,
    val metadataJson: String  // Type-specific JSON
)