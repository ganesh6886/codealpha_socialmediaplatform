package com.example.project

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.project.data.SocialRepository
import com.example.project.ui.components.AppLogo
import com.example.project.ui.screens.*
import com.example.project.ui.theme.*

class MainActivity : ComponentActivity() {
    private lateinit var repository: SocialRepository

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        repository = SocialRepository(applicationContext)
        enableEdgeToEdge()
        setContent {
            ProjectTheme {
                val isLoggedIn by repository.isLoggedIn.collectAsState()
                
                if (!isLoggedIn) {
                    LoginScreen(repository)
                } else {
                    var selectedTab by remember { mutableIntStateOf(0) }
                    
                    Scaffold(
                        modifier = Modifier.fillMaxSize(),
                        containerColor = MidnightBlack,
                        topBar = {
                            if (selectedTab == 0) {
                                CenterAlignedTopAppBar(
                                    colors = TopAppBarDefaults.topAppBarColors(
                                        containerColor = MidnightBlack.copy(alpha = 0.9f)
                                    ),
                                    title = { 
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            AppLogo(size = 32.dp)
                                            Spacer(modifier = Modifier.width(10.dp))
                                            Text(
                                                "FriendLink", 
                                                fontWeight = FontWeight.ExtraBold,
                                                fontSize = 26.sp,
                                                style = LocalTextStyle.current.copy(
                                                    brush = Brush.linearGradient(
                                                        colors = listOf(ElectricPurple, CyberCyan)
                                                    )
                                                )
                                            ) 
                                        }
                                    },
                                    actions = {
                                        IconButton(onClick = {}) { Icon(Icons.Default.Notifications, null, tint = Color.White) }
                                        IconButton(onClick = { selectedTab = 3 }) { Icon(Icons.Default.ChatBubbleOutline, "Messages", tint = Color.White) }
                                    }
                                )
                            }
                        },
                        bottomBar = {
                            NavigationBar(
                                containerColor = MidnightBlack,
                                tonalElevation = 0.dp,
                                modifier = Modifier.background(MidnightBlack)
                            ) {
                                val tabs = listOf(
                                    Triple(0, Icons.Default.Home, Icons.Outlined.Home),
                                    Triple(1, Icons.Default.TravelExplore, Icons.Default.TravelExplore),
                                    Triple(2, Icons.Default.PlayArrow, Icons.Outlined.PlayArrow),
                                    Triple(3, Icons.Default.ChatBubble, Icons.Outlined.ChatBubbleOutline),
                                    Triple(4, Icons.Default.AccountCircle, Icons.Outlined.AccountCircle)
                                )
                                
                                tabs.forEach { (index, filledIcon, outlinedIcon) ->
                                    NavigationBarItem(
                                        selected = selectedTab == index,
                                        onClick = { selectedTab = index },
                                        icon = { 
                                            Icon(
                                                if (selectedTab == index) filledIcon else outlinedIcon, 
                                                null,
                                                tint = if (selectedTab == index) ElectricPurple else Color.Gray,
                                                modifier = Modifier.size(if (selectedTab == index) 28.dp else 24.dp)
                                            ) 
                                        },
                                        colors = NavigationBarItemDefaults.colors(
                                            indicatorColor = ElectricPurple.copy(alpha = 0.1f)
                                        )
                                    )
                                }
                            }
                        }
                    ) { innerPadding ->
                        Box(modifier = Modifier.fillMaxSize()) {
                            when (selectedTab) {
                                0 -> FeedScreen(repository, innerPadding)
                                1 -> DiscoverScreen(repository, innerPadding)
                                2 -> ReelsScreen(repository, innerPadding)
                                3 -> MessagesScreen(repository, innerPadding)
                                4 -> ProfileScreen(repository, innerPadding)
                            }
                        }
                    }
                }
            }
        }
    }
}
