package com.example.psalmify

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.Worker
import androidx.work.WorkerParameters

class NotificationWorker(context: Context, workerParams: WorkerParameters) : Worker(context, workerParams) {

    override fun doWork(): Result {
        // Fetch alias from SharedPreferences
        val sharedPreferencesAlias = applicationContext.getSharedPreferences("loginPrefs", Context.MODE_PRIVATE)
        val alias = sharedPreferencesAlias.getString("alias", "")

        // Show notification
        showNotification(alias ?: "")
        return Result.success()
    }

    private fun showNotification(alias: String) {
        val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel("notification_channel", "Notifications", NotificationManager.IMPORTANCE_DEFAULT)
            notificationManager.createNotificationChannel(channel)
        }

        val intent = Intent(applicationContext, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(applicationContext, 0, intent, PendingIntent.FLAG_IMMUTABLE)

        val notificationText = "Binecuvantatule $alias, iti amintim cu multa smerenie ca trebuie sa-ti citesti pildele!"
        val notification = NotificationCompat.Builder(applicationContext, "notification_channel")
            .setContentTitle("Psalmify")
            .setContentText(notificationText)
            .setSmallIcon(R.drawable.app_icon)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(1, notification)
    }
}