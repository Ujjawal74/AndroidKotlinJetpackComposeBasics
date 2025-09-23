package com.example.networkingapp

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.networkingapp.ui.theme.NetworkingAppTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import kotlinx.coroutines.async
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

// ✅ ViewModel holds all UI state + background work
class MainViewModel : ViewModel() {
    var points by mutableIntStateOf(0)
        private set

    var text by mutableStateOf("")
        private set

    var myText by mutableStateOf("Initial Text")
        private set

    fun increasePoints() {
        points++
    }

    fun updateText(newText: String) {
        text = newText
    }

    fun changeMyText(newValue: String) {
        myText = newValue
    }


//    Network Fetch
    var posts by mutableStateOf(PostUiState())

    fun getPosts(){
        viewModelScope.launch(Dispatchers.IO) {
            posts = posts.copy(isLoading = true, error = null)
            fetchAPI()
        }
    }

    suspend fun fetchAPI() {
        try {
            val retrofit = Retrofit.Builder()
                .baseUrl("https://jsonplaceholder.typicode.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            val postsInterface: PostsInterface = retrofit.create(PostsInterface::class.java)
            val data: Posts = postsInterface.getPosts() // suspend call
//            Log.v("MY_TAG", "Fetched posts: $data")

            // Wrap in your Posts class
            val postsData : Posts = data

            for (post in postsData) {
                Log.v("MY_TAG", "Title: ${post.title}")
            }

            posts = posts.copy(
                posts = postsData,
                isLoading = false,
                error = null
            )
        } catch (e: Exception) {
            Log.v("MY_TAG", "Error fetching posts: ${e.message}")
            posts = posts.copy(
                isLoading = false,
                error = e.message
            )
        }
    }

    //    var sum by mutableIntStateOf(0)
//        private set
    // ✅ Background task that updates UI safely
    fun downloadBigFile() {
        viewModelScope.launch(Dispatchers.IO) {
//            for (i in 1..10000) {
//                sum += i
//                myText = "Downloading... $sum"
//                Log.i("MY_LOG", "Total sum is $sum in ${Thread.currentThread().name}")
//            }
            Log.v("MY_TAG", "Started")

            val one = async {
                task1()
            }
            val two = async {
                task2()
            }

            val result = one.await() + two.await()
            myText = "Downloading... $result"

            Log.v("MY_TAG", "Result is : $result")


        }
    }

    override fun onCleared() {
        super.onCleared()
        Log.i("MY_LOG", "ViewModel is being destroyed, coroutines canceled")
    }

    data class PostUiState(
        val posts: Posts = Posts(),
        val isLoading: Boolean = false,
        val error: String? = null
    )

}

// ✅ Main Activity
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NetworkingAppTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

// ✅ Composable reads state from ViewModel (survives rotation)
@Composable
fun Greeting(modifier: Modifier = Modifier) {
    val viewModel: MainViewModel = viewModel()



    Column(modifier = modifier.padding(20.dp)) {
        Text("Current Point is: ${viewModel.points}")

        Spacer(Modifier.height(5.dp))

        Button(onClick = { viewModel.increasePoints() }) {
            Text("Increase Points")
        }

        Spacer(Modifier.height(10.dp))

        TextField(
            value = viewModel.text,
            onValueChange = { newText -> viewModel.updateText(newText) },
            label = { Text("Enter some text") }
        )

        if (viewModel.text.isNotEmpty()) {
            Spacer(Modifier.height(5.dp))
            Text("You typed: ${viewModel.text}")
        }

        Spacer(Modifier.height(10.dp))

        // ✅ Shows progress of background job
        Text("Text is: ${viewModel.myText}")

        Button(onClick = {
            viewModel.changeMyText("This is new string")
            viewModel.downloadBigFile() }) {
            Text("Download Big File")
        }

        Button(onClick = {
            viewModel.getPosts()
        }) {
            Text("Fetch Data")
        }

        LazyColumn {
            val posts = viewModel.posts.posts.toList()
            items(posts) { post ->
                Text(
                    text = post.title,
                    modifier = Modifier.padding(8.dp)
                )
            }
        }



    }
}


suspend fun task1(): Int {
    delay(5000)
    Log.v("MY_TAG", "Task1 is done")
    return 5000
}

suspend fun task2(): Int {
    delay(7000)
    Log.v("MY_TAG", "Task2 is done")
    return 7000
}
