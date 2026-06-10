package com.nailong.world.ui.game.match3

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nailong.world.ui.game.match3.model.GameConfig
import com.nailong.world.ui.game.match3.model.GameMode
import com.nailong.world.ui.game.match3.model.LevelConfig
import com.nailong.world.ui.game.match3.model.LevelProgress
import com.nailong.world.ui.game.match3.model.levels

// ── Dark theme colours ──
private val DarkBg = Color(0xFF12141C)
private val DarkCard = Color(0xFF1E2030)
private val DarkSurface = Color(0xFF2A2D3E)
private val AccentOrange = Color(0xFFFF6B35)
private val AccentYellow = Color(0xFFFFC107)
private val AccentGradient = Brush.horizontalGradient(listOf(AccentOrange, AccentYellow))
private val TextPrimary = Color(0xFFEEE8E4)
private val TextSecondary = Color(0xFF9A9490)
private val LockColor = Color(0xFF555566)
private const val INFINITE = -1

@Composable
fun ModeSelectScreen(
    onBack: () -> Unit,
    onStartGame: (GameConfig) -> Unit,
) {
    // Force refresh when returning from a game
    var refresh by remember { mutableStateOf(0) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBg)
            .padding(top = 32.dp),
    ) {
        // ── Top Bar ──
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "← 返回",
                color = AccentOrange,
                fontSize = 14.sp,
                modifier = Modifier.clickable(onClick = onBack),
            )
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = "🐉 奶龍消消樂",
                style = MaterialTheme.typography.titleLarge,
                color = TextPrimary,
                fontWeight = FontWeight.Bold,
            )
            Spacer(modifier = Modifier.weight(1f))
            Box(modifier = Modifier.size(40.dp))
        }

        Spacer(modifier = Modifier.height(20.dp))

        // ── Infinite Mode Card ──
        InfiniteModeCard(
            highScore = LevelProgress.getHighScore(INFINITE),
            onClick = {
                onStartGame(GameConfig(mode = GameMode.INFINITE))
            },
        )

        Spacer(modifier = Modifier.height(24.dp))

        // ── Section Title ──
        Text(
            text = "📋 關卡模式",
            style = MaterialTheme.typography.titleLarge,
            color = TextPrimary,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 16.dp),
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "完成關卡目標分數，解鎖更高難度！",
            color = TextSecondary,
            fontSize = 13.sp,
            modifier = Modifier.padding(horizontal = 16.dp),
        )
        Spacer(modifier = Modifier.height(12.dp))

        // ── Level Grid ──
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            items(levels) { level ->
                LevelCard(
                    level = level,
                    isUnlocked = LevelProgress.isUnlocked(level.id),
                    highScore = LevelProgress.getHighScore(level.id),
                    onClick = {
                        if (LevelProgress.isUnlocked(level.id)) {
                            onStartGame(GameConfig(mode = GameMode.LEVEL, level = level))
                        }
                    },
                )
            }
            // Bottom padding spacer
            item { Spacer(modifier = Modifier.height(80.dp)) }
        }
    }
}

@Composable
private fun InfiniteModeCard(
    highScore: Int,
    onClick: () -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.horizontalGradient(listOf(Color(0xFFFF6B35), Color(0xFFFFC107))),
                    shape = RoundedCornerShape(20.dp),
                )
                .padding(20.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                // Left: ∞ symbol
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color.White.copy(alpha = 0.25f)),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(text = "∞", fontSize = 30.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
                Spacer(modifier = Modifier.width(14.dp))
                // Middle: text
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "無限模式",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "無步數限制，挑戰你的最高分！",
                        fontSize = 12.sp,
                        color = Color.White.copy(alpha = 0.8f),
                    )
                }
                // Right: trophy + high score
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "🏆", fontSize = 22.sp)
                    Text(
                        text = "最高分",
                        fontSize = 9.sp,
                        color = Color.White.copy(alpha = 0.7f),
                    )
                    if (highScore > 0) {
                        Text(
                            text = "$highScore",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun LevelCard(
    level: LevelConfig,
    isUnlocked: Boolean,
    highScore: Int,
    onClick: () -> Unit,
) {
    val containerColor = if (isUnlocked) DarkCard else DarkSurface.copy(alpha = 0.5f)
    val contentColor = if (isUnlocked) TextPrimary else LockColor

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .then(if (isUnlocked) Modifier.clickable(onClick = onClick) else Modifier),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isUnlocked) 3.dp else 0.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                // Level number badge
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isUnlocked) AccentOrange else LockColor),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = "${level.id}",
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                    )
                }
                // Lock / unlock indicator
                Text(
                    text = if (isUnlocked) "🔓" else "🔒",
                    fontSize = 16.sp,
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            // Level name
            Text(
                text = level.name,
                color = contentColor,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
            )
            Spacer(modifier = Modifier.height(6.dp))
            // Stats
            Text(text = "🎯 ${level.targetScore}", color = if (isUnlocked) AccentYellow else LockColor, fontSize = 12.sp)
            Text(text = "👟 ${level.moves} 步", color = if (isUnlocked) TextSecondary else LockColor, fontSize = 12.sp)
            if (level.obstacles > 0) {
                Text(text = "🧱 ${level.obstacles} 障礙", color = if (isUnlocked) TextSecondary else LockColor, fontSize = 12.sp)
            }
            // High score
            if (highScore > 0 && isUnlocked) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "🏆 $highScore",
                    color = AccentOrange,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                )
            }
        }
    }
}
