# 🔥 LeetCode Streak

A simple Android app I built for myself to stay consistent with daily LeetCode practice.

The main purpose is simple: **don't forget the daily LeetCode question and keep the streak alive.**

## 📱 Download

**[⬇️ Download the latest APK](../../releases/latest)**

> Android only.

## 🎯 Why I Built This

I kept forgetting to complete my daily LeetCode question, which made it difficult to maintain a consistent streak.

Instead of using an existing app, I decided to build a small personal solution that gives me reminders throughout the day and tracks my progress.

The app is intentionally simple and does not connect to LeetCode.

## ✨ Features

### 🔥 LeetCode Streak

* Track your current streak.
* Set your starting streak when you first install the app.
* Mark the daily streak question as completed.
* Streak alarms stop automatically after completing the streak question.
* Configure your own streak alarm times.

### 🎯 Daily Goals

Set your own daily targets for:

* 🟢 Easy
* 🟡 Medium
* 🔴 Hard

For example:

```text
Easy:   2
Medium: 2
Hard:   1
```

You can also set any category to `0`.

### 🔔 Smart Notifications

The app sends hourly notifications for unfinished daily goals.

The notification changes according to your progress.

For example:

```text
Easy:   1 / 2
Medium: 2 / 2
Hard:   0 / 1
```

The app knows that you still have:

```text
1 Easy
1 Hard
```

Once all daily goals are completed, the hourly notifications stop.

### ⏰ Custom Streak Alarms

You can choose when you want to be reminded about your streak question.

For example:

```text
10:00 AM
2:00 PM
6:00 PM
10:00 PM
11:00 PM
```

These alarms are specifically for the **streak question**.

The daily Easy/Medium/Hard goals use hourly notifications instead.

### 🔄 Daily Reset

Daily question progress resets for the new day.

For example, if yesterday you completed:

```text
Easy:   1 / 2
Medium: 0 / 0
Hard:   1 / 1
```

the next day starts at:

```text
Easy:   0 / 2
Medium: 0 / 0
Hard:   0 / 1
```

Your streak is not reset when the daily schedule resets.

### 🧹 Reset Options

You can manually reset:

* Daily schedule progress
* Current streak

## 🛠️ Tech Stack

* **Kotlin**
* **Jetpack Compose**
* **Material 3**
* **Android SDK**
* **Gradle**
* **AlarmManager**
* **BroadcastReceiver**
* **SharedPreferences**

## 📂 Project Structure

```text
leetcode-streak/
│
├── app/
│   └── src/
│       └── main/
│           ├── java/
│           │   └── com/tanu/leetcodestreak/
│           │       ├── MainActivity.kt
│           │       ├── AppState.kt
│           │       └── ReminderReceiver.kt
│           │
│           └── res/
│
├── gradle/
├── app-release.apk
├── build.gradle.kts
├── settings.gradle.kts
├── gradlew
├── gradlew.bat
└── README.md
```

## 🚀 Installation

1. Download the latest `app-release.apk`.
2. Open the APK on your Android phone.
3. If Android asks for permission to install from that source, allow it.
4. Install the app.
5. Set your name, starting streak, and daily goals.
6. Configure your streak alarms.
7. Start solving.

## 🔒 Privacy

This app is designed as a simple personal productivity tool.

It does **not** require a LeetCode account and does not connect to the LeetCode API.

Your streak, goals, and progress are stored locally on the device.

## 💡 Example

A typical setup could be:

```text
🔥 Current streak: 15 days

Daily goals:
🟢 Easy:   2
🟡 Medium: 1
🔴 Hard:   1

Streak alarms:
10:00 AM
2:00 PM
6:00 PM
10:00 PM
```

The goal is simple:

**Solve → Track → Keep the streak alive. 🔥**

## 📌 Note

This is a personal project built for my own use. It is intentionally kept simple instead of trying to replace or integrate with LeetCode.
