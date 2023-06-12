package com.thefadaap.fadamarketingapp.utils

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.thefadaap.fadamarketingapp.utils.Common.IS_FIRST_SIGN_IN
import kotlinx.coroutines.flow.first

class PreferenceStore (val context: Context) {


    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore("session_manager")


    suspend fun updateSession(isFirstLogIn: Boolean) {
        val isFirstSignInKey = booleanPreferencesKey(IS_FIRST_SIGN_IN)

        context.dataStore.edit { preferences ->
            preferences[isFirstSignInKey] = isFirstLogIn
        }
    }

    suspend fun getIsFirstSignIn(): Boolean? {
        val isFirstSignInKey = booleanPreferencesKey(IS_FIRST_SIGN_IN)
        val preferences = context.dataStore.data.first()

        return preferences[isFirstSignInKey]
    }

    suspend fun logout() {
        context.dataStore.edit {
            it.clear()
        }
    }
}