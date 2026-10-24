package com.tanu.leetcodestreak

import android.Manifest
import android.app.TimePickerDialog
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material3.ExperimentalMaterial3Api
import com.tanu.leetcodestreak.ui.theme.LeetCodeStreakTheme
import java.util.Calendar
import java.util.Locale

class MainActivity : ComponentActivity() {

    private val notificationPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Android 13+ notification permission
        if (
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            checkSelfPermission(
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            notificationPermissionLauncher.launch(
                Manifest.permission.POST_NOTIFICATIONS
            )
        }

        // Make sure today's progress is correct
        AppState.resetForNewDay(this)

        setContent {
            LeetCodeStreakTheme {
                LeetCodeStreakApp(this)
            }
        }
    }
}

/* =========================================================
   MAIN APP
   ========================================================= */

@Composable
fun LeetCodeStreakApp(context: Context) {

    var setupDone by remember {
        mutableStateOf(
            AppState.isSetupDone(context)
        )
    }

    if (!setupDone) {

        SetupScreen(
            context = context,
            onFinished = {
                setupDone = true

                AppState.scheduleAllStreakAlarms(context)
            }
        )

    } else {

        HomeScreen(context)
    }
}

/* =========================================================
   FIRST TIME SETUP
   ========================================================= */

@Composable
fun SetupScreen(
    context: Context,
    onFinished: () -> Unit
) {

    var name by remember {
        mutableStateOf("")
    }

    var streak by remember {
        mutableStateOf("0")
    }

    var easy by remember {
        mutableIntStateOf(2)
    }

    var medium by remember {
        mutableIntStateOf(2)
    }

    var hard by remember {
        mutableIntStateOf(1)
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        item {

            Spacer(
                modifier = Modifier.height(35.dp)
            )

            Text(
                text = "🔥",
                fontSize = 52.sp
            )

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            Text(
                text = "LeetCode Streak",
                fontSize = 30.sp,
                fontWeight = FontWeight.ExtraBold
            )

            Text(
                text = "Build the habit. Protect the streak.",
                fontSize = 14.sp
            )

            Spacer(
                modifier = Modifier.height(28.dp)
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp)
            ) {

                Column(
                    modifier = Modifier.padding(20.dp)
                ) {

                    Text(
                        text = "Let's get you started",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(
                        modifier = Modifier.height(16.dp)
                    )

                    androidx.compose.material3.OutlinedTextField(
                        value = name,
                        onValueChange = {
                            name = it
                        },
                        modifier = Modifier.fillMaxWidth(),
                        label = {
                            Text("Your name")
                        },
                        placeholder = {
                            Text("e.g. Tanu")
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(14.dp)
                    )

                    Spacer(
                        modifier = Modifier.height(12.dp)
                    )

                    androidx.compose.material3.OutlinedTextField(
                        value = streak,
                        onValueChange = {
                            if (it.all { char -> char.isDigit() }) {
                                streak = it
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        label = {
                            Text("Current streak")
                        },
                        placeholder = {
                            Text("e.g. 17")
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(14.dp)
                    )
                }
            }

            Spacer(
                modifier = Modifier.height(16.dp)
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp)
            ) {

                Column(
                    modifier = Modifier.padding(20.dp)
                ) {

                    Text(
                        text = "🎯 Daily Goal",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = "Choose how many questions you want each day.",
                        fontSize = 13.sp
                    )

                    Spacer(
                        modifier = Modifier.height(15.dp)
                    )

                    NumberRow(
                        emoji = "🟢",
                        title = "Easy",
                        value = easy,
                        onChange = {
                            easy = it
                        }
                    )

                    NumberRow(
                        emoji = "🟡",
                        title = "Medium",
                        value = medium,
                        onChange = {
                            medium = it
                        }
                    )

                    NumberRow(
                        emoji = "🔴",
                        title = "Hard",
                        value = hard,
                        onChange = {
                            hard = it
                        }
                    )
                }
            }

            Spacer(
                modifier = Modifier.height(22.dp)
            )

            Button(
                onClick = {

                    AppState.setup(
                        context = context,
                        name = name.trim(),
                        startingStreak =
                            streak.toIntOrNull() ?: 0,
                        easy = easy,
                        medium = medium,
                        hard = hard
                    )

                    onFinished()
                },
                enabled = name.isNotBlank(),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(55.dp),
                shape = RoundedCornerShape(16.dp)
            ) {

                Text(
                    text = "START MY STREAK 🔥",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(
                modifier = Modifier.height(30.dp)
            )
        }
    }
}

/* =========================================================
   HOME
   ========================================================= */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    context: Context
) {

    var refresh by remember {
        mutableIntStateOf(0)
    }

    var settingsOpen by remember {
        mutableStateOf(false)
    }

    if (settingsOpen) {

        SettingsScreen(
            context = context,
            onBack = {
                settingsOpen = false
                refresh++
            }
        )

        return
    }

    // Causes recomposition after button presses
    val currentRefresh = refresh

    @Suppress("UNUSED_VARIABLE")
    val unused = currentRefresh

    val name =
        AppState.getName(context)

    val streak =
        AppState.getCurrentStreak(context)

    val best =
        AppState.getBestStreak(context)

    val easyTarget =
        AppState.getEasyTarget(context)

    val mediumTarget =
        AppState.getMediumTarget(context)

    val hardTarget =
        AppState.getHardTarget(context)

    val easyDone =
        AppState.getEasyDone(context)

    val mediumDone =
        AppState.getMediumDone(context)

    val hardDone =
        AppState.getHardDone(context)

    val streakDone =
        AppState.isStreakDone(context)

    val totalGoal =
        easyTarget +
                mediumTarget +
                hardTarget

    val totalDone =
        easyDone +
                mediumDone +
                hardDone

    val remaining =
        (totalGoal - totalDone)
            .coerceAtLeast(0)

    val progress =
        if (totalGoal == 0) {
            1f
        } else {
            (totalDone.toFloat() /
                    totalGoal.toFloat())
                .coerceIn(0f, 1f)
        }

    val allCompleted =
        AppState.allDailyGoalsComplete(context)

    Scaffold(

        topBar = {

            TopAppBar(

                title = {

                    Column {

                        Text(
                            text = "LeetCode Streak",
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            text = "Stay consistent. Keep coding.",
                            fontSize = 12.sp
                        )
                    }
                },

                actions = {

                    TextButton(
                        onClick = {
                            settingsOpen = true
                        }
                    ) {

                        Text(
                            text = "⚙️",
                            fontSize = 22.sp
                        )
                    }
                },

                colors =
                    TopAppBarDefaults.topAppBarColors(
                        containerColor =
                            MaterialTheme
                                .colorScheme
                                .background
                    )
            )
        }

    ) { padding ->

        LazyColumn(

            modifier = Modifier
                .fillMaxSize()
                .background(
                    MaterialTheme
                        .colorScheme
                        .background
                )
                .padding(padding)
                .padding(horizontal = 16.dp),

            verticalArrangement =
                Arrangement.spacedBy(14.dp)
        ) {

            item {

                Spacer(
                    modifier = Modifier.height(6.dp)
                )

                Text(
                    text = "Hey, $name 👋",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            /* =================================================
               STREAK CARD
               ================================================= */

            item {

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(26.dp),
                    colors = CardDefaults.cardColors(
                        containerColor =
                            MaterialTheme
                                .colorScheme
                                .primaryContainer
                    )
                ) {

                    Column(
                        modifier = Modifier.padding(24.dp)
                    ) {

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement =
                                Arrangement.SpaceBetween,
                            verticalAlignment =
                                Alignment.CenterVertically
                        ) {

                            Column {

                                Text(
                                    text = "CURRENT STREAK",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )

                                Spacer(
                                    modifier =
                                        Modifier.height(3.dp)
                                )

                                Text(
                                    text = "$streak DAYS",
                                    fontSize = 36.sp,
                                    fontWeight = FontWeight.ExtraBold
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .size(65.dp)
                                    .clip(CircleShape)
                                    .background(
                                        MaterialTheme
                                            .colorScheme
                                            .primary
                                    ),
                                contentAlignment =
                                    Alignment.Center
                            ) {

                                Text(
                                    text = "🔥",
                                    fontSize = 30.sp
                                )
                            }
                        }

                        Spacer(
                            modifier =
                                Modifier.height(14.dp)
                        )

                        Text(
                            text =
                                "Best streak: $best days",
                            fontSize = 13.sp
                        )

                        Spacer(
                            modifier =
                                Modifier.height(4.dp)
                        )

                        Text(
                            text =
                                if (streakDone)
                                    "✓ Today's streak is safe!"
                                else
                                    "Don't break the chain today!",
                            fontWeight =
                                FontWeight.SemiBold
                        )
                    }
                }
            }

            /* =================================================
               STREAK QUESTION
               ================================================= */

            item {

                Card(
                    modifier =
                        Modifier.fillMaxWidth(),
                    shape =
                        RoundedCornerShape(22.dp)
                ) {

                    Column(
                        modifier =
                            Modifier.padding(20.dp)
                    ) {

                        Row(
                            verticalAlignment =
                                Alignment.CenterVertically
                        ) {

                            Text(
                                text = "🔥",
                                fontSize = 27.sp
                            )

                            Spacer(
                                modifier =
                                    Modifier.width(10.dp)
                            )

                            Column {

                                Text(
                                    text =
                                        "Today's Streak Question",
                                    fontSize = 19.sp,
                                    fontWeight =
                                        FontWeight.Bold
                                )

                                Text(
                                    text =
                                        if (streakDone)
                                            "Completed for today ✓"
                                        else
                                            "One question to protect your streak."
                                )
                            }
                        }

                        Spacer(
                            modifier =
                                Modifier.height(16.dp)
                        )

                        Button(
                            onClick = {

                                AppState.completeStreak(
                                    context
                                )

                                refresh++
                            },

                            enabled = !streakDone,

                            modifier =
                                Modifier.fillMaxWidth(),

                            shape =
                                RoundedCornerShape(14.dp)
                        ) {

                            Text(
                                text =
                                    if (streakDone)
                                        "✓ STREAK COMPLETED"
                                    else
                                        "I DID IT! 🔥",

                                fontSize = 15.sp,
                                fontWeight =
                                    FontWeight.Bold
                            )
                        }
                    }
                }
            }

            /* =================================================
               DAILY GOALS
               ================================================= */

            item {

                Card(
                    modifier =
                        Modifier.fillMaxWidth(),
                    shape =
                        RoundedCornerShape(22.dp)
                ) {

                    Column(
                        modifier =
                            Modifier.padding(20.dp)
                    ) {

                        Row(
                            modifier =
                                Modifier.fillMaxWidth(),
                            horizontalArrangement =
                                Arrangement.SpaceBetween,
                            verticalAlignment =
                                Alignment.CenterVertically
                        ) {

                            Column {

                                Text(
                                    text = "🎯 Daily Goals",
                                    fontSize = 20.sp,
                                    fontWeight =
                                        FontWeight.Bold
                                )

                                Text(
                                    text =
                                        if (allCompleted)
                                            "Everything completed!"
                                        else
                                            "$remaining remaining"
                                )
                            }

                            Text(
                                text =
                                    "$totalDone / $totalGoal",
                                fontSize = 18.sp,
                                fontWeight =
                                    FontWeight.Bold
                            )
                        }

                        Spacer(
                            modifier =
                                Modifier.height(15.dp)
                        )

                        LinearProgressIndicator(
                            progress = {
                                progress
                            },
                            modifier =
                                Modifier
                                    .fillMaxWidth()
                                    .height(8.dp)
                        )

                        Spacer(
                            modifier =
                                Modifier.height(18.dp)
                        )

                        GoalProgressRow(
                            emoji = "🟢",
                            title = "Easy",
                            done = easyDone,
                            target = easyTarget,
                            onAdd = {

                                AppState.addEasy(
                                    context
                                )

                                refresh++
                            }
                        )

                        Spacer(
                            modifier =
                                Modifier.height(12.dp)
                        )

                        GoalProgressRow(
                            emoji = "🟡",
                            title = "Medium",
                            done = mediumDone,
                            target = mediumTarget,
                            onAdd = {

                                AppState.addMedium(
                                    context
                                )

                                refresh++
                            }
                        )

                        Spacer(
                            modifier =
                                Modifier.height(12.dp)
                        )

                        GoalProgressRow(
                            emoji = "🔴",
                            title = "Hard",
                            done = hardDone,
                            target = hardTarget,
                            onAdd = {

                                AppState.addHard(
                                    context
                                )

                                refresh++
                            }
                        )
                    }
                }
            }

            /* =================================================
               STATUS
               ================================================= */

            item {

                Card(
                    modifier =
                        Modifier.fillMaxWidth(),
                    shape =
                        RoundedCornerShape(22.dp),
                    colors =
                        CardDefaults.cardColors(
                            containerColor =
                                if (allCompleted)
                                    MaterialTheme
                                        .colorScheme
                                        .primaryContainer
                                else
                                    MaterialTheme
                                        .colorScheme
                                        .surfaceVariant
                        )
                ) {

                    Column(
                        modifier =
                            Modifier.padding(20.dp)
                    ) {

                        Text(
                            text =
                                if (allCompleted)
                                    "🎉 DAY COMPLETE!"
                                else
                                    "⏳ KEEP GOING!",
                            fontSize = 19.sp,
                            fontWeight =
                                FontWeight.Bold
                        )

                        Spacer(
                            modifier =
                                Modifier.height(5.dp)
                        )

                        Text(
                            text =
                                if (allCompleted)
                                    "You've completed your daily goal. Enjoy the win."
                                else
                                    "You have $remaining question(s) left today."
                        )
                    }
                }
            }

            /* =================================================
               REMINDER STATUS
               ================================================= */

            item {

                Card(
                    modifier =
                        Modifier.fillMaxWidth(),
                    shape =
                        RoundedCornerShape(22.dp)
                ) {

                    Row(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .padding(18.dp),
                        verticalAlignment =
                            Alignment.CenterVertically
                    ) {

                        Box(
                            modifier =
                                Modifier
                                    .size(48.dp)
                                    .clip(CircleShape)
                                    .background(
                                        MaterialTheme
                                            .colorScheme
                                            .surfaceVariant
                                    ),
                            contentAlignment =
                                Alignment.Center
                        ) {

                            Text(
                                text = "🔔",
                                fontSize = 23.sp
                            )
                        }

                        Spacer(
                            modifier =
                                Modifier.width(14.dp)
                        )

                        Column {

                            Text(
                                text = "Reminders",
                                fontSize = 17.sp,
                                fontWeight =
                                    FontWeight.Bold
                            )

                            Text(
                                text =
                                    if (allCompleted)
                                        "Hourly reminders stopped"
                                    else
                                        "Hourly reminders are active"
                            )

                            Text(
                                text =
                                    if (streakDone)
                                        "🔥 Streak alarm completed"
                                    else
                                        "🔥 Streak alarm is active",
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }

            item {
                Spacer(
                    modifier =
                        Modifier.height(20.dp)
                )
            }
        }
    }
}

/* =========================================================
   GOAL ROW
   ========================================================= */

@Composable
fun GoalProgressRow(
    emoji: String,
    title: String,
    done: Int,
    target: Int,
    onAdd: () -> Unit
) {

    Row(
        modifier =
            Modifier.fillMaxWidth(),
        verticalAlignment =
            Alignment.CenterVertically
    ) {

        Text(
            text = emoji,
            fontSize = 23.sp
        )

        Spacer(
            modifier =
                Modifier.width(12.dp)
        )

        Column(
            modifier =
                Modifier.weight(1f)
        ) {

            Text(
                text = title,
                fontSize = 16.sp,
                fontWeight =
                    FontWeight.Bold
            )

            Text(
                text =
                    if (target == 0)
                        "Not required today"
                    else
                        "$done / $target completed",
                fontSize = 13.sp
            )
        }

        if (target > 0) {

            OutlinedButton(
                onClick = onAdd,
                enabled = done < target,
                shape =
                    RoundedCornerShape(12.dp)
            ) {

                Text(
                    text =
                        if (done >= target)
                            "✓"
                        else
                            "+1"
                )
            }

        } else {

            Text(
                text = "—",
                fontSize = 18.sp
            )
        }
    }
}

/* =========================================================
   NUMBER SELECTOR
   ========================================================= */

@Composable
fun NumberRow(
    emoji: String,
    title: String,
    value: Int,
    onChange: (Int) -> Unit
) {

    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(vertical = 5.dp),
        verticalAlignment =
            Alignment.CenterVertically
    ) {

        Text(
            text = emoji,
            fontSize = 20.sp
        )

        Spacer(
            modifier =
                Modifier.width(8.dp)
        )

        Text(
            text = title,
            modifier =
                Modifier.weight(1f),
            fontSize = 16.sp,
            fontWeight =
                FontWeight.SemiBold
        )

        OutlinedButton(
            onClick = {
                if (value > 0) {
                    onChange(value - 1)
                }
            },
            modifier =
                Modifier.size(
                    width = 48.dp,
                    height = 40.dp
                ),
            shape =
                RoundedCornerShape(10.dp),
            contentPadding =
                PaddingValues(0.dp)
        ) {

            Text(
                text = "−",
                fontSize = 20.sp
            )
        }

        Text(
            text = value.toString(),
            modifier =
                Modifier.width(45.dp),
            fontSize = 18.sp,
            fontWeight =
                FontWeight.Bold
        )

        OutlinedButton(
            onClick = {
                onChange(value + 1)
            },
            modifier =
                Modifier.size(
                    width = 48.dp,
                    height = 40.dp
                ),
            shape =
                RoundedCornerShape(10.dp),
            contentPadding =
                PaddingValues(0.dp)
        ) {

            Text(
                text = "+",
                fontSize = 20.sp
            )
        }
    }
}

/* =========================================================
   SETTINGS
   ========================================================= */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    context: Context,
    onBack: () -> Unit
) {

    var showResetDialog by remember {
        mutableStateOf(false)
    }

    var easy by remember {
        mutableIntStateOf(
            AppState.getEasyTarget(context)
        )
    }

    var medium by remember {
        mutableIntStateOf(
            AppState.getMediumTarget(context)
        )
    }

    var hard by remember {
        mutableIntStateOf(
            AppState.getHardTarget(context)
        )
    }

    var alarms by remember {
        mutableStateOf(
            AppState.getAlarmTimes(context)
        )
    }

    Scaffold(

        topBar = {

            TopAppBar(

                title = {

                    Text(
                        text = "⚙️ Settings",
                        fontWeight =
                            FontWeight.Bold
                    )
                },

                navigationIcon = {

                    Button(
                        onClick = onBack,
                        modifier =
                            Modifier
                                .padding(start = 8.dp)
                                .height(44.dp),
                        shape =
                            RoundedCornerShape(12.dp),
                        contentPadding =
                            PaddingValues(
                                horizontal = 14.dp,
                                vertical = 0.dp
                            )
                    ) {

                        Text(
                            text = "Home",
                            fontSize = 14.sp,
                            fontWeight =
                                FontWeight.SemiBold
                        )
                    }
                }
            )
        }

    ) { padding ->

        LazyColumn(

            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp),

            verticalArrangement =
                Arrangement.spacedBy(14.dp)
        ) {

            /* =================================================
               DAILY QUESTIONS
               ================================================= */

            item {

                Card(
                    modifier =
                        Modifier.fillMaxWidth(),
                    shape =
                        RoundedCornerShape(22.dp)
                ) {

                    Column(
                        modifier =
                            Modifier.padding(20.dp)
                    ) {

                        Text(
                            text = "🎯 Daily Questions",
                            fontSize = 20.sp,
                            fontWeight =
                                FontWeight.Bold
                        )

                        Text(
                            text =
                                "Set any number you want. Zero is allowed."
                        )

                        Spacer(
                            modifier =
                                Modifier.height(12.dp)
                        )

                        NumberRow(
                            emoji = "🟢",
                            title = "Easy",
                            value = easy,
                            onChange = {
                                easy = it
                            }
                        )

                        NumberRow(
                            emoji = "🟡",
                            title = "Medium",
                            value = medium,
                            onChange = {
                                medium = it
                            }
                        )

                        NumberRow(
                            emoji = "🔴",
                            title = "Hard",
                            value = hard,
                            onChange = {
                                hard = it
                            }
                        )

                        Spacer(
                            modifier =
                                Modifier.height(12.dp)
                        )

                        Button(
                            onClick = {

                                AppState.updateGoals(
                                    context,
                                    easy,
                                    medium,
                                    hard
                                )
                            },

                            modifier =
                                Modifier.fillMaxWidth(),

                            shape =
                                RoundedCornerShape(14.dp)
                        ) {

                            Text(
                                text = "SAVE DAILY GOAL",
                                fontWeight =
                                    FontWeight.Bold
                            )
                        }
                    }
                }
            }

            /* =================================================
               RESET
               ================================================= */

            item {

                Card(
                    modifier =
                        Modifier.fillMaxWidth(),
                    shape =
                        RoundedCornerShape(22.dp),
                    colors =
                        CardDefaults.cardColors(
                            containerColor =
                                MaterialTheme
                                    .colorScheme
                                    .errorContainer
                        )
                ) {

                    Column(
                        modifier =
                            Modifier.padding(20.dp)
                    ) {

                        Text(
                            text = "🔄 Reset Progress",
                            fontSize = 20.sp,
                            fontWeight =
                                FontWeight.Bold
                        )

                        Spacer(
                            modifier =
                                Modifier.height(5.dp)
                        )

                        Text(
                            text =
                                "Reset your daily progress or current streak."
                        )

                        Spacer(
                            modifier =
                                Modifier.height(14.dp)
                        )

                        OutlinedButton(
                            onClick = {
                                showResetDialog = true
                            },

                            modifier =
                                Modifier.fillMaxWidth(),

                            shape =
                                RoundedCornerShape(14.dp)
                        ) {

                            Text(
                                text = "RESET PROGRESS",
                                fontWeight =
                                    FontWeight.Bold
                            )
                        }
                    }
                }
            }

            /* =================================================
               STREAK ALARMS
               ================================================= */

            item {

                Card(
                    modifier =
                        Modifier.fillMaxWidth(),
                    shape =
                        RoundedCornerShape(22.dp)
                ) {

                    Column(
                        modifier =
                            Modifier.padding(20.dp)
                    ) {

                        Text(
                            text = "🔥 Streak Alarms",
                            fontSize = 20.sp,
                            fontWeight =
                                FontWeight.Bold
                        )

                        Text(
                            text =
                                "These alarms are only for your streak question."
                        )

                        Spacer(
                            modifier =
                                Modifier.height(15.dp)
                        )

                        if (alarms.isEmpty()) {

                            Text(
                                text =
                                    "No streak alarms configured."
                            )

                        } else {

                            alarms.forEach { minutes ->

                                AlarmRow(
                                    minutes = minutes,
                                    onDelete = {

                                        AppState.removeAlarmTime(
                                            context,
                                            minutes
                                        )

                                        alarms =
                                            AppState
                                                .getAlarmTimes(
                                                    context
                                                )
                                    }
                                )
                            }
                        }

                        Spacer(
                            modifier =
                                Modifier.height(12.dp)
                        )

                        OutlinedButton(
                            onClick = {

                                val calendar =
                                    Calendar.getInstance()

                                TimePickerDialog(
                                    context,

                                    { _, hour, minute ->

                                        AppState.addAlarmTime(
                                            context,
                                            hour,
                                            minute
                                        )

                                        alarms =
                                            AppState
                                                .getAlarmTimes(
                                                    context
                                                )
                                    },

                                    calendar.get(
                                        Calendar.HOUR_OF_DAY
                                    ),

                                    calendar.get(
                                        Calendar.MINUTE
                                    ),

                                    false

                                ).show()
                            },

                            modifier =
                                Modifier.fillMaxWidth(),

                            shape =
                                RoundedCornerShape(14.dp)
                        ) {

                            Text(
                                text =
                                    "+ ADD STREAK ALARM"
                            )
                        }
                    }
                }
            }

            /* =================================================
               REMINDER INFORMATION
               ================================================= */

            item {

                Card(
                    modifier =
                        Modifier.fillMaxWidth(),
                    shape =
                        RoundedCornerShape(22.dp),
                    colors =
                        CardDefaults.cardColors(
                            containerColor =
                                MaterialTheme
                                    .colorScheme
                                    .surfaceVariant
                        )
                ) {

                    Column(
                        modifier =
                            Modifier.padding(20.dp)
                    ) {

                        Text(
                            text = "🔔 How reminders work",
                            fontSize = 18.sp,
                            fontWeight =
                                FontWeight.Bold
                        )

                        Spacer(
                            modifier =
                                Modifier.height(8.dp)
                        )

                        Text(
                            text =
                                "• Streak alarms remind you about the streak question."
                        )

                        Text(
                            text =
                                "• Daily goals use hourly notifications."
                        )

                        Text(
                            text =
                                "• Notifications stop when all daily goals are complete."
                        )

                        Text(
                            text =
                                "• Streak alarms stop when today's streak question is complete."
                        )
                    }
                }
            }

            item {

                Spacer(
                    modifier =
                        Modifier.height(20.dp)
                )
            }
        }
    }

    /* =========================================================
       RESET DIALOG
       ========================================================= */

    if (showResetDialog) {

        AlertDialog(

            onDismissRequest = {
                showResetDialog = false
            },

            title = {

                Text(
                    text = "What do you want to reset?"
                )
            },

            text = {

                Column {

                    Text(
                        text =
                            "Choose what you want to start from zero."
                    )

                    Spacer(
                        modifier =
                            Modifier.height(16.dp)
                    )

                    Button(
                        onClick = {

                            AppState.resetDailySchedule(
                                context
                            )

                            showResetDialog = false
                        },

                        modifier =
                            Modifier.fillMaxWidth(),

                        shape =
                            RoundedCornerShape(12.dp)
                    ) {

                        Text(
                            text =
                                "Reset Daily Progress"
                        )
                    }

                    Spacer(
                        modifier =
                            Modifier.height(8.dp)
                    )

                    Button(
                        onClick = {

                            AppState.resetStreak(
                                context
                            )

                            showResetDialog = false
                        },

                        modifier =
                            Modifier.fillMaxWidth(),

                        shape =
                            RoundedCornerShape(12.dp)
                    ) {

                        Text(
                            text =
                                "Reset Current Streak"
                        )
                    }
                }
            },

            confirmButton = {

                TextButton(
                    onClick = {
                        showResetDialog = false
                    }
                ) {

                    Text(
                        text = "Cancel"
                    )
                }
            }
        )
    }
}

/* =========================================================
   ALARM ROW
   ========================================================= */

@Composable
fun AlarmRow(
    minutes: Int,
    onDelete: () -> Unit
) {

    val hour =
        minutes / 60

    val minute =
        minutes % 60

    val amPm =
        if (hour >= 12) "PM" else "AM"

    val displayHour =
        when {
            hour == 0 -> 12
            hour > 12 -> hour - 12
            else -> hour
        }

    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(vertical = 5.dp),

        verticalAlignment =
            Alignment.CenterVertically
    ) {

        Box(
            modifier =
                Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(
                        MaterialTheme
                            .colorScheme
                            .primaryContainer
                    ),

            contentAlignment =
                Alignment.Center
        ) {

            Text(
                text = "⏰",
                fontSize = 19.sp
            )
        }

        Spacer(
            modifier =
                Modifier.width(12.dp)
        )

        Text(
            text =
                String.format(
                    Locale.getDefault(),
                    "%02d:%02d %s",
                    displayHour,
                    minute,
                    amPm
                ),

            modifier =
                Modifier.weight(1f),

            fontSize = 16.sp,

            fontWeight =
                FontWeight.SemiBold
        )

        TextButton(
            onClick = onDelete
        ) {

            Text(
                text = "Delete"
            )
        }
    }
}