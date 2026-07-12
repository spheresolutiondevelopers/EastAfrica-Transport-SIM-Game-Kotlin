package com.transportsim.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.transportsim.data.database.entities.PartsInventoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PartsInventoryDao {
    @Query("SELECT * FROM parts_inventory")
    suspend fun getAll(): List<PartsInventoryEntity>

    @Query("SELECT * FROM parts_inventory")
    fun observeAll(): Flow<List<PartsInventoryEntity>>

    @Query("SELECT * FROM parts_inventory WHERE partId = :partId")
    suspend fun getById(partId: String): PartsInventoryEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(part: PartsInventoryEntity)

    @Update
    suspend fun update(part: PartsInventoryEntity)

    @Query("UPDATE parts_inventory SET quantity = quantity + :amount WHERE partId = :partId")
    suspend fun adjustQuantity(partId: String, amount: Int)
}