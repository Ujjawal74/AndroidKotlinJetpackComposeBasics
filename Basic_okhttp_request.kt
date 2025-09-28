package com.example.tablayout

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.tablayout.ui.theme.TabLayoutTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TabLayoutTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Surface(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        IPAddressScreen()
                    }
                }
            }
        }
    }
}


@Composable
fun IPAddressScreen() {
    var ipAddress by remember { mutableStateOf("Fetching...") }

    LaunchedEffect(Unit) {
        ipAddress = fetchPublicIP()
    }

    Box(
        modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center
    ) {
        Text(text = ipAddress, style = MaterialTheme.typography.headlineMedium)
    }
}

suspend fun fetchPublicIP(): String {
    return withContext(Dispatchers.IO) { // Network task must be on new thread, give its result when it’s done
        try {
            val client = OkHttpClient()
            val request = Request.Builder().url("https://api.ipify.org?format=json").build()
            val response = client.newCall(request).execute()
            val body = response.body?.string() ?: ""
            response.close()
            val json = JSONObject(body)
            json.getString("ip") // last expression or statement in lambda means return
        } catch (e: Exception) {
            "Error: ${e.message}"
        }
    }
}

/*

Why we didn’t use rememberCoroutineScope().launch
Those two things solve different problems:

| Feature                             | Purpose                                                                                    |
| ----------------------------------- | ------------------------------------------------------------------------------------------ |
| `LaunchedEffect` / `viewModelScope` | **Starts a coroutine** (launches work). Used when you need to kick off a job from UI code. |
| `withContext(Dispatchers.IO)`       | **Switches threads** **inside an already-running coroutine** and returns a value.          |


Your fetchPublicIP() is already a suspend function.
When you call it from LaunchedEffect, you’re already inside a coroutine scope (Compose starts it for you).
So you don’t need to create another scope or launch — you just need to run part of it on a background dispatcher.

That’s exactly what withContext does: it doesn’t launch a new coroutine, it just suspends the current one and resumes it on another thread pool.
If you weren’t inside a coroutine
If you were in normal UI code (e.g., in a click listener) and wanted to run a suspend function, then you’d need a scope:


val scope = rememberCoroutineScope()
Button(onClick = {
    scope.launch {
        val ip = fetchPublicIP()  // now you can call it
    }
}) { Text("Fetch IP") }

launch starts a coroutine because click listeners aren’t already in one.
But in LaunchedEffect you’re already in a coroutine, so you can directly call suspend functions and use withContext.
LaunchedEffect => launches a coroutine in that scope on the Main (UI) thread by default.

*/