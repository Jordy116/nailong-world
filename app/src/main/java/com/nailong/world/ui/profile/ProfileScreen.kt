package com.nailong.world.ui.profile

import androidx.compose.foundation.Image
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
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Games
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.LinearProgressIndicator
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nailong.world.R
import com.nailong.world.ui.components.AnimatedBadge
import com.nailong.world.ui.components.ModernHeroHeader
import com.nailong.world.ui.components.ModernPageBackground
import com.nailong.world.ui.components.ModernSectionTitle
import com.nailong.world.ui.components.PressableScaleCard
import com.nailong.world.ui.game.match3.model.LevelProgress
import com.nailong.world.ui.theme.NailongGlow
import com.nailong.world.ui.theme.NailongMint
import com.nailong.world.ui.theme.NailongPrimary
import com.nailong.world.ui.theme.NailongSecondary

@Composable
fun ProfileScreen(modifier: Modifier = Modifier) {
    val store = LevelProgress.getStore()
    val stats = store.getPlayerStats()
    val levelProgress = store.getLevelProgress()
    val clearedCount = levelProgress.count { it.isCleared }

    var showAboutDialog by remember { mutableStateOf(false) }
    var showLogoutConfirm by remember { mutableStateOf(false) }

    if (showAboutDialog) {
        AlertDialog(
            onDismissRequest = { showAboutDialog = false },
            title = { Text("關於奶龍世界", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text("🐉 奶龍世界 v1.0.0", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("專為奶龍粉絲打造的遊戲、內容與社區平台。")
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("nylon-art-hub.base44.app", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            },
            confirmButton = { TextButton(onClick = { showAboutDialog = false }) { Text("關閉") } },
        )
    }

    if (showLogoutConfirm) {
        AlertDialog(
            onDismissRequest = { showLogoutConfirm = false },
            title = { Text("退出登入", fontWeight = FontWeight.Bold) },
            text = { Text("確定要退出登入嗎？遊戲進度會繼續保留在本地。") },
            confirmButton = {
                TextButton(onClick = { showLogoutConfirm = false }) { Text("確定退出", color = Color(0xFFE53935)) }
            },
            dismissButton = { TextButton(onClick = { showLogoutConfirm = false }) { Text("取消") } },
        )
    }

    ModernPageBackground(modifier = modifier.fillMaxSize()) {
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            item {
                ModernHeroHeader(
                    eyebrow = stats.title,
                    title = stats.nickname,
                    subtitle = "Lv.${stats.playerLevel} · ${stats.expCurrent}/${stats.expMax} EXP",
                    emoji = "🐲",
                    imageRes = R.drawable.nailong_arms_crossed,
                )
            }

            item {
                ProfileExpCard(
                    expCurrent = stats.expCurrent,
                    expMax = stats.expMax,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                )
            }

            item {
                ModernSectionTitle(title = "遊戲成就", subtitle = "你的奶龍成長紀錄")
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    StatCard(R.drawable.nailong_shout, "最高分", "${stats.infiniteHighScore}", Modifier.weight(1f))
                    StatCard(R.drawable.nailong_cute_face, "已通關", "$clearedCount 關", Modifier.weight(1f))
                    StatCard(R.drawable.nailong_wave, "解鎖", "第 ${stats.highestLevelUnlocked} 關", Modifier.weight(1f))
                }
            }

            item { ModernSectionTitle(title = "關卡詳情", subtitle = "挑戰記錄與解鎖進度") }
            items(levelProgress.take(8)) { progress ->
                LevelRow(
                    title = progress.name,
                    subtitle = "最高分 ${progress.highScore}",
                    badge = when {
                        progress.isCleared -> "已通關"
                        progress.isUnlocked -> "可挑戰"
                        else -> "未解鎖"
                    },
                    enabled = progress.isUnlocked,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 5.dp),
                )
            }

            item { ModernSectionTitle(title = "設定與更多") }
            item {
                Column(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    MenuRow(Icons.Filled.Favorite, "我的收藏", "收藏的奶龍藝術與直播重溫")
                    MenuRow(Icons.Filled.Games, "遊戲中心", "快速查看全部遊戲")
                    MenuRow(Icons.Filled.Settings, "系統設置", "音效、通知與快取設定")
                    MenuRow(Icons.Filled.Info, "關於我們", "版本 v1.0.0", onClick = { showAboutDialog = true })
                }
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 32.dp)
                        .clip(RoundedCornerShape(999.dp))
                        .background(MaterialTheme.colorScheme.error.copy(alpha = 0.1f))
                        .clickable(onClick = { showLogoutConfirm = true })
                        .padding(vertical = 14.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text("退出登入", color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(96.dp))
            }
        }
    }
}

@Composable
private fun ProfileExpCard(expCurrent: Int, expMax: Int, modifier: Modifier = Modifier) {
    PressableScaleCard(onClick = { }, modifier = modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Text("成長進度", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                AnimatedBadge("EXP")
            }
            Spacer(modifier = Modifier.height(12.dp))
            LinearProgressIndicator(
                progress = { (expCurrent.toFloat() / expMax.toFloat()).coerceIn(0f, 1f) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(10.dp)
                    .clip(RoundedCornerShape(999.dp)),
                color = NailongPrimary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant,
            )
        }
    }
}

@Composable
private fun StatCard(imageRes: Int, label: String, value: String, modifier: Modifier = Modifier) {
    PressableScaleCard(onClick = { }, modifier = modifier) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(Brush.linearGradient(listOf(NailongPrimary.copy(alpha = 0.18f), NailongMint.copy(alpha = 0.18f)))),
                contentAlignment = Alignment.Center,
            ) {
                Image(
                    painter = painterResource(id = imageRes),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(0.95f),
                    contentScale = ContentScale.Fit,
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(value, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, maxLines = 1)
            Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun LevelRow(title: String, subtitle: String, badge: String, enabled: Boolean, modifier: Modifier = Modifier) {
    PressableScaleCard(onClick = { }, modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        Brush.linearGradient(
                            if (enabled) listOf(NailongSecondary.copy(alpha = 0.22f), NailongGlow.copy(alpha = 0.16f))
                            else listOf(MaterialTheme.colorScheme.surfaceVariant, MaterialTheme.colorScheme.surfaceVariant),
                        ),
                    ),
                contentAlignment = Alignment.Center,
            ) { Text(if (enabled) "⭐" else "🔒") }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                Text(subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            AnimatedBadge(badge)
        }
    }
}

@Composable
private fun MenuRow(icon: ImageVector, title: String, subtitle: String, onClick: () -> Unit = {}) {
    PressableScaleCard(onClick = onClick, modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(15.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(15.dp))
                    .background(NailongPrimary.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center,
            ) {
                androidx.compose.material3.Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                Text(subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
            androidx.compose.material3.Icon(
                Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}
