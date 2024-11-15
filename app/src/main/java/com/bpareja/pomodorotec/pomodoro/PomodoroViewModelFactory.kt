package com.bpareja.pomodorotec.pomodoro

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class PomodoroViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PomodoroViewModel::class.java)) {
            return PomodoroViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
