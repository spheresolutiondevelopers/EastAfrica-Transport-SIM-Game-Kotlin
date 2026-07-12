package com.transportsim.data.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "economy_ledger")
data class EconomyLedgerEntity(
    @PrimaryKey(autoGenerate = true)
    val ledgerId: Int = 0,
    val txType: String,   // "JOURNEY_REVENUE", "REFUEL", "REPAIR", "UPGRADE", "PURCHASE"
    val amountKsh: Int,
    val balanceAfterKsh: Int,
    val relatedId: String?, // sessionId, vehicleId, etc.
    val description: String?,
    val createdAt: String
)