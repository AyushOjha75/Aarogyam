package com.aarogyam.ui.goal

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.aarogyam.data.repository.WeightRepository
import com.aarogyam.domain.WeightUnit
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GoalViewModel @Inject constructor(
    application: Application,
    private val repository: WeightRepository
) : AndroidViewModel(application) {

    val goalKg: StateFlow<Double> = repository.goalKg
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), 0.0)

    val weightUnit: StateFlow<WeightUnit> = repository.weightUnit
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), WeightUnit.KG)

    val heightCm: StateFlow<Double> = repository.heightCm
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), 0.0)

    val latestWeightKg: StateFlow<Double> = kotlinx.coroutines.flow.flow {
        emit(repository.getLatest()?.weightKg ?: 0.0)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), 0.0)

    val appTheme: StateFlow<String> = repository.appTheme
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), "DARK")

    fun saveGoal(displayVal: String) {
        val value = displayVal.toDoubleOrNull() ?: return
        viewModelScope.launch {
            val unit = weightUnit.value
            repository.setGoal(value, unit)
        }
    }

    fun toggleUnit(useImperial: Boolean) {
        viewModelScope.launch {
            repository.setUnit(if (useImperial) WeightUnit.LBS else WeightUnit.KG)
        }
    }

    fun saveHeight(cm: String) {
        val value = cm.toDoubleOrNull() ?: return
        viewModelScope.launch {
            repository.setHeightCm(value)
        }
    }

    fun saveTheme(theme: String) {
        viewModelScope.launch {
            repository.setAppTheme(theme)
        }
    }
}
