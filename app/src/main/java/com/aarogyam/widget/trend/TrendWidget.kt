package com.aarogyam.widget.trend

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.LocalSize
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.SizeMode
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import com.aarogyam.data.repository.WeightRepository
import com.aarogyam.domain.UnitConverter
import kotlinx.coroutines.flow.first

class TrendWidget : GlanceAppWidget() {

    companion object {
        val MEDIUM = DpSize(180.dp, 80.dp)
        val LARGE = DpSize(250.dp, 120.dp)
    }

    override val sizeMode = SizeMode.Responsive(setOf(MEDIUM, LARGE))

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val result = safeDbRead(context)
        provideContent {
            TrendWidgetContent(result)
        }
    }

    private suspend fun safeDbRead(context: Context): TrendWidgetData? {
        return try {
            val repo = WeightRepository.getInstance(context)
            val logs = repo.getLastN(10)
            if (logs.size < 2) return null
            val unit = repo.weightUnit.first()
            val sorted = logs.sortedBy { it.loggedAt }
            val points = sorted.map { UnitConverter.toDisplay(it.weightKg, unit).toFloat() }
            val label = unit.label
            val last7 = points.takeLast(7)
            val minVal = last7.min()
            val maxVal = last7.max()
            TrendWidgetData(
                bitmap = buildSparklineBitmap(points),
                minLabel = "${"%.1f".format(minVal)} $label",
                maxLabel = "${"%.1f".format(maxVal)} $label"
            )
        } catch (e: Exception) {
            null
        }
    }

    private fun buildSparklineBitmap(points: List<Float>): Bitmap {
        val width = 300
        val height = 80
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        canvas.drawColor(android.graphics.Color.parseColor("#2C2C32"))

        val minVal = points.min()
        val maxVal = points.max()
        val range = if (maxVal - minVal == 0f) 1f else maxVal - minVal

        val paint = Paint().apply {
            color = android.graphics.Color.parseColor("#E8A045")
            strokeWidth = 3f
            style = Paint.Style.STROKE
            isAntiAlias = true
            strokeJoin = Paint.Join.ROUND
            strokeCap = Paint.Cap.ROUND
        }

        val path = Path()
        val padH = 10f
        val padV = 10f
        val usableW = width - 2 * padH
        val usableH = height - 2 * padV

        points.forEachIndexed { index, value ->
            val x = padH + index / (points.size - 1).toFloat() * usableW
            val y = padV + usableH - ((value - minVal) / range) * usableH
            if (index == 0) path.moveTo(x, y) else path.lineTo(x, y)
        }

        canvas.drawPath(path, paint)
        return bitmap
    }

    suspend fun update(context: Context) {
        GlanceAppWidgetManager(context)
            .getGlanceIds(TrendWidget::class.java)
            .forEach { id -> update(context, id) }
    }
}

data class TrendWidgetData(
    val bitmap: Bitmap,
    val minLabel: String,
    val maxLabel: String
)

@Composable
private fun TrendWidgetContent(data: TrendWidgetData?) {
    val size = LocalSize.current
    val isLarge = size.height >= TrendWidget.LARGE.height

    Box(
        modifier = GlanceModifier
            .fillMaxSize()
            .background(Color(0xFF2C2C32))
            .padding(8.dp),
        contentAlignment = Alignment.Center
    ) {
        if (data == null) {
            Text(
                text = "Log entries to see trend",
                style = TextStyle(color = ColorProvider(Color(0xFF8A8A96)), fontSize = 12.sp)
            )
        } else if (isLarge) {
            // Large: sparkline + 7-day min/max labels
            Column(
                modifier = GlanceModifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    provider = ImageProvider(data.bitmap),
                    contentDescription = "Weight trend sparkline",
                    modifier = GlanceModifier.fillMaxWidth().height(72.dp)
                )
                Row(
                    modifier = GlanceModifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Min: ${data.minLabel}",
                        style = TextStyle(color = ColorProvider(Color(0xFF8A8A96)), fontSize = 10.sp),
                        modifier = GlanceModifier.padding(end = 8.dp)
                    )
                    Text(
                        text = "Max: ${data.maxLabel}",
                        style = TextStyle(color = ColorProvider(Color(0xFFE8A045)), fontSize = 10.sp)
                    )
                }
            }
        } else {
            // Medium: sparkline only
            Column(
                modifier = GlanceModifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    provider = ImageProvider(data.bitmap),
                    contentDescription = "Weight trend sparkline",
                    modifier = GlanceModifier.fillMaxWidth().height(64.dp)
                )
            }
        }
    }
}
