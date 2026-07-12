package com.transportsim.data.datasource

import android.content.Context
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.sqlite.db.SupportSQLiteOpenHelper
import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory
import net.sqlcipher.database.SupportFactory
import net.sqlcipher.database.SQLiteDatabase
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WorldDatabaseDataSource @Inject constructor(
    private val context: Context
) {
    // Use a separate helper for the read-only world database
    private val dbName = "world_assets.db"
    private val passphrase = "world_assets_read_only" // Could be static since it's read-only

    private val openHelper: SupportSQLiteOpenHelper by lazy {
        val factory = SupportFactory(SQLiteDatabase.getBytes(passphrase.toCharArray()))
        val config = SupportSQLiteOpenHelper.Configuration(
            context = context,
            name = dbName,
            callback = object : SupportSQLiteOpenHelper.Callback(1) {
                override fun onCreate(db: SupportSQLiteDatabase) {
                    // The database is pre-packed, so onCreate is not called.
                }
                override fun onUpgrade(db: SupportSQLiteDatabase, oldVersion: Int, newVersion: Int) {
                    // No upgrades on read-only DB
                }
            }
        )
        factory.create(config)
    }

    private fun getDatabase(): SupportSQLiteDatabase =
        openHelper.writableDatabase // It will be read-only in practice

    fun getRoutes(): List<Map<String, Any>> {
        val db = getDatabase()
        val cursor = db.query("SELECT * FROM routes")
        val result = mutableListOf<Map<String, Any>>()
        while (cursor.moveToNext()) {
            val row = mutableMapOf<String, Any>()
            for (i in 0 until cursor.columnCount) {
                val columnName = cursor.getColumnName(i)
                when (cursor.getType(i)) {
                    SupportSQLiteDatabase.COLUMN_TYPE_TEXT -> row[columnName] = cursor.getString(i)
                    SupportSQLiteDatabase.COLUMN_TYPE_INTEGER -> row[columnName] = cursor.getLong(i)
                    SupportSQLiteDatabase.COLUMN_TYPE_FLOAT -> row[columnName] = cursor.getDouble(i)
                    else -> row[columnName] = cursor.getString(i)
                }
            }
            result.add(row)
        }
        cursor.close()
        return result
    }

    // Add similar methods for other tables (road_segments, bus_stops, etc.)
    // In a real implementation, we'd map these to domain models via a repository.
}