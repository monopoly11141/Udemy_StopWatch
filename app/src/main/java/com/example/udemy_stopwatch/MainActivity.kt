package com.example.udemy_stopwatch

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.udemy_stopwatch.databinding.ActivityMainBinding
import kotlin.math.roundToInt

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var isStarted = false
    private lateinit var serviceIntent: Intent
    private var time = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnStartAndStop.setOnClickListener {
            startOrStop()
        }

        binding.btnRestart.setOnClickListener {
            reset()
        }

        serviceIntent = Intent(applicationContext, StopWatchService::class.java)
        registerReceiver(updateTime, IntentFilter(StopWatchService.UPDATED_TIME))

    }

    private fun startOrStop() {
        if (isStarted) {
            stop()
        } else {
            start()
        }
        isStarted = !isStarted
    }

    private fun start() {
        serviceIntent.putExtra(StopWatchService.CURRENT_TIME, time)
        startService(serviceIntent)

        binding.btnStartAndStop.text = "Stop"
    }

    private fun stop() {
        stopService(serviceIntent)

        binding.btnStartAndStop.text = "Start"
    }

    private fun reset() {
        stop()
        isStarted = false
        time = 0.0
        binding.tvTimeElapsed.text = getFormattedTime(time)
    }

    private val updateTime: BroadcastReceiver = object : BroadcastReceiver() {

        override fun onReceive(context: Context, intent: Intent) {
            time = intent.getDoubleExtra(StopWatchService.CURRENT_TIME, 0.0)
            binding.tvTimeElapsed.text = getFormattedTime(time)

        }

    }

    private fun getFormattedTime(time: Double): String {
        val timeInt = time.roundToInt()

        val secondString = timeInt % 60
        var minuteString = timeInt / 60
        var hourString = 0
        if (minuteString >= 60) {
            hourString = minuteString / 60
            minuteString %= 60
        }
        return String.format("%02d:%02d:%02d", hourString, minuteString, secondString)
    }

}