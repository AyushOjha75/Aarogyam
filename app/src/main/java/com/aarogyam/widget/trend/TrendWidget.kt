package com.aarogyam.widget.trend

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
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

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val bitmap = safeDbRead(context)
        provideContent {
            TrendWidgetContent(bitmap)
        }
    }

    private suspend fun safeDbRead(context: Context): Bitmap? {
        return try {
            val repo = WeightRepository.getInstance(context)
            val logs = repo.getLastN(10)
            if (logs.size < 2) return null
            val unit = repo.weightUnit.first()
            val points = logs.sortedBy { it.loggedAt }
                .map { UnitConverter.toDisplay(it.weightKg, unit).toFloat() }
            buildSparklineBitmap(points)
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

@Composable
private fun TrendWidgetContent(bitmap: Bitmap?) {
    Box(
        modifier = GlanceModifier
            .fillMaxSize()
            .background(Color(0xFF2C2C32))
            .padding(8.dp),
        contentAlignment = Alignment.Center
    ) {
        if (bitmap == null) {
            Text(
                text = "Log entries to see trend",
                style = TextStyle(color = ColorProvider(Color(0xFF8A8A96)), fontSize = 12.sp)
            )
        } else {
            Column(
                modifier = GlanceModifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    provider = ImageProvider(bitmap),
                    contentDescription = "Weight trend sparkline",
                    modifier = GlanceModifier.fillMaxWidth().height(64.dp)
                )
            }
        }
    }
}
