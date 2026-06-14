package com.nailong.world

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.foundation.layout.size
import androidx.compose.ui.draw.scale
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.nailong.world.data.GameDataStore
import com.nailong.world.ui.community.CommunityScreen
import com.nailong.world.ui.game.GameScreen
import com.nailong.world.ui.game.memory.NailongMemoryScreen
import com.nailong.world.ui.game.match3.ModeSelectScreen
import com.nailong.world.ui.game.suika.SuikaGameScreen
import com.nailong.world.ui.game.match3.NailongMatch3Screen
import com.nailong.world.ui.game.match3.model.GameConfig
import com.nailong.world.ui.game.match3.model.GameMode
import com.nailong.world.ui.game.match3.model.LevelProgress
import com.nailong.world.ui.home.HomeScreen
import com.nailong.world.ui.navigation.BottomNavItem
import com.nailong.world.ui.profile.ProfileScreen
import com.nailong.world.ui.theme.NailongWorldTheme

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
    var config: GameConfig by mutableStateOf(GameConfig(mode = GameMode.INFINITE))
}

@Composable
private fun FloatingBottomBar(
    navItems: List<BottomNavItem>,
    selectedTab: Int,
    onSelect: (Int, String) -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(androidx.compose.ui.graphics.Color.Transparent)
            .padding(horizontal = 14.dp, vertical = 10.dp),
    ) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(28.dp),
            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.94f),
            tonalElevation = 8.dp,
            shadowElevation = 10.dp,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.28f)),
        ) {
            Row(
                modifier = Modifier.padding(6.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                navItems.forEachIndexed { index, item ->
                    val selected = selectedTab == index
                    val bg by animateColorAsState(
                        targetValue = if (selected) MaterialTheme.colorScheme.primary else androidx.compose.ui.graphics.Color.Transparent,
                        label = "navBg",
                    )
                    val content by animateColorAsState(
                        targetValue = if (selected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                        label = "navContent",
                    )
                    val scale by animateFloatAsState(
                        targetValue = if (selected) 1.05f else 1f,
                        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessMediumLow),
                        label = "navScale",
                    )
                    Row(
                        modifier = Modifier
                            .weight(1f)
                            .scale(scale)
                            .padding(2.dp)
                            .background(bg, RoundedCornerShape(22.dp))
                            .clickable { onSelect(index, item.route) }
                            .padding(vertical = 10.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
                    ) {
                        Icon(
                            imageVector = if (selected) item.selectedIcon else item.unselectedIcon,
                            contentDescription = item.title,
                            tint = content,
                            modifier = Modifier.size(20.dp),
                        )
                        if (selected) {
                            Text(
                                text = item.title,
                                color = content,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(start = 5.dp),
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun NailongWorldApp() {
    val navController = rememberNavController()
    var selectedTab by rememberSaveable { mutableIntStateOf(0) }
    val navItems = BottomNavItem.items

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            FloatingBottomBar(
                navItems = navItems,
                selectedTab = selectedTab,
                onSelect = { index, route ->
                    selectedTab = index
                    navController.navigate(route) {
                        popUpTo(navController.graph.startDestinationId) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
            )
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
                    onPlayMatch3 = { navController.navigate("match3-menu") },
                )
            }
            composable("game") {
                GameScreen(
                    onPlayMatch3 = { navController.navigate("match3-menu") },
                    onPlayMemory = { navController.navigate("memory-game") },
                    onPlaySuika = { navController.navigate("suika-game") },
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
            composable("suika-game") {
                SuikaGameScreen(onBack = { navController.popBackStack() })
            }
        }
    }
}
