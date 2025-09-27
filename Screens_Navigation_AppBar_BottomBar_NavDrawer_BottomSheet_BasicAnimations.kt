package com.example.layoutapp

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import kotlinx.coroutines.launch
import com.example.layoutapp.ui.theme.LayoutAppTheme

var adsEnabled = true

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val controller = WindowInsetsControllerCompat(window, window.decorView)
        controller.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        controller.hide(WindowInsetsCompat.Type.navigationBars())

        setContent { LayoutAppTheme { MyApp() } }
    }
}

sealed class Screen(val route: String, val title: String, val icon: Int) {
    object Home : Screen("home", "Home", R.drawable.home)
    object Music : Screen("music", "Music", R.drawable.music)      // new screen
    object Settings : Screen("settings", "Settings", R.drawable.settings)
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun MyApp() {
    val navController = rememberNavController()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Text("Menu", modifier = Modifier.padding(16.dp), fontSize = 20.sp)

                DrawerItem(navController, drawerState, Screen.Home, currentRoute)
                DrawerItem(navController, drawerState, Screen.Music, currentRoute)
                DrawerItem(navController, drawerState, Screen.Settings, currentRoute)
            }
        }
    ) {
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            enterTransition = {
                slideInHorizontally(initialOffsetX = { it }, animationSpec = tween(300)) +
                        fadeIn(animationSpec = tween(300))
            },
            exitTransition = {
                slideOutHorizontally(targetOffsetX = { -it }, animationSpec = tween(300)) +
                        fadeOut(animationSpec = tween(300))
            },
            popEnterTransition = {
                slideInHorizontally(initialOffsetX = { -it }, animationSpec = tween(300)) +
                        fadeIn(animationSpec = tween(300))
            },
            popExitTransition = {
                slideOutHorizontally(targetOffsetX = { it }, animationSpec = tween(300)) +
                        fadeOut(animationSpec = tween(300))
            }
        ) {
            composable(Screen.Home.route) { HomeScreen(navController, drawerState) }
            composable(Screen.Music.route) { MusicScreen(navController, drawerState) }
            composable(Screen.Settings.route) { SettingsScreen(navController, drawerState) }
        }

        if (drawerState.isOpen) {
            BackHandler { scope.launch { drawerState.close() } }
        }
    }
}

@Composable
fun DrawerItem(
    navController: NavHostController,
    drawerState: DrawerState,
    screen: Screen,
    currentRoute: String?
) {
    val scope = rememberCoroutineScope()
    NavigationDrawerItem(
        label = { Text(screen.title) },
        selected = currentRoute == screen.route,
        onClick = {
            navController.navigate(screen.route) {
                popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                launchSingleTop = true
                restoreState = true
            }
            scope.launch { drawerState.close() }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavHostController, drawerState: DrawerState) {
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Home") },
                navigationIcon = {
                    IconButton(onClick = { scope.launch { drawerState.open() } }) {
                        Icon(painter = painterResource(id = R.drawable.menu), contentDescription = "Menu")
                    }
                },
                actions = {
                    IconButton(onClick = { navController.navigate(Screen.Settings.route) }) {
                        Icon(painter = painterResource(id = R.drawable.settings), contentDescription = "Settings")
                    }
                }
            )
        },
        bottomBar = {
            Column {
                BottomNavBar(navController)
                if (adsEnabled) AdBanner()
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(200.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.LightGray),
                contentAlignment = Alignment.Center
            ) {
                Text("Ad", fontSize = 20.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MusicScreen(navController: NavHostController, drawerState: DrawerState) {
//    val scope = rememberCoroutineScope()
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Music") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(painter = painterResource(id = R.drawable.back), contentDescription = "Back")
                    }
                }
            )
        },
        bottomBar = {
            Column {
                BottomNavBar(navController)
                if (adsEnabled) AdBanner()
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column {
                Text("🎵 Music Screen", fontSize = 22.sp, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(8.dp))
                Button(onClick = {
                    navController.navigate(Screen.Settings.route)
                }) {
                    Text("Settings Screen")
                }
            }

        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(navController: NavHostController, drawerState: DrawerState) {
    val context = LocalContext.current

    var showMenuSheet by remember { mutableStateOf(false) }
    var selectedCardTitle by remember { mutableStateOf("") }

    if (showMenuSheet) {
        ModalBottomSheet(onDismissRequest = { showMenuSheet = false }) {
            Column(Modifier.padding(16.dp)) {
                Text(selectedCardTitle, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(12.dp))
                Text("Edit", modifier = Modifier.fillMaxWidth().clickable {
                    Toast.makeText(context, "Edit $selectedCardTitle", Toast.LENGTH_SHORT).show()
                    showMenuSheet = false
                }.padding(8.dp))
                Text("Share", modifier = Modifier.fillMaxWidth().clickable {
                    Toast.makeText(context, "Share $selectedCardTitle", Toast.LENGTH_SHORT).show()
                    showMenuSheet = false
                }.padding(8.dp))
                Text("Delete", modifier = Modifier.fillMaxWidth().clickable {
                    Toast.makeText(context, "Delete $selectedCardTitle", Toast.LENGTH_SHORT).show()
                    showMenuSheet = false
                }.padding(8.dp))
            }
        }
    }

    val squareItems = listOf(
        Pair(R.drawable.settings, "New releases"),
        Pair(R.drawable.settings, "Charts"),
        Pair(R.drawable.settings, "Moods & genres"),
        Pair(R.drawable.settings, "Podcasts"),
    )
    val horizontalItems = listOf(
        Triple("Popular Videos", "Most watched recently", R.drawable.home),
        Triple("Trending", "Currently trending now", R.drawable.home),
        Triple("Hot", "New hot uploads", R.drawable.home)
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(painter = painterResource(id = R.drawable.back), contentDescription = "Back")
                    }
                }
            )
        },
        bottomBar = {
            Column {
                BottomNavBar(navController)
                if (adsEnabled) AdBanner()
            }
        }
    ) { padding ->
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            modifier = Modifier
                .padding(padding)
                .fillMaxSize(),
            contentPadding = PaddingValues(12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item(span = { GridItemSpan(3) }) {
                Text("Quick Access", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            }

            items(squareItems) { (icon, title) ->
                SquareCard(icon, title) { Toast.makeText(context, "$title clicked", Toast.LENGTH_SHORT).show() }
            }

            item(span = { GridItemSpan(3) }) { Spacer(Modifier.height(8.dp)) }

            item(span = { GridItemSpan(3) }) {
                Text("Recommended", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            }

            items(horizontalItems, span = { GridItemSpan(3) }) { (title, subtitle, imgRes) ->
                HorizontalCard(
                    title = title,
                    subtitle = subtitle,
                    imageRes = imgRes,
                    onClick = { Toast.makeText(context, "$title clicked", Toast.LENGTH_SHORT).show() },
                    onMenuClick = {
                        selectedCardTitle = title
                        showMenuSheet = true
                    }
                )
            }
        }
    }
}

@Composable
fun SquareCard(icon: Int, title: String, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .clickable { onClick() }
            .padding(8.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(painterResource(id = icon), contentDescription = title, modifier = Modifier.size(28.dp))
        Spacer(Modifier.height(6.dp))
        Text(title, fontSize = 12.sp, fontWeight = FontWeight.Medium, maxLines = 1)
    }
}

@Composable
fun HorizontalCard(
    title: String,
    subtitle: String,
    imageRes: Int,
    onClick: () -> Unit,
    onMenuClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(90.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = imageRes),
                contentDescription = title,
                modifier = Modifier
                    .fillMaxHeight()
                    .aspectRatio(16f / 9f)
                    .clip(RoundedCornerShape(topStart = 10.dp, bottomStart = 10.dp)),
                contentScale = ContentScale.FillHeight
            )
            Spacer(Modifier.width(6.dp))
            Column(
                modifier = Modifier.weight(1f).padding(end = 4.dp)
            ) {
                Text(title, fontWeight = FontWeight.Bold, fontSize = 15.sp, maxLines = 1)
                Spacer(Modifier.height(2.dp))
                Text(subtitle, fontSize = 12.sp, color = Color.Gray, maxLines = 2)
            }
            IconButton(onClick = onMenuClick) {
                Icon(painter = painterResource(id = R.drawable.more_vert), contentDescription = "Menu")
            }
        }
    }
}

@Composable
fun BottomNavBar(navController: NavController) {
    val items = listOf(Screen.Home, Screen.Music, Screen.Settings)
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationBar(
        containerColor = NavigationBarDefaults.containerColor,
        modifier = Modifier.height(56.dp)
    ) {
        items.forEach { screen ->
            val selected = currentRoute == screen.route
            NavigationBarItem(
                selected = selected,
                onClick = {
                    navController.navigate(screen.route) {
                        popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                icon = {
                    Icon(
                        painter = painterResource(id = screen.icon),
                        contentDescription = screen.title,
                        modifier = Modifier.size(20.dp),
                        tint = if (selected) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                label = {
                    Text(screen.title, fontSize = 10.sp,
                        color = if (selected) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.onSurfaceVariant)
                },
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = Color.Transparent,
                    selectedIconColor = MaterialTheme.colorScheme.primary,
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                ),
                interactionSource = remember { MutableInteractionSource() }
            )
        }
    }
}

@Composable
fun AdBanner() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .background(Color.LightGray),
        contentAlignment = Alignment.Center
    ) {
        Text("Ad Banner", color = Color.Black)
    }
}
