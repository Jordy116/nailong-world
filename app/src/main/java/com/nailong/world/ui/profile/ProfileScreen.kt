package com.nailong.world.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Games
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nailong.world.data.GameDataStore
import com.nailong.world.ui.game.match3.model.LevelProgress
import com.nailong.world.ui.game.match3.model.levels

// ── Colour Palette ──
private val DarkBg = Color(0xFF12141C)
private val DarkCard = Color(0xFF1E2030)
private val DarkSurface = Color(0xFF2A2D3E)
private val AccentOrange = Color(0xFFFF6B35)
private val AccentYellow = Color(0xFFFFC107)
private val AccentGradient = Brush.horizontalGradient(listOf(AccentOrange, AccentYellow))
private val TextPrimary = Color(0xFFEEE8E4)
private val TextSecondary = Color(0xFF9A9490)
private val SuccessGreen = Color(0xFF4CAF50)
private val ExpBarBg = Color(0xFF1A1C28)

@Composable
fun ProfileScreen(modifier: Modifier = Modifier) {
    // Read live data from the DataStore-backed LevelProgress every recomposition
    val store = LevelProgress.getStore()
    val stats = store.getPlayerStats()
    val levelProgress = store.getLevelProgress()
    val levelsCleared = levelProgress.count { it.isCleared }

    var showAboutDialog by remember { mutableStateOf(false) }
    var showLogoutConfirm by remember { mutableStateOf(false) }

    // ── About Dialog ──
    if (showAboutDialog) {
        AlertDialog(
            onDismissRequest = { showAboutDialog = false },
            containerColor = DarkCard,
            title = {
                Text("關於奶龍世界", color = TextPrimary, fontWeight = FontWeight.Bold)
            },
            text = {
                Column {
                    Text("🐉 奶龍世界 v1.0.0", color = AccentYellow, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("專為奶龍粉絲打造的娛樂與社區平台。", color = TextSecondary, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("官方網站：", color = TextSecondary, fontSize = 12.sp)
                    Text("nylon-art-hub.base44.app", color = AccentOrange, fontSize = 14.sp)
                }
            },
            confirmButton = {
                TextButton(onClick = { showAboutDialog = false }) {
                    Text("關閉", color = AccentOrange)
                }
            },
        )
    }

    // ── Logout Confirmation ──
    if (showLogoutConfirm) {
        AlertDialog(
            onDismissRequest = { showLogoutConfirm = false },
            containerColor = DarkCard,
            title = { Text("退出登入", color = TextPrimary, fontWeight = FontWeight.Bold) },
            text = { Text("確定要退出登入嗎？退出後遊戲進度將保留在本地。", color = TextSecondary) },
            confirmButton = {
                TextButton(onClick = {
                    showLogoutConfirm = false
                    // In a real app: clear auth session
                }) {
                    Text("確定退出", color = Color(0xFFE53935))
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutConfirm = false }) {
                    Text("取消", color = AccentOrange)
                }
            },
        )
    }

    // ── Main Content ──
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBg),
    ) {
        // ── 1. User Header ──
        item {
            UserHeader(
                nickname = stats.nickname,
                title = stats.title,
                playerLevel = stats.playerLevel,
                expCurrent = stats.expCurrent,
                expMax = stats.expMax,
            )
        }

        // ── 2. Stats Dashboard ──
        item {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "📊 遊戲成就",
                style = MaterialTheme.typography.titleMedium,
                color = TextPrimary,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 16.dp),
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                // Card A: Infinite High Score
                StatDashboardCard(
                    modifier = Modifier.weight(1f),
                    emoji = "🏆",
                    label = "無限模式最高分",
                    value = "${stats.infiniteHighScore}",
                    accentColor = AccentYellow,
                )
                // Card B: Level Progress
                StatDashboardCard(
                    modifier = Modifier.weight(1f),
                    emoji = "🎯",
                    label = "關卡進度",
                    value = "解鎖：第 ${stats.highestLevelUnlocked} 關",
                    accentColor = AccentOrange,
                )
            }
        }

        // ── Level Progress List ──
        item {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "📋 關卡詳情",
                style = MaterialTheme.typography.titleMedium,
                color = TextPrimary,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 16.dp),
            )
            Spacer(modifier = Modifier.height(4.dp))
        }

        items(levelProgress) { progress ->
            LevelProgressRow(
                levelId = progress.levelId,
                name = progress.name,
                highScore = progress.highScore,
                isUnlocked = progress.isUnlocked,
                isCleared = progress.isCleared,
            )
        }

        // ── 3. Menu List ──
        item {
            Spacer(modifier = Modifier.height(20.dp))
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = DarkCard),
            ) {
                MenuItem(Icons.Filled.Favorite, "我的收藏", "收藏的奶龍藝術品與直播重溫")
                HorizontalDivider(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    color = DarkSurface.copy(alpha = 0.5f),
                    thickness = 0.5.dp,
                )
                MenuItem(Icons.Filled.Games, "遊戲中心", "快速跳轉至遊戲列表")
                HorizontalDivider(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    color = DarkSurface.copy(alpha = 0.5f),
                    thickness = 0.5.dp,
                )
                MenuItem(Icons.Filled.Settings, "系統設置", "音效、緩存、通知設定")
                HorizontalDivider(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    color = DarkSurface.copy(alpha = 0.5f),
                    thickness = 0.5.dp,
                )
                MenuItem(
                    Icons.Filled.Info, "關於我們", "版本 v1.0.0",
                    onClick = { showAboutDialog = true },
                )
            }
        }

        // ── 4. Logout Button ──
        item {
            Spacer(modifier = Modifier.height(20.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(DarkSurface)
                    .clickable(onClick = { showLogoutConfirm = true })
                    .padding(vertical = 14.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "退出登入",
                    color = Color(0xFFE53935),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                )
            }
        }

        // ── Version Footer ──
        item {
            Text(
                text = "奶龍世界 v1.0.0",
                color = TextSecondary.copy(alpha = 0.5f),
                fontSize = 12.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp, bottom = 80.dp),
                textAlign = TextAlign.Center,
            )
        }
    }
}

// ── 1. User Header ──

@Composable
private fun UserHeader(
    nickname: String,
    title: String,
    playerLevel: Int,
    expCurrent: Int,
    expMax: Int,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Brush.horizontalGradient(listOf(AccentOrange, AccentYellow)),
                shape = RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp),
            )
            .padding(top = 48.dp, bottom = 24.dp),
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            // Avatar
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(DarkSurface),
                contentAlignment = Alignment.Center,
            ) {
                Text(text = "🐉", fontSize = 44.sp)
            }
            Spacer(modifier = Modifier.height(10.dp))

            // Nickname
            Text(
                text = nickname,
                style = MaterialTheme.typography.titleLarge,
                color = Color.White,
                fontWeight = FontWeight.Bold,
            )
            Spacer(modifier = Modifier.height(4.dp))

            // Title
            Text(
                text = title,
                color = Color.White.copy(alpha = 0.85f),
                fontSize = 13.sp,
            )
            Spacer(modifier = Modifier.height(12.dp))

            // EXP Bar
            ExpBar(
                current = expCurrent,
                max = expMax,
                level = playerLevel,
            )
        }
    }
}

@Composable
private fun ExpBar(current: Int, max: Int, level: Int) {
    val progress = if (max > 0) (current.toFloat() / max) else 0f

    Column(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = "Lv.$level",
                color = Color.White.copy(alpha = 0.8f),
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = "$current / $max EXP",
                color = Color.White.copy(alpha = 0.6f),
                fontSize = 11.sp,
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        // Progress bar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(Color.White.copy(alpha = 0.3f)),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(progress)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(4.dp))
                    .background(Color.White),
            )
        }
    }
}

// ── 2. Stats Dashboard Card ──

@Composable
private fun StatDashboardCard(
    modifier: Modifier = Modifier,
    emoji: String,
    label: String,
    value: String,
    accentColor: Color,
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = DarkCard),
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(text = emoji, fontSize = 32.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = value,
                color = accentColor,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = label,
                color = TextSecondary,
                fontSize = 12.sp,
                textAlign = TextAlign.Center,
            )
        }
    }
}

// ── Level Progress Row ──

@Composable
private fun LevelProgressRow(
    levelId: Int,
    name: String,
    highScore: Int,
    isUnlocked: Boolean,
    isCleared: Boolean,
) {
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
            // Badge
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
                    text = if (isCleared) "✓" else "${levelId}",
                    color = Color.White,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            // Name + stats
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = name,
                    color = if (isUnlocked) TextPrimary else TextSecondary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                )
                Text(
                    text = "🎯 ${levels[levelId - 1].targetScore}" +
                            "  👟 ${levels[levelId - 1].moves}步" +
                            (if (levels[levelId - 1].obstacles > 0) "  🧱 ${levels[levelId - 1].obstacles}" else ""),
                    color = TextSecondary,
                    fontSize = 11.sp,
                )
            }
            // Score / Lock
            if (isCleared) {
                Text(text = "🏆 $highScore", color = AccentYellow, fontSize = 13.sp, fontWeight = FontWeight.Bold)
            } else if (!isUnlocked) {
                Text(text = "🔒", fontSize = 16.sp)
            } else {
                Text(text = "—", color = TextSecondary, fontSize = 13.sp)
            }
        }
    }
}

// ── 3. Menu Item ──

@Composable
private fun MenuItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit = {},
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = AccentOrange,
            modifier = Modifier.size(24.dp),
        )
        Spacer(modifier = Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                color = TextPrimary,
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary,
            )
        }
        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null,
            tint = TextSecondary,
            modifier = Modifier.size(20.dp),
        )
    }
}
