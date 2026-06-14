package com.nailong.world.ui.game

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.nailong.world.R
import com.nailong.world.ui.components.ModernChip
import com.nailong.world.ui.components.ModernHeroHeader
import com.nailong.world.ui.components.ModernPageBackground
import com.nailong.world.ui.components.PressableScaleCard
import com.nailong.world.ui.theme.NailongGlow
import com.nailong.world.ui.theme.NailongPrimary
import com.nailong.world.ui.theme.NailongSecondary
import com.nailong.world.viewmodel.GameViewModel
import kotlinx.coroutines.delay

private val gameArt = mapOf(
    "g1" to R.drawable.nailong_cute_face,
    "game_1" to R.drawable.nailong_cute_face,
    "g_memory" to R.drawable.nailong_thinking,
    "g_suika" to R.drawable.nailong_cabbage,
    "g3" to R.drawable.nailong_wave,
    "g4" to R.drawable.nailong_arms_crossed,
    "g5" to R.drawable.nailong_laugh_head,
    "g6" to R.drawable.nailong_belly_laugh,
)

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun GameScreen(
    onPlayMatch3: () -> Unit = {},
    onPlayMemory: () -> Unit = {},
    onPlaySuika: () -> Unit = {},
    modifier: Modifier = Modifier,
    viewModel: GameViewModel = viewModel(),
) {
    val state by viewModel.uiState.collectAsState()
    val categories = listOf("all" to "全部", "hot" to "熱門", "recommended" to "推薦", "new" to "最新")
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        delay(60)
        visible = true
    }

    ModernPageBackground(modifier = modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            AnimatedVisibility(
                visible = visible,
                enter = fadeIn(tween(420)) + slideInVertically(tween(420)) { -it / 6 },
            ) {
                ModernHeroHeader(
                    eyebrow = "PLAYGROUND",
                    title = "遊戲中心",
                    subtitle = "挑戰分數、解鎖成就，讓奶龍陪你玩",
                    emoji = "🎮",
                    imageRes = R.drawable.nailong_laugh_duo,
                )
            }

            LazyRow(
                contentPadding = PaddingValues(horizontal = 18.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                items(categories) { (key, label) ->
                    ModernChip(
                        text = label,
                        selected = state.selectedCategory == key,
                        onClick = { viewModel.selectCategory(key) },
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            AnimatedContent(
                targetState = state.selectedCategory,
                transitionSpec = {
                    (fadeIn(tween(220)) + slideInVertically { it / 12 }) togetherWith
                        (fadeOut(tween(160)) + slideOutVertically { -it / 12 })
                },
                label = "gameCategoryContent",
            ) { targetCategory ->
                val games = when (targetCategory) {
                    "all" -> state.games
                    else -> state.games.filter { game -> game.category == targetCategory }
                }
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 14.dp),
                    contentPadding = PaddingValues(bottom = 96.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    items(games, key = { game -> game.id }) { game ->
                        GameGridCard(
                            imageRes = gameArt[game.id] ?: R.drawable.nailong_simple,
                            title = game.title,
                            description = game.description,
                            enabled = game.id in setOf("g1", "game_1", "g_memory", "g_suika"),
                            onClick = {
                                when (game.id) {
                                    "g1", "game_1" -> onPlayMatch3()
                                    "g_memory" -> onPlayMemory()
                                    "g_suika" -> onPlaySuika()
                                }
                            },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun GameGridCard(
    imageRes: Int,
    title: String,
    description: String,
    enabled: Boolean,
    onClick: () -> Unit,
) {
    PressableScaleCard(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(0.86f),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(14.dp),
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .clip(RoundedCornerShape(26.dp))
                    .background(
                        Brush.linearGradient(
                            listOf(
                                NailongPrimary.copy(alpha = 0.18f),
                                NailongSecondary.copy(alpha = 0.24f),
                                NailongGlow.copy(alpha = 0.16f),
                            ),
                        ),
                    ),
                contentAlignment = Alignment.Center,
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize(0.72f)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.55f)),
                    contentAlignment = Alignment.Center,
                ) {
                    Image(
                        painter = painterResource(id = imageRes),
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(0.88f),
                        contentScale = ContentScale.Fit,
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = if (enabled) description else "即將開放 · ${description}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}
