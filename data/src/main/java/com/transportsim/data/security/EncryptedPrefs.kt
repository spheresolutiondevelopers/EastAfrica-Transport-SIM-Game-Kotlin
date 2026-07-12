package com.transportsim.data.security

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.security.crypto.EncryptedFile
import androidx.security.crypto.MasterKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EncryptedPrefs @Inject constructor(
    private val context: Context
) {
    private val Context.dataStore by preferencesDataStore(
        name = "encrypted_prefs",
        encryption = { context ->
            val masterKey = MasterKey.Builder(context)
                .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                .build()
            EncryptedFile(
                File(context.filesDir, "encrypted_prefs_preferences"),
                masterKey,
                EncryptedFile.FileEncryptionScheme.AES256_GCM_HKDF_4KB
            )
        }
    )

    suspend fun putString(key: String, value: String) {
        dataStore.edit { prefs ->
            prefs[stringPreferencesKey(key)] = value
        }
    }

    suspend fun getString(key: String, defaultValue: String = ""): String {
        return dataStore.data
            .map { prefs -> prefs[stringPreferencesKey(key)] ?: defaultValue }
            .collect { return it }
    }

    fun observeString(key: String, defaultValue: String = ""): Flow<String> {
        return dataStore.data
            .map { prefs -> prefs[stringPreferencesKey(key)] ?: defaultValue }
    }

    suspend fun clearAll() {
        dataStore.edit { it.clear() }
    }
}