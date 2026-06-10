package com.nailong.world.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.nailong.world.ui.components.Badge
import com.nailong.world.ui.components.ContentCard
import com.nailong.world.ui.components.GameCard
import com.nailong.world.ui.components.LiveChip
import com.nailong.world.ui.components.NailongLiveBanner
import com.nailong.world.ui.components.SectionHeader
import com.nailong.world.ui.theme.GradientEnd
import com.nailong.world.ui.theme.GradientStart
import com.nailong.world.ui.theme.NailongOrange
import com.nailong.world.ui.theme.TextSecondary
import com.nailong.world.viewmodel.HomeViewModel

@Composable
fun HomeScreen(
    onNavigateToGame: () -> Unit = {},
    onPlayMatch3: () -> Unit = {},
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = viewModel(),
) {
    val state by viewModel.uiState.collectAsState()

    if (state.isLoading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            CircularProgressIndicator(color = NailongOrange)
        }
        return
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
    ) {
        // ── Gallery Header ──
        item {
            GalleryHeader()
        }

        // ── Live Room Banner ──
        item {
            Spacer(modifier = Modifier.height(8.dp))
            NailongLiveBanner(
                onClick = { /* navigate to live room */ },
            )
        }

        // ── Hot Games ──
        item {
            SectionHeader(
                title = "熱門遊戲",
                actionLabel = "全部 →",
                onAction = onNavigateToGame,
            )
        }
        item {
            LazyRow(
                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                items(state.hotGames) { game ->
                    GameCard(
                        title = game.title,
                        description = game.description,
                        onClick = {
                            if (game.id == "game_1") onPlayMatch3()
                        },
                    )
                }
            }
        }

        // ── Recommended Content ──
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
                onClick = { /* open content detail */ },
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
            )
        }

        // ── Daily Check-in ──
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

@Composable
private fun GalleryHeader() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Brush.horizontalGradient(listOf(GradientStart, GradientEnd)),
                shape = RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp),
            )
            .padding(top = 48.dp, bottom = 24.dp),
        contentAlignment = Alignment.Center,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = "🐉", fontSize = 40.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "奶龍藝術館",
                style = MaterialTheme.typography.displayLarge,
                color = MaterialTheme.colorScheme.onPrimary,
            )
            Text(
                text = "NAILONG ART GALLERY",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f),
                letterSpacing = 4.sp,
            )
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
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
        ),
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "每日簽到",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "連續 $consecutiveDays 天 · 今日簽到領獎勵",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary,
                )
            }
            Button(
                onClick = onCheckIn,
                enabled = !hasCheckedIn,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (hasCheckedIn) TextSecondary else NailongOrange,
                ),
            ) {
                Text(
                    text = if (hasCheckedIn) "已簽到" else "領取",
                    fontWeight = FontWeight.Bold,
                )
            }
        }
    }
}
