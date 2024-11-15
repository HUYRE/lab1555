package com.bpareja.pomodorotec.pomodoro

import android.app.Application
import android.os.CountDownTimer
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

enum class Phase {
    FOCUS, BREAK
}

class PomodoroViewModel(application: Application) : AndroidViewModel(application) {
    private val context = getApplication<Application>().applicationContext

    private val _timeLeft = MutableLiveData("25:00")
    val timeLeft: LiveData<String> = _timeLeft

    private val _isRunning = MutableLiveData(false)
    val isRunning: LiveData<Boolean> = _isRunning

    private val _currentPhase = MutableLiveData(Phase.FOCUS)
    val currentPhase: LiveData<Phase> = _currentPhase

    private val _progress = MutableLiveData(0)
    val progress: LiveData<Int> = _progress

    private var countDownTimer: CountDownTimer? = null
    var timeRemaining: Long = 25 * 60 * 1000L // Tiempo restante del temporizador en milisegundos
    private val TOTAL_TIME = 25 * 60 * 1000L // Tiempo total de la fase de enfoque en milisegundos

    fun startFocusSession() {
        _currentPhase.value = Phase.FOCUS
        startTimer(TOTAL_TIME)
    }

    fun startBreakSession() {
        _currentPhase.value = Phase.BREAK
        startTimer(5 * 60 * 1000L) // Fase de descanso de 5 minutos
    }

    private fun startTimer(timeMillis: Long) {
        _isRunning.value = true
        timeRemaining = timeMillis

        countDownTimer?.cancel()
        countDownTimer = object : CountDownTimer(timeMillis, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                timeRemaining = millisUntilFinished
                val minutes = (millisUntilFinished / 1000) / 60
                val seconds = (millisUntilFinished / 1000) % 60
                _timeLeft.value = String.format("%02d:%02d", minutes, seconds)
                _progress.value = ((TOTAL_TIME - millisUntilFinished).toDouble() / TOTAL_TIME * 100).toInt()
            }

            override fun onFinish() {
                _isRunning.value = false
                _timeLeft.value = "00:00"
                _progress.value = 100
                // Cambiar a la siguiente fase o enviar una notificación al usuario
            }
        }.start()
    }

    fun pauseTimer() {
        _isRunning.value = false
        countDownTimer?.cancel()
    }

    fun resetTimer() {
        _isRunning.value = false
        countDownTimer?.cancel()
        _timeLeft.value = "25:00"
        _progress.value = 0
        timeRemaining = TOTAL_TIME
    }

    fun restoreTimerState(isRunning: Boolean, phase: String?, timeRemaining: Long) {
        _isRunning.value = isRunning
        _currentPhase.value = if (phase == Phase.BREAK.name) Phase.BREAK else Phase.FOCUS
        this.timeRemaining = timeRemaining

        val minutes = (timeRemaining / 1000) / 60
        val seconds = (timeRemaining / 1000) % 60
        _timeLeft.value = String.format("%02d:%02d", minutes, seconds)

        if (isRunning) {
            startTimer(timeRemaining) // Reiniciar el temporizador si estaba en ejecución
        }
    }

    fun resumeTimerIfNeeded() {
        if (isRunning.value == true) {
            startTimer(timeRemaining) // Reanudar el temporizador si estaba corriendo
        }
    }
}
