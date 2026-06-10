package com.nailong.world.ui.profile

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nailong.world.data.repository.NailongRepository
import com.nailong.world.ui.game.match3.model.LevelProgress
import com.nailong.world.ui.game.match3.model.levels
import androidx.compose.ui.graphics.Color

private val DarkBg = Color(0xFF12141C)
private val DarkCard = Color(0xFF1E2030)
private val DarkSurface = Color(0xFF2A2D3E)
private val AccentOrange = Color(0xFFFF6B35)
private val AccentYellow = Color(0xFFFFC107)
private val TextPrimary = Color(0xFFEEE8E4)
private val TextSecondary = Color(0xFF9A9490)
private val SuccessGreen = Color(0xFF4CAF50)

@Composable
fun ProfileScreen(modifier: Modifier = Modifier) {
    val levelsCompleted = levels.count { LevelProgress.getHighScore(it.id) > 0 }
    val infiniteHighScore = LevelProgress.getHighScore(-1)
    val totalAchievements = levelsCompleted

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBg),
    ) {
        // ── Profile Header ──
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.horizontalGradient(listOf(AccentOrange, AccentYellow)),
                        shape = RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp),
                    )
                    .padding(top = 48.dp, bottom = 32.dp),
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape)
                            .background(DarkSurface),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(text = "🐉", fontSize = 44.sp)
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "奶龍粉絲",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onPrimary,
                        fontWeight = FontWeight.Bold,
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "已通關 $levelsCompleted/${levels.size} 關  ·  無限最高 $infiniteHighScore 分",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f),
                    )
                }
            }
        }

        // ── Stats Row ──
        item {
            Row(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
            ) {
                StatItem(Icons.Filled.Star, "通關", "$levelsCompleted")
                StatItem(Icons.Filled.EmojiEvents, "成就", "$totalAchievements")
                StatItem(Icons.Filled.Favorite, "最高分", "$infiniteHighScore")
            }
        }

        // ── Level Progress Section ──
        item {
            Text(
                text = "📋 關卡進度",
                style = MaterialTheme.typography.titleMedium,
                color = TextPrimary,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            )
        }

        // Show each level's progress
        items(levels) { level ->
            val highScore = LevelProgress.getHighScore(level.id)
            val isUnlocked = LevelProgress.isUnlocked(level.id)
            val isCleared = highScore > 0

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 3.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isCleared) DarkCard.copy(alpha = 0.8f) else DarkSurface.copy(alpha = 0.5f)
                ),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    // Level number
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(
                                when {
                                    isCleared -> SuccessGreen
                                    isUnlocked -> AccentOrange
                                    else -> Color(0xFF444455)
                                }
                            ),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = if (isCleared) "✓" else "${level.id}",
                            color = Color.White,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = level.name,
                            color = if (isUnlocked) TextPrimary else TextSecondary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                        )
                        Text(
                            text = "🎯 ${level.targetScore}  👟 ${level.moves}步" +
                                    (if (level.obstacles > 0) "  🧱 ${level.obstacles}" else ""),
                            color = TextSecondary,
                            fontSize = 11.sp,
                        )
                    }
                    if (isCleared) {
                        Text(
                            text = "🏆 $highScore",
                            color = AccentYellow,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                        )
                    } else if (!isUnlocked) {
                        Text(text = "🔒", fontSize = 16.sp)
                    } else {
                        Text(text = "—", color = TextSecondary, fontSize = 13.sp)
                    }
                }
            }
        }

        // ── Infinite Mode Stats ──
        item {
            Spacer(modifier = Modifier.height(12.dp))
            Card(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = DarkCard),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(AccentOrange.copy(alpha = 0.3f)),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(text = "∞", color = AccentOrange, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = "無限模式", color = TextPrimary, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
                        Text(text = "最高分 $infiniteHighScore", color = TextSecondary, fontSize = 12.sp)
                    }
                    Text(text = "🏆", fontSize = 24.sp)
                }
            }
        }

        // ── Menu Items ──
        item {
            Spacer(modifier = Modifier.height(16.dp))
            Card(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = DarkCard),
            ) {
                ProfileMenuItem(Icons.Filled.Settings, "設定", "個人資料與偏好")
            }
        }

        // ── Reset Button ──
        item {
            Spacer(modifier = Modifier.height(16.dp))
            var showResetConfirm by remember { mutableStateOf(false) }
            Text(
                text = if (showResetConfirm) "確認重置所有進度？" else "重置所有遊戲進度",
                color = if (showResetConfirm) Color(0xFFE53935) else TextSecondary,
                fontSize = 13.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        if (showResetConfirm) {
                            LevelProgress.resetAll()
                            showResetConfirm = false
                        } else {
                            showResetConfirm = true
                        }
                    }
                    .padding(16.dp),
            )
        }

        // ── Version Info ──
        item {
            Text(
                text = "奶龍世界 v1.0.0",
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary,
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp, bottom = 80.dp),
                textAlign = TextAlign.Center,
            )
        }
    }
}

@Composable
private fun StatItem(icon: ImageVector, label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(imageVector = icon, contentDescription = label, tint = AccentOrange, modifier = Modifier.size(28.dp))
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = value, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = TextPrimary)
        Text(text = label, style = MaterialTheme.typography.labelSmall, color = TextSecondary)
    }
}

@Composable
private fun ProfileMenuItem(icon: ImageVector, title: String, subtitle: String, onClick: () -> Unit = {}) {
    Row(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick).padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(imageVector = icon, contentDescription = title, tint = AccentOrange, modifier = Modifier.size(24.dp))
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, style = MaterialTheme.typography.titleSmall, color = TextPrimary)
            Text(text = subtitle, style = MaterialTheme.typography.bodySmall, color = TextSecondary)
        }
        Icon(imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null, tint = TextSecondary)
    }
}
