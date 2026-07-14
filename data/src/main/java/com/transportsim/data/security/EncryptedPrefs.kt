package com.transportsim.data.security

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import io.github.osipxd.datastore.preferences.encrypted.encryptedPreferencesDataStore
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

    fun observeString(key: String, defaultValue: String = ""): Flow<String> {
        return context.dataStore.data
            .map { prefs -> prefs[stringPreferencesKey(key)] ?: defaultValue }
    }

    suspend fun clearAll() {
        context.dataStore.edit { it.clear() }
    }
}
