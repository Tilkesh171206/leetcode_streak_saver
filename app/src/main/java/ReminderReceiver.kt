package com.tanu.leetcodestreak

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import java.util.Calendar

class ReminderReceiver : BroadcastReceiver() {

    override fun onReceive(
        context: Context,
        intent: Intent?
    ) {

        AppState.resetForNewDay(context)

        val type =
            intent?.getStringExtra("type")
                ?: "hourly_goal"

        when (type) {

            "streak" -> {

                if (!AppState.isStreakDone(context)) {

                    showStreakNotification(context)

                    /*
                     * Schedule this same alarm again
                     * for tomorrow.
                     */
                    val alarmId =
                        intent?.getIntExtra(
                            "alarm_id",
                            0
                        ) ?: 0

                    scheduleNextStreakAlarm(
                        context,
                        alarmId
                    )
                }
            }

            "hourly_goal" -> {

                if (AppState.anythingLeft(context)) {

                    showGoalNotification(context)

                    scheduleNextHourlyNotification(
                        context
                    )
                }
            }
        }
    }

    private fun showStreakNotification(
        context: Context
    ) {

        createChannel(context)

        val name =
            AppState.getName(context)

        val notification =
            NotificationCompat.Builder(
                context,
                "leetcode_reminders"
            )
                .setSmallIcon(
                    android.R.drawable.ic_dialog_info
                )
                .setContentTitle(
                    "🔥 Don't break your streak, $name!"
                )
                .setContentText(
                    "Today's streak question is waiting for you."
                )
                .setPriority(
                    NotificationCompat.PRIORITY_HIGH
                )
                .setAutoCancel(true)
                .build()

        val manager =
            context.getSystemService(
                Context.NOTIFICATION_SERVICE
            ) as NotificationManager

        manager.notify(
            1001,
            notification
        )
    }

    private fun showGoalNotification(
        context: Context
    ) {

        createChannel(context)

        val easyLeft =
            AppState.getEasyTarget(context) -
                    AppState.getEasyDone(context)

        val mediumLeft =
            AppState.getMediumTarget(context) -
                    AppState.getMediumDone(context)

        val hardLeft =
            AppState.getHardTarget(context) -
                    AppState.getHardDone(context)

        val totalLeft =
            easyLeft.coerceAtLeast(0) +
                    mediumLeft.coerceAtLeast(0) +
                    hardLeft.coerceAtLeast(0)

        val name =
            AppState.getName(context)

        val parts = mutableListOf<String>()

        if (easyLeft > 0) {
            parts.add(
                "$easyLeft Easy"
            )
        }

        if (mediumLeft > 0) {
            parts.add(
                "$mediumLeft Medium"
            )
        }

        if (hardLeft > 0) {
            parts.add(
                "$hardLeft Hard"
            )
        }

        val remaining =
            parts.joinToString(" • ")

        val message =
            when {

                totalLeft == 1 ->
                    "Only 1 question left. Finish it! 🔥"

                else ->
                    "$totalLeft questions left: $remaining"
            }

        val notification =
            NotificationCompat.Builder(
                context,
                "leetcode_reminders"
            )
                .setSmallIcon(
                    android.R.drawable.ic_dialog_info
                )
                .setContentTitle(
                    "🎯 Keep going, $name!"
                )
                .setContentText(message)
                .setPriority(
                    NotificationCompat.PRIORITY_HIGH
                )
                .setAutoCancel(true)
                .build()

        val manager =
            context.getSystemService(
                Context.NOTIFICATION_SERVICE
            ) as NotificationManager

        manager.notify(
            1002,
            notification
        )
    }

    private fun createChannel(
        context: Context
    ) {

        val channel =
            NotificationChannel(
                "leetcode_reminders",
                "LeetCode Reminders",
                NotificationManager.IMPORTANCE_HIGH
            )

        val manager =
            context.getSystemService(
                Context.NOTIFICATION_SERVICE
            ) as NotificationManager

        manager.createNotificationChannel(
            channel
        )
    }

    private fun scheduleNextHourlyNotification(
        context: Context
    ) {

        val alarmManager =
            context.getSystemService(
                Context.ALARM_SERVICE
            ) as android.app.AlarmManager

        val intent =
            Intent(
                context,
                ReminderReceiver::class.java
            ).apply {
                putExtra(
                    "type",
                    "hourly_goal"
                )
            }

        val pendingIntent =
            android.app.PendingIntent.getBroadcast(
                context,
                9000,
                intent,
                android.app.PendingIntent.FLAG_UPDATE_CURRENT or
                        android.app.PendingIntent.FLAG_IMMUTABLE
            )

        val calendar =
            Calendar.getInstance()

        calendar.add(
            Calendar.HOUR_OF_DAY,
            1
        )

        calendar.set(
            Calendar.MINUTE,
            0
        )

        calendar.set(
            Calendar.SECOND,
            0
        )

        calendar.set(
            Calendar.MILLISECOND,
            0
        )

        alarmManager.setAndAllowWhileIdle(
            android.app.AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            pendingIntent
        )
    }

    private fun scheduleNextStreakAlarm(
        context: Context,
        alarmId: Int
    ) {

        val times =
            AppState.getAlarmTimes(context)

        if (alarmId >= times.size) return

        val minutes =
            times[alarmId]

        val hour =
            minutes / 60

        val minute =
            minutes % 60

        val calendar =
            Calendar.getInstance()

        calendar.add(
            Calendar.DAY_OF_YEAR,
            1
        )

        calendar.set(
            Calendar.HOUR_OF_DAY,
            hour
        )

        calendar.set(
            Calendar.MINUTE,
            minute
        )

        calendar.set(
            Calendar.SECOND,
            0
        )

        calendar.set(
            Calendar.MILLISECOND,
            0
        )

        val intent =
            Intent(
                context,
                ReminderReceiver::class.java
            ).apply {
                putExtra(
                    "type",
                    "streak"
                )

                putExtra(
                    "alarm_id",
                    alarmId
                )
            }

        val pendingIntent =
            android.app.PendingIntent.getBroadcast(
                context,
                5000 + alarmId,
                intent,
                android.app.PendingIntent.FLAG_UPDATE_CURRENT or
                        android.app.PendingIntent.FLAG_IMMUTABLE
            )

        val alarmManager =
            context.getSystemService(
                Context.ALARM_SERVICE
            ) as android.app.AlarmManager

        alarmManager.setAndAllowWhileIdle(
            android.app.AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            pendingIntent
        )
    }
}