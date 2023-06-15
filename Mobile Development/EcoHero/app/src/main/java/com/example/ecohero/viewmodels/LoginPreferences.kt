package com.example.ecohero.viewmodels

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class LoginPreferences private constructor(private val dataStore: DataStore<Preferences>) {

    fun isLoggedIn(): Flow<Boolean> {
        return dataStore.data.map { preferences ->
            preferences[IS_LOGGED_IN] ?: false
        }
    }

    suspend fun setLoggedIn(isLoggedIn: Boolean) {
        dataStore.edit { preferences ->
            preferences[IS_LOGGED_IN] =  isLoggedIn
        }
    }

    companion object {
        private val IS_LOGGED_IN = booleanPreferencesKey("isLoggedIn")

        @Volatile
        private var INSTANCE: LoginPreferences? = null

        fun getInstance(dataStore: DataStore<androidx.datastore.preferences.core.Preferences>): LoginPreferences {
            return INSTANCE ?: synchronized(this) {
                val instance = LoginPreferences(dataStore)
                INSTANCE = instance
                instance
            }
        }

    }

}