package com.transportsim.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.transportsim.data.database.entities.PlayerProfileEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PlayerProfileDao {
    @Query("SELECT * FROM player_profile LIMIT 1")
    suspend fun getProfile(): PlayerProfileEntity?

    @Query("SELECT * FROM player_profile LIMIT 1")
    fun observeProfile(): Flow<PlayerProfileEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertProfile(profile: PlayerProfileEntity)

    @Query("UPDATE player_profile SET balanceKsh = balanceKsh + :amount, xp = xp + :xpGain WHERE playerId = 'default'")
    suspend fun addBalanceAndXp(amount: Int, xpGain: Int)
}