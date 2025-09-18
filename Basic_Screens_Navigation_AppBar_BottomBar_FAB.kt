package com.example.myapp

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.myapp.ui.theme.MyAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyAppTheme {
                MyApp()
            }
        }
    }
}


@Composable
fun MyApp() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Screen.Home.route) {
        composable(Screen.Home.route) {
            HomeScreen(navController)
        }
        composable(Screen.Settings.route) {
            SettingsScreen(navController)
        }

        composable(Screen.About.route) {
            AboutScreen(navController)
        }

        composable(Screen.Updates.route) {
            UpdatesScreen(navController)
        }

        composable(Screen.Profile.route) { backStackEntry ->
            val name = backStackEntry.arguments?.getString("name") ?: "Unknown"
            val age = backStackEntry.arguments?.getString("age")?.toIntOrNull() ?: 0
            ProfileScreen(navController, name, age)
        }
    }

}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyTopBar(
    title: String,
    showMenu: Boolean,
    onMenuClick: () -> Unit,
    canNavigateBack: Boolean,
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    TopAppBar(
        title = { Text(title) },
        navigationIcon = {
            when {
                showMenu -> {
                    IconButton(onClick = onMenuClick) {
                        Image(
                            painter = painterResource(id = R.drawable.ham_menu),
                            contentDescription = "Menu"
                        )
                    }
                }

                canNavigateBack -> {
                    IconButton(onClick = onBackClick) {
                        Image(
                            painter = painterResource(id = R.drawable.back),
                            contentDescription = "Back"
                        )
                    }
                }
            }
        },
        actions = {
            IconButton(
                onClick = {
                    Toast.makeText(context, "Clicked Settings", Toast.LENGTH_SHORT).show()
                }
            ) {
                Icon(painter = painterResource(id = R.drawable.settings_icon), contentDescription = "Settings")
            }
        }
    )
}

@Composable
fun BottomBar(navController: NavController) {
    val items = listOf(
        Screen.Home,
        Screen.Settings,
        Screen.Profile,
        Screen.About,
        Screen.Updates
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.12f) // keep same height
            .background(colorResource(R.color.pale_peach))
            .horizontalScroll(rememberScrollState()), // <-- enables horizontal scrolling
        horizontalArrangement = Arrangement.Start
    ) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        items.forEach { screen ->
            val isSelected = currentRoute == screen.route

            Column(
                modifier = Modifier
                    .padding(horizontal = 20.dp, vertical = 8.dp)
                    .clickable {
                        if (screen is Screen.Profile) {
                            navController.navigate(screen.createRoute("Ujjawal", 25)) {
                                popUpTo(navController.graph.startDestinationId) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        } else {
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.startDestinationId) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    },
//                    .drawBehind {
//                        drawLine(
//                            color = if (isSelected) Color(0xFF6A0DAD) else Color.Transparent, // purple for selected, gray for others
//                            start = Offset(0f, 0f),
//                            end = Offset(size.width, 0f),
//                            strokeWidth = 2.dp.toPx()
//                        )
//                    },
//                adjust Icon Padding and remove column's modifier's vertical padding -> for top border
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    painter = painterResource(screen.icon),
                    contentDescription = null,
                    modifier = Modifier.size(22.dp),
                    tint = if (isSelected) Color(0xFF6A0DAD) else Color.Gray
                )
                Text(
                    text = screen.title,
                    fontSize = 12.sp,
                    color = if (isSelected) Color(0xFF6A0DAD) else Color.Gray
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavHostController) {
    val context = LocalContext.current

    Scaffold(
        topBar = {
            MyTopBar(
                title = "Home",
                showMenu = true,
                onMenuClick = { Toast.makeText(context, "Clicked Menu", Toast.LENGTH_SHORT).show() },
                canNavigateBack = false,
                onBackClick = {}
            )
        },
        bottomBar = { BottomBar(navController) }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(onClick = { navController.navigate(Screen.Settings.route) }) {
                Text("Go to Settings")
            }

            Spacer(Modifier.height(16.dp))

            ScrollingLazyColumn(Modifier)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(navController: NavHostController) {
    Scaffold(
        topBar = {
            MyTopBar(
                title = "Settings",
                showMenu = false,
                onMenuClick = {},
                canNavigateBack = true,
                onBackClick = { navController.popBackStack() } // goes back
            )
        },
        bottomBar = { BottomBar(navController) }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("This is Settings Screen")

            Spacer(Modifier.height(16.dp))

            Button(onClick = { navController.popBackStack() }) {
                Text("Back")
            }

            Spacer(Modifier.height(16.dp))

            Button(onClick = {
                // pass data → Profile
                val name = "Mr. Ujjawal Biswas"
                val age = 25
                navController.navigate(Screen.Profile.createRoute(name, age))
            }) {
                Text("Go to Profile with Data")
            }

            Spacer(Modifier.height(16.dp))

            TestActions(Modifier)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(navController: NavHostController, name: String, age: Int) {
    Scaffold(
        topBar = {
            MyTopBar(
                title = "Profile",
                showMenu = false,
                onMenuClick = {},
                canNavigateBack = true,
                onBackClick = { navController.popBackStack() }
            )
        },
        bottomBar = { BottomBar(navController) }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Hello, $name! You are $age years old.")

            Spacer(Modifier.height(16.dp))

            Button(onClick = { navController.popBackStack() }) {
                Text("Back")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutScreen(navController: NavHostController) {
    Scaffold(
        topBar = {
            MyTopBar(
                title = "About",
                showMenu = false,
                onMenuClick = {},
                canNavigateBack = true,
                onBackClick = { navController.popBackStack() }
            )
        },
        bottomBar = { BottomBar(navController) }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("This is About Screen")

            Spacer(Modifier.height(16.dp))

            Button(onClick = { navController.popBackStack() }) {
                Text("Back")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UpdatesScreen(navController: NavHostController) {
    Scaffold(
        topBar = {
            MyTopBar(
                title = "Updates",
                showMenu = false,
                onMenuClick = {},
                canNavigateBack = true,
                onBackClick = { navController.popBackStack() }
            )
        },
        bottomBar = { BottomBar(navController) },
        floatingActionButton = { MyFAB() }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("This is Updates Screen")

            Spacer(Modifier.height(16.dp))

            Button(onClick = { navController.popBackStack() }) {
                Text("Back")
            }
        }
    }
}


@Composable
fun TestActions(modifier: Modifier) {
    var text by remember { mutableStateOf("") }
    var progress by remember { mutableFloatStateOf(0f) }
    Column(modifier = modifier.padding(16.dp)) {

        TextField(value = text, onValueChange = { newVal -> text = newVal })
        Text("You Typed: $text")
        CircularProgressIndicator(progress = { progress }, strokeWidth = 7.dp, color = Color.Magenta, modifier = Modifier.size(80.dp))
        Button(onClick = {
            if (progress >= 1f) {
                progress = 0f
            } else {
                progress += 0.1f
            }
        }) { Text("Click Me") }
    }
}

@Composable
fun MyCard(id: Int, cardNo: Int) {
    val context = LocalContext.current
    Card(
        modifier = Modifier
            .padding(5.dp),          // outer margin around card
        elevation = CardDefaults.cardElevation(6.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier
            .padding(10.dp)
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = {
                        Toast.makeText(context, "Card $cardNo is Clicked", Toast.LENGTH_SHORT).show()
                    },
                    onLongPress = {
                        Toast.makeText(context, "Card $cardNo is Long Pressed", Toast.LENGTH_SHORT).show()
                    }
                )
            }) {
            Image(
                painter = painterResource(id), contentDescription = "Nature Image $cardNo", contentScale = ContentScale.FillWidth, modifier = Modifier
                    .fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text("Card No. $cardNo", fontSize = 24.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            Text("This is a awesome Image - $cardNo", fontWeight = FontWeight.Thin)
        }
    }

//    modifier.clickable {
//        Toast.makeText(context, "Card $cardNo is Clicked", Toast.LENGTH_SHORT).show()
//    }
//    long press will not work with clickable use only detectTapGestures.
}

@Composable
fun ScrollingLazyColumn(modifier: Modifier) {
    val arr = arrayOf(
        R.drawable.pic1,
        R.drawable.pic2,
        R.drawable.pic3,
        R.drawable.pic4,
        R.drawable.pic5,
        R.drawable.pic6,
        R.drawable.pic7,
        R.drawable.pic8,
        R.drawable.pic9,
        R.drawable.pic10,
        R.drawable.pic11
    )
    LazyColumn(modifier = modifier.padding(horizontal = 10.dp, vertical = 5.dp)) {
        itemsIndexed(arr) { index, item ->
            MyCard(item, index)
        } // just a way to iterate (inbuilt)
    }
}

@Composable
fun MyFAB() {
    val context = LocalContext.current
    FloatingActionButton(
        onClick = { Toast.makeText(context, "Clicked Floating Action Button", Toast.LENGTH_SHORT).show() },
        contentColor = colorResource(R.color.deep_violet),
        containerColor = colorResource(id = R.color.pale_cyan)
    ) {
        Icon(painter = painterResource(R.drawable.bell), contentDescription = "Bell Icon")
    }
}