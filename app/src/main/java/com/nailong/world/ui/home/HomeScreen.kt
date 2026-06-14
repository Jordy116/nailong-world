package com.nailong.world.ui.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.nailong.world.ui.components.ModernHeroHeader
import com.nailong.world.ui.components.ModernListCard
import com.nailong.world.ui.components.ModernPageBackground
import com.nailong.world.ui.components.ModernSectionTitle
import com.nailong.world.ui.components.NailongLiveBanner
import com.nailong.world.ui.components.NailongMatch3Banner
import com.nailong.world.viewmodel.HomeViewModel
import kotlinx.coroutines.delay

@Composable
fun HomeScreen(
    onNavigateToGame: () -> Unit = {},
    onPlayMatch3: () -> Unit = {},
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = viewModel(),
) {
    val state by viewModel.uiState.collectAsState()
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        delay(80)
        visible = true
    }

    ModernPageBackground(modifier = modifier.fillMaxSize()) {
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            item {
                AnimatedVisibility(
                    visible = visible,
                    enter = fadeIn(tween(420)) + slideInVertically(tween(420)) { -it / 6 },
                ) {
                    ModernHeroHeader(
                        eyebrow = "NAILONG WORLD",
                        title = "奶龍世界",
                        subtitle = "更流暢、更可愛的遊戲與社區體驗",
                        emoji = "🐉",
                    )
                }
            }

            item {
                AnimatedVisibility(
                    visible = visible,
                    enter = fadeIn(tween(520, delayMillis = 80)) + slideInVertically(tween(520, delayMillis = 80)) { it / 5 },
                ) {
                    Column {
                        NailongLiveBanner(onClick = { })
                        Spacer(modifier = Modifier.height(12.dp))
                        NailongMatch3Banner(onClick = onPlayMatch3)
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(8.dp))
                ModernSectionTitle(
                    title = "今日推介",
                    subtitle = "精選內容與奶龍收藏",
                    action = "全部遊戲",
                    onAction = onNavigateToGame,
                )
            }

            itemsIndexed(state.recommendedContent) { index, content ->
                var itemVisible by remember { mutableStateOf(false) }
                LaunchedEffect(Unit) {
                    delay(120L + index * 70L)
                    itemVisible = true
                }
                AnimatedVisibility(
                    visible = itemVisible,
                    enter = fadeIn(tween(360)) + slideInVertically(tween(360)) { it / 4 },
                ) {
                    val emoji = when (content.category) {
                        "gallery" -> "🖼️"
                        "music" -> "🎧"
                        "sticker" -> "✨"
                        else -> "🎯"
                    }
                    ModernListCard(
                        emoji = emoji,
                        title = content.title,
                        description = content.description,
                        badge = content.badgeText,
                        onClick = { },
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
                    )
                }
            }

            item { Spacer(modifier = Modifier.height(96.dp)) }
        }
    }
}
