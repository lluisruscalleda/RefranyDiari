package com.example.refranydiari

import android.Manifest
import android.content.BroadcastReceiver
import android.view.WindowInsets
import android.view.WindowInsetsController
import androidx.core.view.WindowCompat
import android.os.Bundle
import android.view.View
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresPermission
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.refranydiari.ui.theme.RefranyDiariTheme
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.absoluteValue
import android.app.PendingIntent
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

data class RefranySection(
    val title: String,
    val color: Color,
    val sentence: String,
    var isExpanded: Boolean = false
)

val catalanColors = listOf(
    Color(0xFFDA1212), // Red (Senyeres)
    Color(0xFF0055A4), // Deep blue
    Color(0xFFF4C300), // Yellow
    Color(0xFF003399), // Blue
    Color(0xFFB41C18)  // Dark red
)

val topicFiles = listOf(
    "Temps_i_Naturalesa.txt",
    "Treball_i_Vida_Quotidiana.txt",
    "Saviesa_i_Consells.txt",
    "Relacions_i_Societat.txt",
    "Sort_i_Destí.txt"
)
@Composable
fun SentenceScreen(context: Context) {
    val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
    val todayHash = today.hashCode().absoluteValue

    val sections = remember { mutableStateListOf<RefranySection>() }

    LaunchedEffect(Unit) {
        val window = (context as? ComponentActivity)?.window ?: return@LaunchedEffect

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.let {
                it.hide(WindowInsets.Type.statusBars() or WindowInsets.Type.navigationBars())
                it.systemBarsBehavior = WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            }
        } else {
            @Suppress("DEPRECATION")
            window.decorView.systemUiVisibility = (
                    View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                            or View.SYSTEM_UI_FLAG_FULLSCREEN
                            or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    )
        }
    }

    LaunchedEffect(Unit) {
        val sectionList = topicFiles.mapIndexed { index, fileName ->
            val topicName = fileName.removeSuffix(".txt").replace('_', ' ')
            val color = catalanColors[index % catalanColors.size]

            val sentences = context.assets.open(fileName)
                .bufferedReader().readLines()
                .filter { it.isNotBlank() }

            val idx = (todayHash + index) % sentences.size

            RefranySection(
                title = topicName,
                color = color,
                sentence = sentences[idx]
            )
        }

        sections.clear()
        sections.addAll(sectionList)
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        RefranysLayout(sections)
    }
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        createNotificationChannel(this)
        scheduleDailyNotification(this)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            RefranyDiariTheme {
                SentenceScreen(this)
            }
        }
    }
}

@Composable
fun RefranysLayout(sections: List<RefranySection>) {
    var expandedIndex by remember { mutableStateOf<Int?>(null) }

    Column(modifier = Modifier.fillMaxSize()) {
        sections.forEachIndexed { index, section ->
            val isExpanded = expandedIndex == index

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .background(section.color)
                    .clickable { expandedIndex = if (isExpanded) null else index }
                    .padding(horizontal = 24.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Column {
                    Text(
                        text = section.title.uppercase(),
                        style = MaterialTheme.typography.headlineLarge.copy(
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 24.sp,
                            letterSpacing = 1.5.sp
                        )
                    )
                    AnimatedVisibility(visible = isExpanded) {
                        Text(
                            text = section.sentence,
                            style = MaterialTheme.typography.bodyLarge.copy(
                                color = Color.White,
                                fontSize = 16.sp,
                                lineHeight = 20.sp
                            ),
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                }
            }
        }
    }
}
fun createNotificationChannel(context: Context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val name = "Daily Refranys"
        val descriptionText = "Notificació diària de nous refranys"
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel("DAILY_REFRANYS_CHANNEL", name, importance).apply {
            description = descriptionText
        }
        val notificationManager: NotificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }
}

class DailyNotificationReceiver : BroadcastReceiver() {
    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    override fun onReceive(context: Context, intent: Intent) {
        val tapIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val tapPendingIntent = PendingIntent.getActivity(
            context,
            0,
            tapIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val builder = NotificationCompat.Builder(context, "DAILY_REFRANYS_CHANNEL")
            .setSmallIcon(R.drawable.notification) // Use your own icon
            .setContentTitle("Nou refrany del dia")
            .setContentText("Fes clic per veure els nous refranys d'avui!")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(tapPendingIntent)
            .setAutoCancel(true)

        val notificationManager = NotificationManagerCompat.from(context)
        notificationManager.notify(1001, builder.build())
    }
}

fun scheduleDailyNotification(context: Context) {
    val intent = Intent(context, DailyNotificationReceiver::class.java)
    val pendingIntent = PendingIntent.getBroadcast(
        context,
        0,
        intent,
        PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
    )

    val calendar = Calendar.getInstance().apply {
        timeInMillis = System.currentTimeMillis()
        set(Calendar.HOUR_OF_DAY, 13) // 13 AM
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
    }

    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    alarmManager.setInexactRepeating(
        AlarmManager.RTC_WAKEUP,
        calendar.timeInMillis,
        AlarmManager.INTERVAL_DAY,
        pendingIntent
    )
}
