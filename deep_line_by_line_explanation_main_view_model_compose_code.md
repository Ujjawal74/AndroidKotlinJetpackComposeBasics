# Deep Line-by-line Explanation — Main View Model Compose Code (Updated)

This document explains your code **line by line**, with extra focus on the **difference between `remember` vs `mutableStateOf` vs normal variables (`var x = 0`)**, and **why `remember` is not used in ViewModel**.

---

## STEP 1: Dependencies
```kotlin
// In build.gradle (app):
dependencies {
    implementation "androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0"
}
```
This adds the integration between **ViewModel** and **Jetpack Compose**, allowing you to use `viewModel()` inside Composables.

---

## STEP 2: ViewModel Class
```kotlin
class MainViewModel : ViewModel() {
    var points by mutableIntStateOf(0)
        private set

    var text by mutableStateOf("")
        private set
```
- `class MainViewModel : ViewModel()` → A ViewModel survives **configuration changes** (like screen rotation). Normal variables in an Activity/Composable do **not**.

- `var points by mutableIntStateOf(0)` →
  - This creates **state** that is observable by Compose.
  - Whenever `points` changes, Compose UI automatically **refreshes**.
  - **Why not `remember { mutableIntStateOf(0) }`?** Because `remember` is only for Composables. A ViewModel already persists across recompositions, so we don’t need `remember` here.
  - **Why not `var points = 0`?** → That would store a plain integer. The UI would not know when it changes, so the screen wouldn’t update.

- `private set` → Encapsulation. Only the ViewModel can modify these properties. The UI must call functions.

```kotlin
    fun increasePoints() {
        points++
    }

    fun updateText(newText: String) {
        text = newText
    }
```
- These functions change the state. Compose automatically re-renders the UI when state changes.

```kotlin
    override fun onCleared() {
        super.onCleared()
        println("ViewModel is being destroyed")
    }
```
- Cleanup hook, called when ViewModel is destroyed.

---

## STEP 3: MainActivity
```kotlin
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
```
- Standard Compose Activity.
- Calls `Greeting()` Composable.

---

## STEP 4: Greeting Composable
```kotlin
@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    val viewModel: MainViewModel = viewModel()
```
- `val viewModel: MainViewModel = viewModel()` → retrieves a ViewModel **attached to this Activity**.
- **Why not `MainViewModel()`?** → That would create a new instance every recomposition. Your state would reset and you’d lose the whole point of using ViewModel.

```kotlin
    Column(modifier = modifier.padding(20.dp)) {
        Text(text = "Hello $name!")
        Spacer(Modifier.height(5.dp))

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
    }
}
```
- Reads data (`viewModel.points`, `viewModel.text`).
- Updates state through functions (`increasePoints`, `updateText`).
- UI auto-refreshes when these values change.

---

## STEP 5: Preview
```kotlin
@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    NetworkingAppTheme {
        Greeting("Android")
    }
}
```
- Preview only (not using ViewModel).

---

## 🔑 Important Concepts Recap

### 1. `var points = 0`
- Just a plain variable.
- UI will **NOT** update when this changes.
- Not suitable for reactive UI in Compose.

### 2. `var points by remember { mutableIntStateOf(0) }`
- Used **inside Composables**.
- `remember` ensures value survives **recomposition**.
- Needed because Composable functions re-run multiple times.

### 3. `var points by mutableIntStateOf(0)` (used in ViewModel)
- Used **inside ViewModels**.
- ViewModel survives **configuration changes**.
- No `remember` needed, because ViewModel itself is the memory.
- Compose automatically observes this state.

---

## 🎯 Final Rule of Thumb
- **Inside Composable →** use `remember { mutableStateOf(...) }`
- **Inside ViewModel →** use `mutableStateOf(...)` directly
- **Never** use plain `var x = 0` for UI state → UI won’t refresh.

---

✅ This updated explanation should now cover your **main doubt: why no `remember` in ViewModel**, and why `mutableStateOf` is necessary for UI refresh.

