package com.aarogyam.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "weight_logs")
data class WeightLog(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val weightKg: Double,
    val notes: String?,
    val loggedAt: Long = System.currentTimeMillis()
)
