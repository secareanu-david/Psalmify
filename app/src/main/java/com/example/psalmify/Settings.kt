package com.example.psalmify

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.SeekBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.SwitchCompat
import androidx.core.app.NotificationCompat
import androidx.fragment.app.Fragment
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

class Settings : Fragment() {

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var sharedPreferencesAlias: SharedPreferences
    private var isNotificationEnabled = false
    private var notificationInterval = 15 // Default interval in minutes (15 minutes)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sharedPreferences = requireActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        sharedPreferencesAlias = requireActivity().getSharedPreferences("loginPrefs", Context.MODE_PRIVATE)

        // Theme switch setup
        val themeSwitch = view.findViewById<SwitchCompat>(R.id.theme_switch)
        val isDarkMode = sharedPreferences.getBoolean(THEME_KEY, false)
        themeSwitch.isChecked = isDarkMode
        themeSwitch.setOnCheckedChangeListener { _, isChecked ->
            sharedPreferences.edit().putBoolean(THEME_KEY, isChecked).apply()
            applyTheme(isChecked)
        }

        // Notification settings setup
        val notificationSwitch = view.findViewById<SwitchCompat>(R.id.notification_background)
        val notificationSeekBar = view.findViewById<SeekBar>(R.id.search_bar)
        val intervalTextView = view.findViewById<TextView>(R.id.text)

        // Initialize notification settings
        isNotificationEnabled = sharedPreferences.getBoolean(NOTIFICATION_ENABLED_KEY, false)
        notificationInterval = sharedPreferences.getInt(NOTIFICATION_INTERVAL_KEY, 15) // Default interval of 15 minutes
        notificationSwitch.isChecked = isNotificationEnabled
        notificationSeekBar.progress = notificationInterval - 15 // Start from 15 minutes
        intervalTextView.text = "Interval notificari: $notificationInterval minute"

        if(SyncManager.isGuest){
            notificationSwitch.isEnabled = false
            notificationSeekBar.isEnabled = false
        }

        notificationSwitch.setOnCheckedChangeListener { _, isChecked ->
            isNotificationEnabled = isChecked
            sharedPreferences.edit().putBoolean(NOTIFICATION_ENABLED_KEY, isChecked).apply()

            if (isChecked) {
                // Set an initial delayed notification
                //val initialDelay = notificationInterval * 60L // Delay for the first notification in seconds (converted to seconds)
                val initialDelay = 10L
                // Set the periodic notifications to repeat after the specified interval
                val periodicWorkRequest = PeriodicWorkRequestBuilder<NotificationWorker>(
                    notificationInterval.toLong(), TimeUnit.MINUTES
                ).setInitialDelay(initialDelay, TimeUnit.SECONDS)
                    .build()
                WorkManager.getInstance(requireContext()).enqueueUniquePeriodicWork(
                    "NotificationWork",
                    ExistingPeriodicWorkPolicy.UPDATE,
                    periodicWorkRequest
                )
            } else {
                WorkManager.getInstance(requireContext()).cancelUniqueWork("NotificationWork")
            }
        }

        // Update seekbar range to be between 15 and 60 minutes
        notificationSeekBar.max = 45 // 60 minutes - 15 minutes
        notificationSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                notificationInterval = progress + 15 // Offset by 15 to make it 15-60 minutes
                intervalTextView.text = "Interval notificari: $notificationInterval minute"
                sharedPreferences.edit().putInt(NOTIFICATION_INTERVAL_KEY, notificationInterval).apply()

                if (isNotificationEnabled) {
                    // Cancel the existing work and set the new notification interval
                    WorkManager.getInstance(requireContext()).cancelUniqueWork("NotificationWork")

                    // Set an initial delayed notification
                    //val initialDelay = notificationInterval * 60L // Delay for the first notification in seconds (converted to seconds)
                    val initialDelay = 10L

                    // Set the periodic notifications to repeat after the specified interval
                    val periodicWorkRequest = PeriodicWorkRequestBuilder<NotificationWorker>(
                        notificationInterval.toLong(), TimeUnit.MINUTES
                    ).setInitialDelay(initialDelay, TimeUnit.SECONDS)
                        .build()
                    WorkManager.getInstance(requireContext()).enqueueUniquePeriodicWork(
                        "NotificationWork",
                        ExistingPeriodicWorkPolicy.UPDATE,
                        periodicWorkRequest
                    )
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
    }

    private fun applyTheme(isDarkMode: Boolean) {
        if (isDarkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            requireActivity().setTheme(R.style.AppTheme_Dark)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            requireActivity().setTheme(R.style.AppTheme_Light)
        }
    }

    companion object {
        const val PREFS_NAME = "app_prefs"
        const val THEME_KEY = "theme"
        const val NOTIFICATION_ENABLED_KEY = "notification_enabled"
        const val NOTIFICATION_INTERVAL_KEY = "notification_interval"
    }
}
