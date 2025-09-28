package com.example.tablayout

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.tablayout.ui.theme.TabLayoutTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request

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
                        TodosScreen()
                    }
                }
            }
        }
    }
}


@Composable
fun TodosScreen() {
    var todos by remember { mutableStateOf<List<Todo>>(emptyList()) }

    LaunchedEffect(Unit) {
        todos = fetchTodos()
    }

    androidx.compose.foundation.lazy.LazyColumn(
        modifier = Modifier.fillMaxSize()
    ) {
        items(todos.size) { index ->
            val todo = todos[index]
            Text(
                text = todo.title, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.padding(16.dp)
            )
        }
    }
}


suspend fun fetchTodos(): List<Todo> {
    return withContext(Dispatchers.IO) {
        try {
            val client = OkHttpClient()
            val request = Request.Builder().url("https://jsonplaceholder.typicode.com/todos").build()
            val response = client.newCall(request).execute()
            val body = response.body?.string() ?: ""
            response.close()

            val jsonArray = org.json.JSONArray(body)
            val todos = mutableListOf<Todo>()
            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                todos.add(
                    Todo(
                        userId = obj.getInt("userId"), id = obj.getInt("id"), title = obj.getString("title"), completed = obj.getBoolean("completed")
                    )
                )
            }
            todos //In Kotlin the last expression inside a lambda (or block) is the return value
        } catch (e: Exception) {
            Log.v("MY_TAG", e.message.toString())
            emptyList() // return empty list if something fails
        }
    }
}


//@Composable
//fun IPAddressScreen() {
//    var ipAddress by remember { mutableStateOf("Fetching...") }
//
//    LaunchedEffect(Unit) {
//        ipAddress = fetchPublicIP()
//    }
//
//    Box(
//        modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center
//    ) {
//        Text(text = ipAddress, style = MaterialTheme.typography.headlineMedium)
//    }
//}
//
//suspend fun fetchPublicIP(): String {
//    return withContext(Dispatchers.IO) {
//        try {
//            val client = OkHttpClient()
//            val request = Request.Builder().url("https://api.ipify.org?format=json").build()
//            val response = client.newCall(request).execute()
//            val body = response.body?.string() ?: ""
//            response.close()
//            val json = JSONObject(body)
//            json.getString("ip")
//        } catch (e: Exception) {
//            "Error: ${e.message}"
//        }
//    }
//}