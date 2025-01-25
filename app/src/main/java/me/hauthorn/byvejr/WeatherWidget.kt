package me.hauthorn.byvejr

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.widget.RemoteViews
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.AppWidgetTarget
import java.util.concurrent.TimeUnit


class WeatherWidget : AppWidgetProvider() {
    private val widgetWorkKey = "WidgetUpdateWork"

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        // There may be multiple widgets active, so update all of them
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }


    override fun onEnabled(context: Context) {
        val workRequest = PeriodicWorkRequestBuilder<WidgetWorker>(2, TimeUnit.HOURS)
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()
            )
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            widgetWorkKey,
            ExistingPeriodicWorkPolicy.KEEP,
            workRequest
        )
    }

    override fun onDisabled(context: Context) {
        // Disable the periodic work
        WorkManager.getInstance(context).cancelUniqueWork(widgetWorkKey)
    }
}

internal fun updateAppWidget(
    context: Context,
    appWidgetManager: AppWidgetManager,
    appWidgetId: Int
) {
    // Construct the RemoteViews object
    val views = RemoteViews(context.packageName, R.layout.weather_widget)
    val appWidgetTarget = AppWidgetTarget(context, R.id.weather_widget_view, views, appWidgetId)

    val sharedPref = context.getSharedPreferences("me.hauthorn.byvejr", Context.MODE_PRIVATE)
    val zip = sharedPref.getString("zip", "7500") ?: "7500"

    Glide.with(context)
        .asBitmap()
        .load("https://www.klartvejr.dk/kort/$zip")
        .into(appWidgetTarget)

    // Instruct the widget manager to update the widget
    appWidgetManager.updateAppWidget(appWidgetId, views)
}