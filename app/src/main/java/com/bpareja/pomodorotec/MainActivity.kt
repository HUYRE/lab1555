package com.bpareja.pomodorotec

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bpareja.pomodorotec.pomodoro.PomodoroScreen
import com.bpareja.pomodorotec.pomodoro.PomodoroViewModel
import com.bpareja.pomodorotec.pomodoro.PomodoroViewModelFactory

class MainActivity : ComponentActivity() {

    private val pomodoroViewModel: PomodoroViewModel by viewModels {
        PomodoroViewModelFactory(application)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (savedInstanceState != null) {
            val isRunning = savedInstanceState.getBoolean("IS_RUNNING", false)
            val currentPhase = savedInstanceState.getString("CURRENT_PHASE")
            val timeRemaining = savedInstanceState.getLong("TIME_REMAINING")

            pomodoroViewModel.restoreTimerState(isRunning, currentPhase, timeRemaining)
        }

        setContent {
            PomodoroScreen(viewModel = pomodoroViewModel)
        }

        createNotificationChannel()
        requestNotificationPermission()
        handleNewIntent(intent)
    }

    private fun handleNewIntent(newIntent: Intent?) {
        newIntent?.extras?.let { extras ->
            if (extras.getBoolean("FROM_NOTIFICATION", false)) {
                pomodoroViewModel.resumeTimerIfNeeded()
            }
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Canal Pomodoro"
            val descriptionText = "Notificaciones para el temporizador Pomodoro"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this, android.Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(android.Manifest.permission.POST_NOTIFICATIONS),
                    REQUEST_CODE
                )
            }
        }
    }

    fun handleIntentExternally(externalIntent: Intent?) {
        handleNewIntent(externalIntent)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean("IS_RUNNING", pomodoroViewModel.isRunning.value ?: false)
        outState.putString("CURRENT_PHASE", pomodoroViewModel.currentPhase.value?.name)
        outState.putLong("TIME_REMAINING", pomodoroViewModel.timeRemaining)
    }

    companion object {
        const val CHANNEL_ID = "pomodoro_channel"
        private const val REQUEST_CODE = 1
        const val NOTIFICATION_ID = 1
    }
}
