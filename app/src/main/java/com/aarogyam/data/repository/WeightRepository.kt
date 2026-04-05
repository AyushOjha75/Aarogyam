package com.aarogyam.data.repository

import android.content.Context
import com.aarogyam.data.datastore.PreferencesRepository
import com.aarogyam.data.db.AppDatabase
import com.aarogyam.data.db.WeightLog
import com.aarogyam.data.db.WeightLogDao
import com.aarogyam.domain.UnitConverter
import com.aarogyam.domain.WeightUnit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class WeightRepository private constructor(
    private val dao: WeightLogDao,
    private val preferencesRepository: PreferencesRepository
) {

    val allLogs: Flow<List<WeightLog>> = dao.getAllLogs()

    val weightUnit: Flow<WeightUnit> = preferencesRepository.weightUnit.map { raw ->
        WeightUnit.entries.firstOrNull { it.name == raw } ?: WeightUnit.KG
    }

    val goalKg: Flow<Double> = preferencesRepository.goalKg

    suspend fun logWeight(kg: Double, notes: String?) {
        dao.insert(WeightLog(weightKg = kg, notes = notes))
    }

    suspend fun deleteLog(log: WeightLog) {
        dao.delete(log)
    }

    suspend fun getLatest(): WeightLog? = dao.getLatest()

    suspend fun getLastN(n: Int): List<WeightLog> = dao.getLastN(n)

    suspend fun setGoal(displayVal: Double, unit: WeightUnit) {
        val kg = UnitConverter.toStorageKg(displayVal, unit)
        preferencesRepository.setGoalKg(kg)
    }

    suspend fun setUnit(unit: WeightUnit) {
        preferencesRepository.setUnit(unit.name)
    }

    companion object {
        @Volatile
        private var INSTANCE: WeightRepository? = null

        fun getInstance(context: Context): WeightRepository {
            return INSTANCE ?: synchronized(this) {
                val db = AppDatabase.getInstance(context)
                val prefs = PreferencesRepository(context)
                WeightRepository(db.weightLogDao(), prefs).also { INSTANCE = it }
            }
        }

        /** Used by Hilt DI where dependencies are already provided. */
        fun create(dao: WeightLogDao, prefs: PreferencesRepository): WeightRepository {
            return INSTANCE ?: synchronized(this) {
                WeightRepository(dao, prefs).also { INSTANCE = it }
            }
        }
    }
}
