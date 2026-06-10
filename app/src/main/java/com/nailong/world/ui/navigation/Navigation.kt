package com.nailong.world.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Games
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Games
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.People
import androidx.compose.material.icons.outlined.Person
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomNavItem(
    val route: String,
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
) {
    data object Home : BottomNavItem(
        route = "home", title = "主頁",
        selectedIcon = Icons.Filled.Home, unselectedIcon = Icons.Outlined.Home,
    )
    data object Game : BottomNavItem(
        route = "game", title = "遊戲",
        selectedIcon = Icons.Filled.Games, unselectedIcon = Icons.Outlined.Games,
    )
    data object Community : BottomNavItem(
        route = "community", title = "社群",
        selectedIcon = Icons.Filled.People, unselectedIcon = Icons.Outlined.People,
    )
    data object Profile : BottomNavItem(
        route = "profile", title = "我的",
        selectedIcon = Icons.Filled.Person, unselectedIcon = Icons.Outlined.Person,
    )

    companion object {
        val items = listOf(Home, Game, Community, Profile)
    }
}
