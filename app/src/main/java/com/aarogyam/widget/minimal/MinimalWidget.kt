package com.aarogyam.widget.minimal

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.padding
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import com.aarogyam.data.repository.WeightRepository
import com.aarogyam.domain.UnitConverter
import com.aarogyam.domain.WeightUnit
import kotlinx.coroutines.flow.first
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MinimalWidget : GlanceAppWidget() {

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val data = safeDbRead(context)
        provideContent {
            MinimalWidgetContent(data)
        }
    }

    private suspend fun safeDbRead(context: Context): MinimalWidgetData? {
        return try {
            val repo = WeightRepository.getInstance(context)
            val latest = repo.getLatest() ?: return null
            val unit: WeightUnit = repo.weightUnit.first()
            MinimalWidgetData(
                formattedWeight = UnitConverter.format(latest.weightKg, unit),
                date = SimpleDateFormat("d MMM", Locale.getDefault()).format(Date(latest.loggedAt))
            )
        } catch (e: Exception) {
            null
        }
    }

    suspend fun update(context: Context) {
        GlanceAppWidgetManager(context)
            .getGlanceIds(MinimalWidget::class.java)
            .forEach { id -> update(context, id) }
    }
}

data class MinimalWidgetData(val formattedWeight: String, val date: String)

@Composable
private fun MinimalWidgetContent(data: MinimalWidgetData?) {
    Box(
        modifier = GlanceModifier
            .fillMaxSize()
            .background(Color(0xFF2C2C32))
            .padding(8.dp),
        contentAlignment = Alignment.Center
    ) {
        if (data == null) {
            Text(
                text = "No data yet — open app to log weight",
                style = TextStyle(color = ColorProvider(Color(0xFF8A8A96)), fontSize = 11.sp)
            )
        } else {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = data.formattedWeight,
                    style = TextStyle(
                        color = ColorProvider(Color(0xFFE8A045)),
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    )
                )
                Text(
                    text = data.date,
                    style = TextStyle(
                        color = ColorProvider(Color(0xFF8A8A96)),
                        fontSize = 11.sp
                    )
                )
            }
        }
    }
}
