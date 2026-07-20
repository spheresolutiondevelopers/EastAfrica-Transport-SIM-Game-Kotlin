package com.transportsim.data.security

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import io.github.osipxd.security.crypto.encryptedPreferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EncryptedPrefs @Inject constructor(
    private val context: Context
) {
    // This delegate is an extension on Context. Usage: context.dataStore
    private val Context.dataStore by encryptedPreferencesDataStore(
        name = "encrypted_prefs"
    )

    suspend fun putString(key: String, value: String) {
        context.dataStore.edit { prefs ->
            prefs[stringPreferencesKey(key)] = value
        }
    }

    suspend fun getString(key: String, defaultValue: String = ""): String {
        return context.dataStore.data
            .map { prefs -> prefs[stringPreferencesKey(key)] ?: defaultValue }
            .first()
    }

    suspend fun putFloat(key: String, value: Float) {
        context.dataStore.edit { prefs ->
            prefs[floatPreferencesKey(key)] = value
        }
    }

    suspend fun getFloat(key: String, defaultValue: Float): Float {
        return context.dataStore.data.map { prefs ->
            prefs[floatPreferencesKey(key)] ?: defaultValue
        }.first()
    }

    suspend fun putBoolean(key: String, value: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[booleanPreferencesKey(key)] = value
        }
    }

    suspend fun getBoolean(key: String, defaultValue: Boolean): Boolean {
        return context.dataStore.data.map { prefs ->
            prefs[booleanPreferencesKey(key)] ?: defaultValue
        }.first()
    }

    fun observeString(key: String, defaultValue: String = ""): Flow<String> {
        return context.dataStore.data
            .map { prefs -> prefs[stringPreferencesKey(key)] ?: defaultValue }
    }

    suspend fun clearAll() {
        context.dataStore.edit { it.clear() }
    }
}
