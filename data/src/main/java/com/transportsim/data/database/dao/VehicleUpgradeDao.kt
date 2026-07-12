package com.transportsim.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.transportsim.data.database.entities.VehicleUpgradeEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface VehicleUpgradeDao {
    @Query("SELECT * FROM vehicle_upgrades WHERE vehicleId = :vehicleId")
    suspend fun getForVehicle(vehicleId: Int): List<VehicleUpgradeEntity>

    @Query("SELECT * FROM vehicle_upgrades WHERE vehicleId = :vehicleId")
    fun observeForVehicle(vehicleId: Int): Flow<List<VehicleUpgradeEntity>>

    @Query("SELECT * FROM vehicle_upgrades WHERE vehicleId = :vehicleId AND upgradeId = :upgradeId")
    suspend fun get(vehicleId: Int, upgradeId: String): VehicleUpgradeEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(upgrade: VehicleUpgradeEntity)

    @Update
    suspend fun update(upgrade: VehicleUpgradeEntity)

    @Query("DELETE FROM vehicle_upgrades WHERE vehicleId = :vehicleId")
    suspend fun deleteForVehicle(vehicleId: Int)
}