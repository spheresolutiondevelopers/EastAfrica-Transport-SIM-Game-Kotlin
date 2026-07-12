package com.transportsim.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.transportsim.data.database.entities.EconomyLedgerEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface EconomyLedgerDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entry: EconomyLedgerEntity): Long

    @Query("SELECT * FROM economy_ledger ORDER BY createdAt DESC LIMIT 100")
    suspend fun getRecent(): List<EconomyLedgerEntity>

    @Query("SELECT * FROM economy_ledger ORDER BY createdAt DESC LIMIT 100")
    fun observeRecent(): Flow<List<EconomyLedgerEntity>>

    @Query("SELECT SUM(amountKsh) FROM economy_ledger WHERE txType = :txType AND createdAt >= :since")
    suspend fun getSumByTypeSince(txType: String, since: String): Int?

    @Query("DELETE FROM economy_ledger WHERE createdAt < :olderThan")
    suspend fun deleteOlderThan(olderThan: String)
}