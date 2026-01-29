package com.finance.app.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "finance_prefs")

class PreferencesManager(private val context: Context) {
    companion object {
        val TOKEN_KEY = stringPreferencesKey("auth_token")
        val USER_ID_KEY = stringPreferencesKey("user_id")
        val BASE_URL_KEY = stringPreferencesKey("base_url")
    }
    
    val token: Flow<String?> = context.dataStore.data.map { it[TOKEN_KEY] }
    val userId: Flow<String?> = context.dataStore.data.map { it[USER_ID_KEY] }
    val baseUrl: Flow<String?> = context.dataStore.data.map { it[BASE_URL_KEY] }
    
    suspend fun saveToken(token: String) {
        context.dataStore.edit { it[TOKEN_KEY] = token }
    }
    
    suspend fun saveUserId(userId: Int) {
        context.dataStore.edit { it[USER_ID_KEY] = userId.toString() }
    }
    
    suspend fun saveBaseUrl(baseUrl: String) {
        context.dataStore.edit { it[BASE_URL_KEY] = baseUrl }
    }
    
    suspend fun clear() {
        context.dataStore.edit { it.clear() }
    }
    
    suspend fun getTokenSync(): String? {
        return context.dataStore.data.first()[TOKEN_KEY]
    }
}
