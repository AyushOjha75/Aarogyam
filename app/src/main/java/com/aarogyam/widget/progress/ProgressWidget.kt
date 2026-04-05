package com.aarogyam.widget.progress

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.LinearProgressIndicator
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.padding
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import com.aarogyam.data.repository.WeightRepository
import com.aarogyam.domain.UnitConverter
import kotlinx.coroutines.flow.first

class ProgressWidget : GlanceAppWidget() {

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val data = safeDbRead(context)
        provideContent {
            ProgressWidgetContent(data)
        }
    }

    private suspend fun safeDbRead(context: Context): ProgressWidgetData? {
        return try {
            val repo = WeightRepository.getInstance(context)
            val latest = repo.getLatest() ?: return null
            val goalKg = repo.goalKg.first()
            if (goalKg <= 0.0) return null
            val unit = repo.weightUnit.first()
            val currentDisplay = UnitConverter.toDisplay(latest.weightKg, unit)
            val goalDisplay = UnitConverter.toDisplay(goalKg, unit)
            val progress = if (goalDisplay > 0) {
                (currentDisplay / goalDisplay).toFloat().coerceIn(0f, 1f)
            } else 0f
            ProgressWidgetData(
                formattedCurrent = UnitConverter.format(latest.weightKg, unit),
                formattedGoal = UnitConverter.format(goalKg, unit),
                progress = progress
            )
        } catch (e: Exception) {
            null
        }
    }

    suspend fun update(context: Context) {
        GlanceAppWidgetManager(context)
            .getGlanceIds(ProgressWidget::class.java)
            .forEach { id -> update(context, id) }
    }
}

data class ProgressWidgetData(
    val formattedCurrent: String,
    val formattedGoal: String,
    val progress: Float
)

@Composable
private fun ProgressWidgetContent(data: ProgressWidgetData?) {
    Box(
        modifier = GlanceModifier
            .fillMaxSize()
            .background(Color(0xFF2C2C32))
            .padding(10.dp),
        contentAlignment = Alignment.Center
    ) {
        if (data == null) {
            Text(
                text = "Set a goal in app",
                style = TextStyle(color = ColorProvider(Color(0xFF8A8A96)), fontSize = 12.sp)
            )
        } else {
            Column(
                modifier = GlanceModifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "${data.formattedCurrent} / ${data.formattedGoal}",
                    style = TextStyle(
                        color = ColorProvider(Color(0xFFE8A045)),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                )
                LinearProgressIndicator(
                    progress = data.progress,
                    modifier = GlanceModifier.fillMaxWidth().padding(top = 6.dp),
                    color = ColorProvider(Color(0xFFE8A045)),
                    backgroundColor = ColorProvider(Color(0xFF3E3E46))
                )
            }
        }
    }
}
