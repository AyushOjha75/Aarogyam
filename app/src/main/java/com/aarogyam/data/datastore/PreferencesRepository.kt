package com.aarogyam.data.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "aarogyam_prefs")

class PreferencesRepository(private val context: Context) {

    companion object {
        val KEY_UNIT = stringPreferencesKey("weight_unit")
        val KEY_GOAL_KG = doublePreferencesKey("goal_kg")
        val KEY_HEIGHT_CM = doublePreferencesKey("height_cm")
        val KEY_THEME = stringPreferencesKey("app_theme")
    }

    val weightUnit: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[KEY_UNIT] ?: "KG"
    }

    val goalKg: Flow<Double> = context.dataStore.data.map { prefs ->
        prefs[KEY_GOAL_KG] ?: 0.0
    }

    val heightCm: Flow<Double> = context.dataStore.data.map { prefs ->
        prefs[KEY_HEIGHT_CM] ?: 0.0
    }

    val appTheme: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[KEY_THEME] ?: "DARK"
    }

    suspend fun setUnit(unit: String) {
        context.dataStore.edit { prefs ->
            prefs[KEY_UNIT] = unit
        }
    }

    suspend fun setGoalKg(kg: Double) {
        context.dataStore.edit { prefs ->
            prefs[KEY_GOAL_KG] = kg
        }
    }

    suspend fun setHeightCm(cm: Double) {
        context.dataStore.edit { prefs ->
            prefs[KEY_HEIGHT_CM] = cm
        }
    }

    suspend fun setAppTheme(theme: String) {
        context.dataStore.edit { prefs ->
            prefs[KEY_THEME] = theme
        }
    }
}
