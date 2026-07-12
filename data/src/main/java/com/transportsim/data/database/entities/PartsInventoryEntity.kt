package com.transportsim.data.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "parts_inventory")
data class PartsInventoryEntity(
    @PrimaryKey
    val partId: String,
    val displayName: String,
    val quantity: Int,
    val icon: String?
)