package me.hauthorn.byvejr

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.bumptech.glide.Glide

class WidgetWorker(ctx: Context, workerParams: WorkerParameters) : Worker(ctx, workerParams) {
    override fun doWork(): Result {
        val context = applicationContext

        Glide.get(context).clearMemory()

        val appWidgetManager = AppWidgetManager.getInstance(context)
        val appWidgetIds = appWidgetManager.getAppWidgetIds(ComponentName(context, WeatherWidget::class.java))
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
        return Result.success()
    }
}