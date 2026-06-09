package com.nailong.world

import android.os.Bundle
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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp
import com.nailong.world.ui.community.CommunityScreen
import com.nailong.world.ui.game.GameScreen
import com.nailong.world.ui.home.HomeScreen
import com.nailong.world.ui.navigation.BottomNavItem
import com.nailong.world.ui.profile.ProfileScreen
import com.nailong.world.ui.theme.NailongWorldTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NailongWorldTheme {
                NailongWorldApp()
            }
        }
    }
}

@Composable
fun NailongWorldApp() {
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
                        onClick = { selectedTab = index },
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
        when (selectedTab) {
            0 -> HomeScreen(
                onNavigateToGame = { selectedTab = 1 },
                modifier = Modifier.padding(innerPadding),
            )
            1 -> GameScreen(
                modifier = Modifier.padding(innerPadding),
            )
            2 -> CommunityScreen(
                modifier = Modifier.padding(innerPadding),
            )
            3 -> ProfileScreen(
                modifier = Modifier.padding(innerPadding),
            )
        }
    }
}
