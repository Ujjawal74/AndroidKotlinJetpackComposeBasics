// STEP 1: Add these dependencies to your app/build.gradle file
/*
dependencies {
    implementation "androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0"
    // ... your other dependencies
}
*/

package com.example.networkingapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.networkingapp.ui.theme.NetworkingAppTheme

// STEP 2: Create your ViewModel class
class MainViewModel : ViewModel() {
    // STEP 3: Move your state variables here
    // These will survive configuration changes!
    var points by mutableIntStateOf(0)
        private set  // This makes the setter private - only this class can modify points
    
    var text by mutableStateOf("")
        private set  // Same here - encapsulation is good practice
    
    // STEP 4: Create functions to modify the state
    fun increasePoints() {
        points++
    }
    
    fun updateText(newText: String) {
        text = newText
    }
    
    // STEP 5: Optional - Override onCleared() to know when ViewModel is destroyed
    override fun onCleared() {
        super.onCleared()
        // This is called when the ViewModel is destroyed
        // Useful for cleanup (like canceling network requests)
        println("ViewModel is being destroyed")
    }
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NetworkingAppTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        name = "Android",
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    // STEP 6: Get the ViewModel instance
    // viewModel() function creates or retrieves existing ViewModel
    val viewModel: MainViewModel = viewModel()
    
    Column(modifier = modifier.padding(20.dp)) {
        Text(
            text = "Hello $name!",
        )
        Spacer(Modifier.height(5.dp))
        
        // STEP 7: Access state from ViewModel
        Text("Current Point is: ${viewModel.points}")
        
        Spacer(Modifier.height(5.dp))
        
        // STEP 8: Call ViewModel functions to modify state
        Button(onClick = {
            viewModel.increasePoints()  // Call ViewModel function instead of direct state change
        }) {
            Text("Increase Points")
        }
        
        Spacer(Modifier.height(10.dp))
        
        // BONUS: Let's add a text field to show how text state works too
        androidx.compose.material3.TextField(
            value = viewModel.text,
            onValueChange = { newText ->
                viewModel.updateText(newText)  // Call ViewModel function
            },
            label = { Text("Enter some text") }
        )
        
        if (viewModel.text.isNotEmpty()) {
            Spacer(Modifier.height(5.dp))
            Text("You typed: ${viewModel.text}")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    NetworkingAppTheme {
        Greeting("Android")
    }
}