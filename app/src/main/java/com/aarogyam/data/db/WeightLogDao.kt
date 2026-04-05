package com.aarogyam.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface WeightLogDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(log: WeightLog): Long

    @Delete
    suspend fun delete(log: WeightLog)

    @Query("SELECT * FROM weight_logs ORDER BY loggedAt DESC")
    fun getAllLogs(): Flow<List<WeightLog>>

    @Query("SELECT * FROM weight_logs ORDER BY loggedAt DESC LIMIT 1")
    suspend fun getLatest(): WeightLog?

    @Query("SELECT * FROM weight_logs ORDER BY loggedAt DESC LIMIT :n")
    suspend fun getLastN(n: Int): List<WeightLog>
}
