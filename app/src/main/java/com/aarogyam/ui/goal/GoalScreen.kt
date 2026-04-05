package com.aarogyam.ui.goal

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.aarogyam.domain.UnitConverter
import com.aarogyam.domain.WeightUnit
import com.aarogyam.ui.theme.Amber400

@Composable
fun GoalScreen(
    viewModel: GoalViewModel = hiltViewModel()
) {
    val goalKg by viewModel.goalKg.collectAsStateWithLifecycle()
    val unit by viewModel.weightUnit.collectAsStateWithLifecycle()
    var goalInput by remember { mutableStateOf("") }
    var saved by remember { mutableStateOf(false) }

    val currentGoalDisplay = if (goalKg > 0.0) {
        UnitConverter.format(goalKg, unit)
    } else {
        "Not set"
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Weight Goal",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Current goal: $currentGoalDisplay",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Unit toggle
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Unit:", color = MaterialTheme.colorScheme.onBackground)
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

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = goalInput,
            onValueChange = {
                goalInput = it
                saved = false
            },
            label = { Text("Goal weight (${unit.label})") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                viewModel.saveGoal(goalInput)
                goalInput = ""
                saved = true
            },
            enabled = goalInput.isNotBlank(),
            colors = ButtonDefaults.buttonColors(containerColor = Amber400),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = if (saved) "Goal Saved!" else "Save Goal",
                color = MaterialTheme.colorScheme.onPrimary
            )
        }
    }
}
