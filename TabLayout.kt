package com.example.tablayout

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.tablayout.ui.theme.TabLayoutTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TabLayoutTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    MusicHomeScreen(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}


@Composable
fun MusicHomeScreen(modifier: Modifier) {
    val tabs = listOf("Songs", "Videos", "Artists", "Albums", "Folders")
    val pagerState = rememberPagerState(pageCount = { tabs.size })
    val coroutineScope = rememberCoroutineScope()

    Column(modifier = modifier.fillMaxSize()) {
        ScrollableTabRow(
            selectedTabIndex = pagerState.currentPage, edgePadding = 0.dp
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(selected = pagerState.currentPage == index, onClick = { coroutineScope.launch { pagerState.animateScrollToPage(index) } }, text = {
                    Text(
                        title, fontWeight = if (pagerState.currentPage == index) FontWeight.Bold else FontWeight.Normal
                    )
                })
            }
        }

        HorizontalPager(
            state = pagerState,
        ) { page ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(colorResource(R.color.purple_200), colorResource(R.color.teal_200), Color.White),
                        )
                    ),
            ) {
                when (page) {
                    0 -> SongsPage()
                    1 -> VideosPage()
                    2 -> ArtistsPage()
                    3 -> AlbumsPage()
                    4 -> FoldersPage()
                }
            }
        }
    }
}

@Composable
fun SongsPage() {
    Text("🎵 Songs list goes here", modifier = Modifier.padding(16.dp))
}

@Composable
fun VideosPage() {
    Text("📹 Videos go here", modifier = Modifier.padding(16.dp))
}

@Composable
fun ArtistsPage() {
    Text("🎤 Artists go here", modifier = Modifier.padding(16.dp))
}

@Composable
fun AlbumsPage() {
    Text("💿 Albums go here", modifier = Modifier.padding(16.dp))
}

@Composable
fun FoldersPage() {
    Text("📂 Folders go here", modifier = Modifier.padding(16.dp))
}
