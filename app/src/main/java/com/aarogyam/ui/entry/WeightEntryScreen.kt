package com.aarogyam.ui.entry

import android.app.DatePickerDialog
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.aarogyam.ui.components.AarogyamButton
import com.aarogyam.ui.components.AarogyamCard
import com.aarogyam.ui.components.AarogyamTextField
import com.aarogyam.ui.components.AarogyamTopBar
import com.aarogyam.ui.components.SectionHeader
import com.aarogyam.ui.theme.Amber400
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Composable
fun WeightEntryScreen(
    viewModel: WeightEntryViewModel = hiltViewModel()
) {
    val unit by viewModel.weightUnit.collectAsStateWithLifecycle()
    var weightInput by remember { mutableStateOf("") }
    var notesInput by remember { mutableStateOf("") }
    var saved by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val now = remember { Calendar.getInstance() }
    var selectedDateMillis by remember { mutableLongStateOf(System.currentTimeMillis()) }

    val dateLabel = remember(selectedDateMillis) {
        SimpleDateFormat("EEEE, d MMMM yyyy", Locale.getDefault()).format(Date(selectedDateMillis))
    }

    val parsedWeight = weightInput.toDoubleOrNull()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AarogyamTopBar(title = "Log Weight")

        // Live weight preview
        if (parsedWeight != null) {
            Text(
                text = "${"%.1f".format(parsedWeight)} ${unit.label}",
                style = MaterialTheme.typography.displayMedium,
                color = Amber400,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 8.dp)
            )
        } else {
            Spacer(modifier = Modifier.height(24.dp))
        }

        Spacer(modifier = Modifier.height(8.dp))

        AarogyamCard(modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)) {
            Column {
                SectionHeader("Entry Details")

                AarogyamTextField(
                    value = weightInput,
                    onValueChange = {
                        weightInput = it
                        saved = false
                    },
                    label = "Weight (${unit.label})",
                    keyboardType = KeyboardType.Decimal
                )

                Spacer(modifier = Modifier.height(16.dp))

                AarogyamTextField(
                    value = notesInput,
                    onValueChange = { notesInput = it },
                    label = "Notes (optional)",
                    keyboardType = KeyboardType.Text
                )

                Spacer(modifier = Modifier.height(16.dp))

                SectionHeader("Date")

                OutlinedButton(
                    onClick = {
                        val cal = Calendar.getInstance().apply { timeInMillis = selectedDateMillis }
                        val dpd = DatePickerDialog(
                            context,
                            { _, year, month, day ->
                                val picked = Calendar.getInstance().apply {
                                    set(year, month, day, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), 0)
                                    set(Calendar.MILLISECOND, 0)
                                }
                                selectedDateMillis = picked.timeInMillis
                            },
                            cal.get(Calendar.YEAR),
                            cal.get(Calendar.MONTH),
                            cal.get(Calendar.DAY_OF_MONTH)
                        )
                        // Allow any past date — no future restriction beyond today
                        dpd.datePicker.maxDate = System.currentTimeMillis()
                        dpd.show()
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = dateLabel,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        AarogyamButton(
            text = if (saved) "Saved!" else "Save Entry",
            onClick = {
                viewModel.logWeight(weightInput, notesInput, selectedDateMillis)
                weightInput = ""
                notesInput = ""
                selectedDateMillis = System.currentTimeMillis()
                saved = true
            },
            enabled = weightInput.isNotBlank(),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))
    }
}
