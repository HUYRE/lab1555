package com.bpareja.pomodorotec

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews

class PomodoroWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        // Actualiza cada instancia del widget
        appWidgetIds.forEach { appWidgetId ->
            updateWidget(context, appWidgetManager, appWidgetId)
        }
    }

    companion object {
        private const val PREFS_NAME = "PomodoroPrefs"
        private const val PROGRESS_KEY = "Progress"
        private const val TIME_REMAINING_KEY = "TimeRemaining"

        fun updateWidget(context: Context, appWidgetManager: AppWidgetManager, appWidgetId: Int) {
            // Obtener valores de SharedPreferences
            val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            val progress = prefs.getInt(PROGRESS_KEY, 0) // Valor por defecto es 0
            val timeRemaining = prefs.getString(TIME_REMAINING_KEY, "00:00:00") ?: "00:00:00"

            // Configura la vista remota para el widget
            val views = RemoteViews(context.packageName, R.layout.widget_pomodoro)

            // Actualiza el progreso y el tiempo restante en el widget
            views.setTextViewText(R.id.widget_timer_phase, "Focus Session") // O tu lógica para la fase
            views.setProgressBar(R.id.widget_timer_progress, 100, progress, false)
            views.setTextViewText(R.id.widget_timer_time_remaining, timeRemaining)

            // Define un intent para actualizar el widget al tocarlo
            val intent = Intent(context, MainActivity::class.java)
            val pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
            views.setOnClickPendingIntent(R.id.widget_timer_phase, pendingIntent)

            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }

    // Mueve la función dentro de la clase
    fun refreshAllWidgets(context: Context) {
        val appWidgetManager = AppWidgetManager.getInstance(context)
        val componentName = ComponentName(context, PomodoroWidgetProvider::class.java)
        val appWidgetIds = appWidgetManager.getAppWidgetIds(componentName)
        appWidgetIds.forEach { updateWidget(context, appWidgetManager, it) }
    }
}
