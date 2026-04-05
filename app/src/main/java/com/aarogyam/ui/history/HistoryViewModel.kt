package com.aarogyam.ui.history

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.aarogyam.data.db.WeightLog
import com.aarogyam.data.repository.WeightRepository
import com.aarogyam.domain.UnitConverter
import com.aarogyam.domain.WeightUnit
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HistoryUiState(
    val entries: List<WeightLog> = emptyList(),
    val unit: WeightUnit = WeightUnit.KG,
    val chartPoints: List<Pair<Long, Double>> = emptyList()
)

@HiltViewModel
class HistoryViewModel @Inject constructor(
    application: Application,
    private val repository: WeightRepository
) : AndroidViewModel(application) {

    val uiState: StateFlow<HistoryUiState> = combine(
        repository.allLogs,
        repository.weightUnit
    ) { logs, unit ->
        val chartPoints = logs
            .sortedBy { it.loggedAt }
            .map { log -> log.loggedAt to UnitConverter.toDisplay(log.weightKg, unit) }
        HistoryUiState(entries = logs, unit = unit, chartPoints = chartPoints)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), HistoryUiState())

    fun deleteLog(log: WeightLog) {
        viewModelScope.launch {
            repository.deleteLog(log)
        }
    }
}
