package com.transportsim.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.transportsim.data.database.entities.PlayerVehicleEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PlayerVehicleDao {
    @Query("SELECT * FROM player_vehicles ORDER BY vehicleId")
    suspend fun getAll(): List<PlayerVehicleEntity>

    @Query("SELECT * FROM player_vehicles ORDER BY vehicleId")
    fun observeAll(): Flow<List<PlayerVehicleEntity>>

    @Query("SELECT * FROM player_vehicles WHERE vehicleId = :vehicleId")
    suspend fun getById(vehicleId: Int): PlayerVehicleEntity?

    @Query("SELECT * FROM player_vehicles WHERE status = 'ACTIVE'")
    suspend fun getActiveVehicles(): List<PlayerVehicleEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(vehicle: PlayerVehicleEntity): Long

    @Update
    suspend fun update(vehicle: PlayerVehicleEntity)

    @Query("UPDATE player_vehicles SET status = :status WHERE vehicleId = :vehicleId")
    suspend fun updateStatus(vehicleId: Int, status: String)

    @Query("UPDATE player_vehicles SET fuelLevelL = :fuelLevel, fuelLevelPct = :fuelPct WHERE vehicleId = :vehicleId")
    suspend fun updateFuel(vehicleId: Int, fuelLevel: Float, fuelPct: Float)

    @Query("DELETE FROM player_vehicles WHERE vehicleId = :vehicleId")
    suspend fun delete(vehicleId: Int)

    @Query("SELECT EXISTS(SELECT 1 FROM player_vehicles WHERE typeId = :typeId)")
    suspend fun existsByTypeId(typeId: String): Boolean
}