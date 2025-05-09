package com.example.refranydiari

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.refranydiari.ui.theme.RefranyDiariTheme
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.absoluteValue

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            RefranyDiariTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    SentenceScreen(this)
                }
            }
        }
    }
}

@Composable
fun SentenceScreen(context: Context) {
    val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
    val sharedPref = context.getSharedPreferences("DailyLinePrefs", Context.MODE_PRIVATE)

    var sentence by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        val savedDate = sharedPref.getString("lastDate", null)
        val savedIndex = sharedPref.getInt("lastIndex", -1)

        val sentences = context.assets.open("refranys.txt")
            .bufferedReader().readLines()

        val index = if (savedDate == today && savedIndex != -1) {
            savedIndex
        } else {
            val newIndex = (today.hashCode().absoluteValue) % sentences.size
            with(sharedPref.edit()) {
                putString("lastDate", today)
                putInt("lastIndex", newIndex)
                apply()
            }
            newIndex
        }

        sentence = sentences[index]
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        Text(text = sentence, style = MaterialTheme.typography.headlineSmall)
    }
}
