package com.nailong.world

import android.os.Bundle
import com.nailong.world.data.GameDataStore
import com.nailong.world.ui.game.match3.model.LevelProgress
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.nailong.world.ui.community.CommunityScreen
import com.nailong.world.ui.game.GameScreen
import com.nailong.world.ui.game.match3.ModeSelectScreen
import com.nailong.world.ui.game.match3.NailongMatch3Screen
import com.nailong.world.ui.game.match3.model.GameConfig
import com.nailong.world.ui.game.match3.model.GameMode
import com.nailong.world.ui.home.HomeScreen
import com.nailong.world.ui.navigation.BottomNavItem
import com.nailong.world.ui.profile.ProfileScreen
import com.nailong.world.ui.theme.NailongWorldTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Initialize game data persistence
        LevelProgress.init(GameDataStore(applicationContext))
        setContent {
            NailongWorldTheme {
                NailongWorldApp()
            }
        }
    }
}

/**
 * Shared game config state — set by ModeSelectScreen, read by the game screen.
 */
object GameConfigHolder {
    var config: GameConfig by mutableStateOf(GameConfig(mode = GameMode.INFINITE))
}

@Composable
fun NailongWorldApp() {
    val navController = rememberNavController()
    var selectedTab by rememberSaveable { mutableIntStateOf(0) }
    val navItems = BottomNavItem.items

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
            ) {
                navItems.forEachIndexed { index, item ->
                    NavigationBarItem(
                        selected = selectedTab == index,
                        onClick = {
                            selectedTab = index
                            navController.navigate(item.route) {
                                popUpTo(navController.graph.startDestinationId) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = {
                            Icon(
                                imageVector = if (selectedTab == index) item.selectedIcon else item.unselectedIcon,
                                contentDescription = item.title,
                            )
                        },
                        label = { Text(text = item.title, fontSize = 12.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            selectedTextColor = MaterialTheme.colorScheme.primary,
                            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                        ),
                    )
                }
            }
        },
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "home",
            modifier = Modifier.padding(innerPadding),
        ) {
            composable("home") {
                HomeScreen(
                    onNavigateToGame = {
                        selectedTab = 1
                        navController.navigate("game") {
                            popUpTo(navController.graph.startDestinationId) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    onPlayMatch3 = {
                        navController.navigate("match3-menu")
                    },
                )
            }
            composable("game") {
                GameScreen(
                    onPlayMatch3 = {
                        navController.navigate("match3-menu")
                    },
                )
            }
            composable("community") {
                CommunityScreen()
            }
            composable("profile") {
                ProfileScreen()
            }
            composable("match3-menu") {
                ModeSelectScreen(
                    onBack = { navController.popBackStack() },
                    onStartGame = { config ->
                        GameConfigHolder.config = config
                        navController.navigate("match3-game")
                    },
                )
            }
            composable("match3-game") {
                NailongMatch3Screen(
                    onBack = { navController.popBackStack() },
                )
            }
        }
    }
}
