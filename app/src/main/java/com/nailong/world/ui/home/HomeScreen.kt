package com.nailong.world.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.nailong.world.R
import com.nailong.world.ui.components.Badge
import com.nailong.world.ui.components.ContentCard
import com.nailong.world.ui.components.SectionHeader
import com.nailong.world.viewmodel.HomeViewModel
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.basic.LazyColumn
import top.yukonga.miuix.kmp.basic.TopAppBar

@Composable
fun HomeScreen(
    onNavigateToGame: () -> Unit = {},
    onPlayMatch3: () -> Unit = {},
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = viewModel(),
) {
    val state by viewModel.uiState.collectAsState()

    Column(
        modifier = modifier.fillMaxSize(),
    ) {
        // Miuix TopAppBar with translucent background
        TopAppBar(
            title = "奶龍世界",
            color = MaterialTheme.colorScheme.background,
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
        ) {
            // Gallery header
            item {
                GalleryHeader()
            }

            // Match-3 Banner
            item {
                Spacer(modifier = Modifier.height(12.dp))
                com.nailong.world.ui.components.NailongMatch3Banner(
                    onClick = onPlayMatch3,
                )
            }

            // Recommended content
            item {
                Spacer(modifier = Modifier.height(16.dp))
                SectionHeader(title = "推介")
            }

            items(state.recommendedContent) { content ->
                val emoji = when (content.category) {
                    "gallery" -> "🖼️"
                    "music" -> "🎵"
                    "sticker" -> "😆"
                    else -> "🎯"
                }
                ContentCard(
                    emoji = emoji,
                    title = content.title,
                    description = content.description,
                    badgeText = content.badgeText,
                    onClick = { },
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                )
            }

            // Check-in
            item {
                Spacer(modifier = Modifier.height(16.dp))
                CheckInCard(
                    consecutiveDays = state.checkIn.consecutiveDays,
                    hasCheckedIn = state.checkIn.hasCheckedInToday,
                    onCheckIn = { viewModel.performCheckIn() },
                )
            }

            item { Spacer(modifier = Modifier.height(80.dp)) }
        }
    }
}

@Composable
private fun GalleryHeader() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .clip(RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp)),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .then(
                    with(androidx.compose.ui.Modifier) {
                        androidx.compose.foundation.background(
                            androidx.compose.ui.graphics.Brush.horizontalGradient(
                                listOf(
                                    com.nailong.world.ui.theme.NailongPrimary,
                                    com.nailong.world.ui.theme.NailongSecondary,
                                ),
                            ),
                        )
                    }
                ),
            contentAlignment = Alignment.Center,
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = "🐉", fontSize = 36.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "奶龍藝術館",
                    style = MaterialTheme.typography.displayLarge,
                    color = androidx.compose.ui.graphics.Color.White,
                )
                Text(
                    text = "NAILONG ART GALLERY",
                    style = MaterialTheme.typography.labelMedium,
                    color = androidx.compose.ui.graphics.Color.White.copy(alpha = 0.8f),
                    letterSpacing = 4.sp,
                )
            }
        }
    }
}

@Composable
private fun CheckInCard(
    consecutiveDays: Int,
    hasCheckedIn: Boolean,
    onCheckIn: () -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
        ) {
            Text(
                text = "每日簽到",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "連續 $consecutiveDays 天 · 今日簽到領獎勵",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}
