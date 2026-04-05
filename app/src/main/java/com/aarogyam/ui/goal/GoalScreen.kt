package com.aarogyam.ui.goal

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.aarogyam.domain.UnitConverter
import com.aarogyam.domain.WeightUnit
import com.aarogyam.ui.components.AarogyamButton
import com.aarogyam.ui.components.AarogyamCard
import com.aarogyam.ui.components.AarogyamTopBar
import com.aarogyam.ui.components.SectionHeader
import com.aarogyam.ui.theme.Amber400
import com.aarogyam.ui.theme.AppTheme

private fun calcBmi(weightKg: Double, heightCm: Double): Double? {
    if (weightKg <= 0.0 || heightCm <= 0.0) return null
    val heightM = heightCm / 100.0
    return weightKg / (heightM * heightM)
}

private fun bmiCategory(bmi: Double): String = when {
    bmi < 18.5 -> "Underweight"
    bmi < 25.0 -> "Normal"
    bmi < 30.0 -> "Overweight"
    else -> "Obese"
}

@Composable
fun GoalScreen(
    viewModel: GoalViewModel = hiltViewModel()
) {
    val goalKg by viewModel.goalKg.collectAsStateWithLifecycle()
    val unit by viewModel.weightUnit.collectAsStateWithLifecycle()
    val heightCm by viewModel.heightCm.collectAsStateWithLifecycle()
    val latestWeightKg by viewModel.latestWeightKg.collectAsStateWithLifecycle()
    val appThemeStr by viewModel.appTheme.collectAsStateWithLifecycle()

    var goalInput by remember { mutableStateOf("") }
    var heightInput by remember { mutableStateOf("") }
    var goalSaved by remember { mutableStateOf(false) }
    var heightSaved by remember { mutableStateOf(false) }

    val currentGoalDisplay = if (goalKg > 0.0) UnitConverter.format(goalKg, unit) else "Not set"

    // BMI from latest weight + stored height
    val currentBmi = calcBmi(latestWeightKg, heightCm)
    val currentBmiStr = currentBmi?.let { "${"%.1f".format(it)} — ${bmiCategory(it)}" } ?: "Set height to calculate"

    // Goal BMI preview
    val goalKgPreview: Double? = goalInput.toDoubleOrNull()?.let { v ->
        UnitConverter.toStorageKg(v, unit)
    }
    val goalBmiPreview = if (goalKgPreview != null) calcBmi(goalKgPreview, heightCm) else null

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AarogyamTopBar(title = "Goals & Settings")

        // Current Stats Card
        AarogyamCard(modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)) {
            Column {
                SectionHeader("Current Stats")
                StatRow(label = "Current goal", value = currentGoalDisplay)
                StatRow(label = "Current BMI", value = currentBmiStr)
                StatRow(label = "Height", value = if (heightCm > 0.0) "${"%.1f".format(heightCm)} cm" else "Not set")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Goal Setting Card
        AarogyamCard(modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)) {
            Column {
                SectionHeader("Goal Setting")

                // Unit toggle
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(bottom = 8.dp)
                ) {
                    Text("Unit:", color = MaterialTheme.colorScheme.onSurface, style = MaterialTheme.typography.bodyMedium)
                    FilterChip(
                        selected = unit == WeightUnit.KG,
                        onClick = { viewModel.toggleUnit(false) },
                        label = { Text("KG") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Amber400,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                        )
                    )
                    FilterChip(
                        selected = unit == WeightUnit.LBS,
                        onClick = { viewModel.toggleUnit(true) },
                        label = { Text("LBS") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Amber400,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                        )
                    )
                }

                OutlinedTextField(
                    value = goalInput,
                    onValueChange = {
                        goalInput = it
                        goalSaved = false
                    },
                    label = { Text("Goal weight (${unit.label})") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    supportingText = if (goalBmiPreview != null) {
                        { Text("At ${goalInput} ${unit.label} your BMI would be ${"%.1f".format(goalBmiPreview)} — ${bmiCategory(goalBmiPreview)}") }
                    } else null
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = heightInput,
                    onValueChange = {
                        heightInput = it
                        heightSaved = false
                    },
                    label = { Text("Height (cm)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    AarogyamButton(
                        text = if (goalSaved) "Goal Saved!" else "Save Goal",
                        onClick = {
                            viewModel.saveGoal(goalInput)
                            goalInput = ""
                            goalSaved = true
                        },
                        enabled = goalInput.isNotBlank(),
                        modifier = Modifier.weight(1f)
                    )
                    AarogyamButton(
                        text = if (heightSaved) "Height Saved!" else "Save Height",
                        onClick = {
                            viewModel.saveHeight(heightInput)
                            heightInput = ""
                            heightSaved = true
                        },
                        enabled = heightInput.isNotBlank(),
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Theme Card
        AarogyamCard(modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)) {
            Column {
                SectionHeader("Theme")
                ThemeSelector(
                    currentTheme = when (appThemeStr) {
                        "LIGHT" -> AppTheme.LIGHT
                        "FOREST" -> AppTheme.FOREST
                        else -> AppTheme.DARK
                    },
                    onThemeSelected = { theme -> viewModel.saveTheme(theme.name) }
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun StatRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(text = value, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface)
    }
}

@Composable
fun ThemeSelector(
    currentTheme: AppTheme,
    onThemeSelected: (AppTheme) -> Unit
) {
    val themes = listOf(
        Triple(AppTheme.DARK, "Dark", listOf(Color(0xFF1A1A1E), Color(0xFF2C2C32), Color(0xFFE8A045))),
        Triple(AppTheme.LIGHT, "Light", listOf(Color(0xFFFFFFFF), Color(0xFFF5F5F5), Color(0xFFE8A045))),
        Triple(AppTheme.FOREST, "Forest", listOf(Color(0xFF2E7D32), Color(0xFF1B5E20), Color(0xFF4CAF50)))
    )

    Row(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        themes.forEach { (theme, label, colors) ->
            val isSelected = theme == currentTheme
            Column(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(8.dp))
                    .border(
                        width = if (isSelected) 2.dp else 1.dp,
                        color = if (isSelected) Amber400 else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                        shape = RoundedCornerShape(8.dp)
                    )
                    .clickable { onThemeSelected(theme) }
                    .padding(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Color preview swatch
                Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                    colors.forEach { c ->
                        Box(
                            modifier = Modifier
                                .size(16.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(c)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelLarge,
                    color = if (isSelected) Amber400 else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
