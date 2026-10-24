package com.tanu.leetcodestreak

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

object AppState {

    private const val PREFS = "leetcode_app"

    // =====================================================
    // USER SETUP
    // =====================================================

    private const val NAME = "name"
    private const val SETUP_DONE = "setup_done"

    // =====================================================
    // STREAK
    // =====================================================

    private const val CURRENT_STREAK = "current_streak"
    private const val BEST_STREAK = "best_streak"
    private const val STREAK_DONE = "streak_done"

    // =====================================================
    // DAILY GOALS
    // =====================================================

    private const val EASY_TARGET = "easy_target"
    private const val MEDIUM_TARGET = "medium_target"
    private const val HARD_TARGET = "hard_target"

    private const val EASY_DONE = "easy_done"
    private const val MEDIUM_DONE = "medium_done"
    private const val HARD_DONE = "hard_done"

    // =====================================================
    // DATE
    // =====================================================

    private const val LAST_DATE = "last_date"

    // =====================================================
    // STREAK ALARMS
    // =====================================================

    private const val STREAK_ALARMS = "streak_alarms"

    // =====================================================
    // HOURLY GOAL NOTIFICATION
    // =====================================================

    private const val HOURLY_GOAL_REQUEST_CODE = 9000

    private fun prefs(context: Context) =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)

    // =====================================================
    // USER
    // =====================================================

    fun getName(context: Context): String =
        prefs(context).getString(NAME, "User") ?: "User"

    fun isSetupDone(context: Context): Boolean =
        prefs(context).getBoolean(SETUP_DONE, false)

    // =====================================================
    // STREAK GETTERS
    // =====================================================

    fun getCurrentStreak(context: Context): Int =
        prefs(context).getInt(CURRENT_STREAK, 0)

    fun getBestStreak(context: Context): Int =
        prefs(context).getInt(BEST_STREAK, 0)

    fun isStreakDone(context: Context): Boolean =
        prefs(context).getBoolean(STREAK_DONE, false)

    // =====================================================
    // DAILY TARGET GETTERS
    // =====================================================

    fun getEasyTarget(context: Context): Int =
        prefs(context).getInt(EASY_TARGET, 0)

    fun getMediumTarget(context: Context): Int =
        prefs(context).getInt(MEDIUM_TARGET, 0)

    fun getHardTarget(context: Context): Int =
        prefs(context).getInt(HARD_TARGET, 0)

    // =====================================================
    // DAILY PROGRESS GETTERS
    // =====================================================

    fun getEasyDone(context: Context): Int =
        prefs(context).getInt(EASY_DONE, 0)

    fun getMediumDone(context: Context): Int =
        prefs(context).getInt(MEDIUM_DONE, 0)

    fun getHardDone(context: Context): Int =
        prefs(context).getInt(HARD_DONE, 0)

    // =====================================================
    // INITIAL SETUP
    // =====================================================

    fun setup(
        context: Context,
        name: String,
        startingStreak: Int,
        easy: Int,
        medium: Int,
        hard: Int
    ) {

        prefs(context).edit()
            .putString(NAME, name)
            .putInt(CURRENT_STREAK, startingStreak)
            .putInt(BEST_STREAK, startingStreak)
            .putInt(EASY_TARGET, easy.coerceAtLeast(0))
            .putInt(MEDIUM_TARGET, medium.coerceAtLeast(0))
            .putInt(HARD_TARGET, hard.coerceAtLeast(0))
            .putInt(EASY_DONE, 0)
            .putInt(MEDIUM_DONE, 0)
            .putInt(HARD_DONE, 0)
            .putBoolean(STREAK_DONE, false)
            .putString(LAST_DATE, today())
            .putBoolean(SETUP_DONE, true)
            .apply()

        // Start today's reminders.
        scheduleAllStreakAlarms(context)
        scheduleHourlyGoalNotifications(context)
    }

    // =====================================================
    // UPDATE DAILY GOALS
    // =====================================================

    fun updateGoals(
        context: Context,
        easy: Int,
        medium: Int,
        hard: Int
    ) {

        prefs(context).edit()
            .putInt(EASY_TARGET, easy.coerceAtLeast(0))
            .putInt(MEDIUM_TARGET, medium.coerceAtLeast(0))
            .putInt(HARD_TARGET, hard.coerceAtLeast(0))
            .apply()

        // Recalculate notification state immediately.
        if (allDailyGoalsComplete(context)) {
            cancelHourlyGoalNotifications(context)
        } else {
            scheduleHourlyGoalNotifications(context)
        }
    }

    // =====================================================
    // ADD EASY
    // =====================================================

    fun addEasy(context: Context) {

        val target = getEasyTarget(context)
        val done = getEasyDone(context)

        if (done < target) {

            prefs(context).edit()
                .putInt(EASY_DONE, done + 1)
                .apply()

            updateGoalNotificationState(context)
        }
    }

    // =====================================================
    // ADD MEDIUM
    // =====================================================

    fun addMedium(context: Context) {

        val target = getMediumTarget(context)
        val done = getMediumDone(context)

        if (done < target) {

            prefs(context).edit()
                .putInt(MEDIUM_DONE, done + 1)
                .apply()

            updateGoalNotificationState(context)
        }
    }

    // =====================================================
    // ADD HARD
    // =====================================================

    fun addHard(context: Context) {

        val target = getHardTarget(context)
        val done = getHardDone(context)

        if (done < target) {

            prefs(context).edit()
                .putInt(HARD_DONE, done + 1)
                .apply()

            updateGoalNotificationState(context)
        }
    }

    // =====================================================
    // STREAK COMPLETION
    // =====================================================

    fun completeStreak(context: Context) {

        if (isStreakDone(context)) return

        val current = getCurrentStreak(context) + 1
        val best = maxOf(
            current,
            getBestStreak(context)
        )

        prefs(context).edit()
            .putInt(CURRENT_STREAK, current)
            .putInt(BEST_STREAK, best)
            .putBoolean(STREAK_DONE, true)
            .apply()

        // Streak alarms stop immediately.
        cancelAllStreakAlarms(context)
    }

    // =====================================================
    // NEW DAY
    // =====================================================

    fun resetForNewDay(context: Context) {

        val p = prefs(context)

        val lastDate =
            p.getString(LAST_DATE, "") ?: ""

        val currentDate = today()

        if (lastDate == currentDate) return

        /*
         * Only today's question progress is reset.
         *
         * Current streak and best streak remain unchanged.
         *
         * Example:
         *
         * Yesterday:
         * Easy   1/2
         * Medium 0/0
         * Hard   1/1
         *
         * Today:
         * Easy   0/2
         * Medium 0/0
         * Hard   0/1
         */

        p.edit()
            .putInt(EASY_DONE, 0)
            .putInt(MEDIUM_DONE, 0)
            .putInt(HARD_DONE, 0)
            .putBoolean(STREAK_DONE, false)
            .putString(LAST_DATE, currentDate)
            .apply()

        // Start today's streak alarms again.
        scheduleAllStreakAlarms(context)

        // Start hourly goal notifications again.
        scheduleHourlyGoalNotifications(context)
    }

    // =====================================================
    // RESET DAILY SCHEDULE
    // =====================================================

    fun resetDailySchedule(context: Context) {

        prefs(context).edit()
            .putInt(EASY_DONE, 0)
            .putInt(MEDIUM_DONE, 0)
            .putInt(HARD_DONE, 0)
            .apply()

        scheduleHourlyGoalNotifications(context)
    }

    // =====================================================
    // RESET CURRENT STREAK
    // =====================================================

    fun resetStreak(context: Context) {

        prefs(context).edit()
            .putInt(CURRENT_STREAK, 0)
            .putBoolean(STREAK_DONE, false)
            .apply()

        // Restart configured streak alarms.
        scheduleAllStreakAlarms(context)
    }

    // =====================================================
    // DAILY GOAL STATUS
    // =====================================================

    fun allDailyGoalsComplete(context: Context): Boolean {

        return getEasyDone(context) >= getEasyTarget(context) &&
                getMediumDone(context) >= getMediumTarget(context) &&
                getHardDone(context) >= getHardTarget(context)
    }

    fun anythingLeft(context: Context): Boolean {

        return !allDailyGoalsComplete(context)
    }

    // =====================================================
    // DATE
    // =====================================================

    private fun today(): String {

        return SimpleDateFormat(
            "yyyy-MM-dd",
            Locale.US
        ).format(Date())
    }

    // =====================================================
    // STREAK ALARM TIMES
    // =====================================================

    fun getAlarmTimes(context: Context): MutableList<Int> {

        val stored =
            prefs(context).getString(STREAK_ALARMS, "") ?: ""

        if (stored.isBlank()) {
            return mutableListOf()
        }

        return stored
            .split(",")
            .mapNotNull { it.toIntOrNull() }
            .toMutableList()
    }

    fun saveAlarmTimes(
        context: Context,
        times: List<Int>
    ) {

        val value =
            times.sorted().joinToString(",")

        prefs(context).edit()
            .putString(STREAK_ALARMS, value)
            .apply()

        scheduleAllStreakAlarms(context)
    }

    fun addAlarmTime(
        context: Context,
        hour: Int,
        minute: Int
    ) {

        val minutes = hour * 60 + minute

        val times = getAlarmTimes(context)

        if (!times.contains(minutes)) {
            times.add(minutes)
        }

        saveAlarmTimes(context, times)
    }

    fun removeAlarmTime(
        context: Context,
        minutes: Int
    ) {

        val times = getAlarmTimes(context)

        times.remove(minutes)

        saveAlarmTimes(context, times)
    }

    // =====================================================
    // STREAK ALARMS
    // =====================================================

    fun scheduleAllStreakAlarms(context: Context) {

        cancelAllStreakAlarms(context)

        // If streak question is already completed,
        // there should be no more streak alarms today.
        if (isStreakDone(context)) return

        val alarmManager =
            context.getSystemService(
                Context.ALARM_SERVICE
            ) as AlarmManager

        val times = getAlarmTimes(context)

        for ((index, minutes) in times.withIndex()) {

            val hour = minutes / 60
            val minute = minutes % 60

            val triggerTime =
                nextOccurrence(hour, minute)

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
                        index
                    )
                }

            val pendingIntent =
                PendingIntent.getBroadcast(
                    context,
                    5000 + index,
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT or
                            PendingIntent.FLAG_IMMUTABLE
                )

            alarmManager.setAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                triggerTime,
                pendingIntent
            )
        }
    }

    // =====================================================
    // CANCEL STREAK ALARMS
    // =====================================================

    fun cancelAllStreakAlarms(context: Context) {

        val alarmManager =
            context.getSystemService(
                Context.ALARM_SERVICE
            ) as AlarmManager

        val times = getAlarmTimes(context)

        for (index in times.indices) {

            val intent =
                Intent(
                    context,
                    ReminderReceiver::class.java
                ).apply {

                    putExtra(
                        "type",
                        "streak"
                    )
                }

            val pendingIntent =
                PendingIntent.getBroadcast(
                    context,
                    5000 + index,
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT or
                            PendingIntent.FLAG_IMMUTABLE
                )

            alarmManager.cancel(pendingIntent)
        }
    }

    // =====================================================
    // HOURLY DAILY-GOAL NOTIFICATIONS
    // =====================================================

    fun scheduleHourlyGoalNotifications(context: Context) {

        cancelHourlyGoalNotifications(context)

        // No notification if every scheduled question is complete.
        if (allDailyGoalsComplete(context)) return

        val alarmManager =
            context.getSystemService(
                Context.ALARM_SERVICE
            ) as AlarmManager

        val intent =
            Intent(
                context,
                ReminderReceiver::class.java
            ).apply {

                putExtra(
                    "type",
                    "daily_goal"
                )
            }

        val pendingIntent =
            PendingIntent.getBroadcast(
                context,
                HOURLY_GOAL_REQUEST_CODE,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or
                        PendingIntent.FLAG_IMMUTABLE
            )

        /*
         * Start at the next full hour.
         *
         * Example:
         * 10:25 -> first notification around 11:00
         * 11:25 -> first notification around 12:00
         */

        val calendar = Calendar.getInstance()

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

        /*
         * Hourly notifications continue until
         * all daily goals are completed.
         */
        alarmManager.setInexactRepeating(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            AlarmManager.INTERVAL_HOUR,
            pendingIntent
        )
    }

    // =====================================================
    // CANCEL HOURLY GOAL NOTIFICATIONS
    // =====================================================

    fun cancelHourlyGoalNotifications(context: Context) {

        val alarmManager =
            context.getSystemService(
                Context.ALARM_SERVICE
            ) as AlarmManager

        val intent =
            Intent(
                context,
                ReminderReceiver::class.java
            ).apply {

                putExtra(
                    "type",
                    "daily_goal"
                )
            }

        val pendingIntent =
            PendingIntent.getBroadcast(
                context,
                HOURLY_GOAL_REQUEST_CODE,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or
                        PendingIntent.FLAG_IMMUTABLE
            )

        alarmManager.cancel(pendingIntent)
    }

    // =====================================================
    // UPDATE HOURLY NOTIFICATION STATE
    // =====================================================

    private fun updateGoalNotificationState(context: Context) {

        if (allDailyGoalsComplete(context)) {

            // Everything finished -> stop notifications.
            cancelHourlyGoalNotifications(context)

        } else {

            // Something is still left -> keep notifications running.
            scheduleHourlyGoalNotifications(context)
        }
    }

    // =====================================================
    // NEXT ALARM OCCURRENCE
    // =====================================================

    private fun nextOccurrence(
        hour: Int,
        minute: Int
    ): Long {

        val calendar =
            Calendar.getInstance()

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

        /*
         * If today's alarm time has already passed,
         * schedule it for tomorrow.
         */

        if (
            calendar.timeInMillis <=
            System.currentTimeMillis()
        ) {

            calendar.add(
                Calendar.DAY_OF_YEAR,
                1
            )
        }

        return calendar.timeInMillis
    }
}