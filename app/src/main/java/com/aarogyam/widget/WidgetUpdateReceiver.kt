package com.aarogyam.widget

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.aarogyam.widget.minimal.MinimalWidget
import com.aarogyam.widget.progress.ProgressWidget
import com.aarogyam.widget.trend.TrendWidget
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class WidgetUpdateReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == ACTION_WIDGET_UPDATE) {
            val appContext = context.applicationContext
            CoroutineScope(Dispatchers.IO).launch {
                MinimalWidget().update(appContext)
                ProgressWidget().update(appContext)
                TrendWidget().update(appContext)
            }
        }
    }

    companion object {
        const val ACTION_WIDGET_UPDATE = "com.aarogyam.WIDGET_UPDATE"

        fun send(context: Context) {
            val intent = Intent(ACTION_WIDGET_UPDATE).apply {
                setPackage(context.packageName)
            }
            context.sendBroadcast(intent)
        }
    }
}
