package com.nailong.world

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.nailong.world.data.GameDataStore
import com.nailong.world.ui.community.CommunityScreen
import com.nailong.world.ui.game.GameScreen
import com.nailong.world.ui.game.memory.NailongMemoryScreen
import com.nailong.world.ui.game.match3.ModeSelectScreen
import com.nailong.world.ui.game.match3.NailongMatch3Screen
import com.nailong.world.ui.game.match3.model.GameConfig
import com.nailong.world.ui.game.match3.model.GameMode
import com.nailong.world.ui.game.match3.model.LevelProgress
import com.nailong.world.ui.home.HomeScreen
import com.nailong.world.ui.profile.ProfileScreen
import com.nailong.world.ui.theme.NailongWorldTheme
import top.yukonga.miuix.kmp.basic.NavigationBar
import top.yukonga.miuix.kmp.basic.NavigationBarItem

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        LevelProgress.init(GameDataStore(applicationContext))
        setContent {
            NailongWorldTheme {
                NailongWorldApp()
            }
        }
    }
}

object GameConfigHolder {
    var config: GameConfig by androidx.compose.runtime.mutableStateOf(GameConfig(mode = GameMode.INFINITE))
}

@Composable
fun NailongWorldApp() {
    val navController = rememberNavController()
    var selectedTab by rememberSaveable { mutableIntStateOf(0) }
    val navItems = com.nailong.world.ui.navigation.BottomNavItem.items
    val icons = listOf(
        androidx.compose.material.icons.Icons.Filled.Home,
        androidx.compose.material.icons.Icons.Filled.Games,
        androidx.compose.material.icons.Icons.Filled.People,
        androidx.compose.material.icons.Icons.Filled.Person,
    )

    androidx.compose.foundation.layout.Box(modifier = Modifier.then(androidx.compose.ui.Modifier)) {
        NavHost(
            navController = navController,
            startDestination = "home",
        ) {
            composable("home") {
                androidx.compose.foundation.layout.Column(modifier = Modifier) {
                    HomeScreen(
                        onNavigateToGame = {
                            selectedTab = 1
                            navController.navigate("game") {
                                popUpTo(navController.graph.startDestinationId) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        onPlayMatch3 = { navController.navigate("match3-menu") },
                    )
                }
            }
            composable("game") {
                GameScreen(
                    onPlayMatch3 = { navController.navigate("match3-menu") },
                    onPlayMemory = { navController.navigate("memory-game") },
                )
            }
            composable("community") { CommunityScreen() }
            composable("profile") { ProfileScreen() }
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
                NailongMatch3Screen(onBack = { navController.popBackStack() })
            }
            composable("memory-game") {
                NailongMemoryScreen(onBack = { navController.popBackStack() })
            }
        }
    }

    // Miuix NavigationBar with liquid glass styling
    NavigationBar(
        modifier = Modifier
            .align(androidx.compose.ui.Alignment.BottomCenter),
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
                icon = icons[index],
            )
        }
    }
}
