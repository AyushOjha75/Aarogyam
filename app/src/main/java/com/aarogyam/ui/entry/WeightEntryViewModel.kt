package com.aarogyam.ui.entry

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.aarogyam.data.repository.WeightRepository
import com.aarogyam.domain.UnitConverter
import com.aarogyam.domain.WeightUnit
import com.aarogyam.widget.WidgetUpdateReceiver
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WeightEntryViewModel @Inject constructor(
    application: Application,
    private val repository: WeightRepository
) : AndroidViewModel(application) {

    val weightUnit: StateFlow<WeightUnit> = repository.weightUnit
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), WeightUnit.KG)

    fun logWeight(displayValue: String, notes: String) {
        val parsed = displayValue.toDoubleOrNull() ?: return
        viewModelScope.launch {
            val unit = weightUnit.value
            val kg = UnitConverter.toStorageKg(parsed, unit)
            repository.logWeight(kg, notes.ifBlank { null })
            WidgetUpdateReceiver.send(getApplication())
        }
    }
}
