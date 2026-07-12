package com.transportsim.data.datasource

import android.content.Context
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.transportsim.domain.models.VehicleCatalogEntry
import com.transportsim.domain.models.Upgrade
import com.transportsim.domain.models.SurfaceType
import com.transportsim.domain.models.Mission
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AssetDataSource @Inject constructor(
    private val context: Context
) {
    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    private suspend fun <T> readJsonAsset(fileName: String, adapter: JsonAdapter<T>): T? {
        return withContext(Dispatchers.IO) {
            try {
                context.assets.open(fileName).use { inputStream ->
                    val json = inputStream.bufferedReader().use { it.readText() }
                    adapter.fromJson(json)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }

    suspend fun loadVehicleCatalog(): List<VehicleCatalogEntry> {
        val adapter: JsonAdapter<List<VehicleCatalogEntry>> = moshi.adapter(
            Types.newParameterizedType(List::class.java, VehicleCatalogEntry::class.java)
        )
        return readJsonAsset("config/vehicle_specs.json", adapter) ?: emptyList()
    }

    suspend fun loadUpgradeDefinitions(): List<Upgrade> {
        val adapter: JsonAdapter<List<Upgrade>> = moshi.adapter(
            Types.newParameterizedType(List::class.java, Upgrade::class.java)
        )
        return readJsonAsset("config/upgrade_defs.json", adapter) ?: emptyList()
    }

    suspend fun loadSurfaceTypes(): List<SurfaceType> {
        val adapter: JsonAdapter<List<SurfaceType>> = moshi.adapter(
            Types.newParameterizedType(List::class.java, SurfaceType::class.java)
        )
        return readJsonAsset("config/surface_types.json", adapter) ?: emptyList()
    }

    suspend fun loadMissionTemplates(): List<Mission> {
        val adapter: JsonAdapter<List<Mission>> = moshi.adapter(
            Types.newParameterizedType(List::class.java, Mission::class.java)
        )
        return readJsonAsset("config/mission_templates.json", adapter) ?: emptyList()
    }
}