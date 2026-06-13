package com.nailong.world.ui.game.suika

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.nailong.world.R
import com.nailong.world.viewmodel.SuikaViewModel

private val DarkBg = Color(0xFF12141C)
private val DarkCard = Color(0xFF1E2030)
private val DarkSurface = Color(0xFF2A2D3E)
private val AccentOrange = Color(0xFFFF6B35)
private val AccentYellow = Color(0xFFFFC107)
private val TextPrimary = Color(0xFFEEE8E4)
private val TextSecondary = Color(0xFF9A9490)
private val DangerRed = Color(0xFFFF3344)

private val tileResources = listOf(
    R.drawable.tile_nailong_1, R.drawable.tile_nailong_2,
    R.drawable.tile_nailong_3, R.drawable.tile_nailong_4,
    R.drawable.tile_nailong_5, R.drawable.tile_nailong_6,
)

private val levelColors = listOf(
    Color(0xFF4CAF50), Color(0xFFFF6B35), Color(0xFFFFC107),
    Color(0xFFE53935), Color(0xFF9C27B0), Color(0xFFB71C1C),
)

/** Scale factor from game coords to dp */
private const val GAME_SCALE = 0.45f

@Composable
fun SuikaGameScreen(
    onBack: () -> Unit,
    viewModel: SuikaViewModel = viewModel(),
) {
    val state = viewModel.state
    var containerSize by remember { mutableStateOf(IntSize(1, 1)) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBg)
            .padding(top = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        // Top Bar
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "←", color = AccentOrange, fontSize = 20.sp,
                modifier = Modifier.clickable(onClick = {
                    viewModel.saveScore(); onBack()
                }),
            )
            Spacer(Modifier.weight(1f))
            Text("🐉 合成大奶龍", color = TextPrimary, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.weight(1f))
            Text(
                text = "重置", color = Color(0xFFE53935), fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.clickable(onClick = { viewModel.startGame() }),
            )
        }

        Spacer(Modifier.height(6.dp))

        // Score Row
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            ScoreCard("🏆 分數", "${state.score}", AccentYellow, Modifier.weight(1f))
            ScoreCard("⭐ 最高", "${state.highScore}", AccentOrange, Modifier.weight(1f))

            // Next preview
            Card(
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = DarkCard),
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(4.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text("下一隻", color = TextSecondary, fontSize = 9.sp)
                    Box(
                        modifier = Modifier.size(32.dp)
                            .clip(CircleShape)
                            .background(DarkSurface),
                        contentAlignment = Alignment.Center,
                    ) {
                        state.nextDragon?.let {
                            val img = tileResources[it.level.coerceIn(1, 6) - 1]
                            androidx.compose.foundation.Image(
                                painter = painterResource(id = img),
                                contentDescription = null,
                                modifier = Modifier.fillMaxSize(0.9f),
                                contentScale = ContentScale.Crop,
                            )
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(8.dp))

        // Game Container
        val density = LocalDensity.current

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = 8.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xFF0D0F17))
                .onSizeChanged { containerSize = it }
                .pointerInput(Unit) {
                    detectHorizontalDragGestures(
                        onDragEnd = { viewModel.dropDragon() },
                        onHorizontalDrag = { _, dragAmount ->
                            val scaleX = if (containerSize.width > 0)
                                containerSize.width.toFloat() / SuikaContainer.WIDTH else 1f
                            val scaleY = if (containerSize.height > 0)
                                containerSize.height.toFloat() / SuikaContainer.HEIGHT else 1f
                            val scale = minOf(scaleX, scaleY).takeIf { it > 0f } ?: 1f
                            viewModel.setDropX(viewModel.state.dropX + dragAmount / scale)
                        },
                    )
                }
                .clickable { viewModel.dropDragon() },
            contentAlignment = Alignment.TopCenter,
        ) {
            // Calculate game-to-screen conversion
            val screenW = containerSize.width.toFloat()
            val screenH = containerSize.height.toFloat()
            val scaleX = screenW / SuikaContainer.WIDTH.toFloat()
            val scaleY = screenH / SuikaContainer.HEIGHT.toFloat()
            val scale = minOf(scaleX, scaleY)
            val offsetX = (screenW - SuikaContainer.WIDTH.toFloat() * scale) / 2f
            val offsetY = (screenH - SuikaContainer.HEIGHT.toFloat() * scale) / 2f

            fun gx(x: Float) = x * scale + offsetX
            fun gy(y: Float) = y * scale + offsetY
            fun grPx(r: Float) = r * scale
            fun grDp(r: Float) = with(density) { (r * scale).toDp() }

            // Danger line
            if (state.warningFlash) {
                Box(
                    modifier = Modifier
                        .offset {
                            IntOffset(
                                gx(SuikaContainer.LEFT_WALL + 4f).toInt(),
                                gy(SuikaContainer.DANGER_LINE).toInt(),
                            )
                        }
                        .size(
                            width = with(density) {
                                ((SuikaContainer.RIGHT_WALL - SuikaContainer.LEFT_WALL - 8f) * scale).toDp()
                            },
                            height = 2.dp,
                        )
                        .background(DangerRed),
                )
            }

            // Dragons
            for (d in state.dragons) {
                if (!d.isActive) continue
                val radiusPx = grPx(d.radius)
                val px = gx(d.x) - radiusPx
                val py = gy(d.y) - radiusPx
                val size = grDp(d.radius * 2)

                Box(
                    modifier = Modifier
                        .offset { IntOffset(px.toInt(), py.toInt()) }
                        .size(size),
                    contentAlignment = Alignment.Center,
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape)
                            .background(DarkSurface)
                            .border(3.dp, levelColors[d.level.coerceIn(1, 6) - 1], CircleShape),
                        contentAlignment = Alignment.Center,
                    ) {
                        val img = tileResources[d.level.coerceIn(1, 6) - 1]
                        androidx.compose.foundation.Image(
                            painter = painterResource(id = img),
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(0.88f),
                            contentScale = ContentScale.Crop,
                        )
                    }
                }
            }

            // Current dragon preview
            state.currentDragon?.let { d ->
                val previewRadiusPx = grPx(d.radius * 0.7f)
                val previewSize = grDp(d.radius * 1.4f)
                val previewX = gx(state.dropX) - previewRadiusPx
                val previewY = gy(SuikaContainer.DANGER_LINE + 10f)

                Box(
                    modifier = Modifier
                        .offset { IntOffset(previewX.toInt(), previewY.toInt()) }
                        .size(previewSize)
                        .alpha(0.7f),
                    contentAlignment = Alignment.Center,
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape)
                            .background(DarkSurface)
                            .border(2.dp, levelColors[d.level.coerceIn(1, 6) - 1], CircleShape),
                        contentAlignment = Alignment.Center,
                    ) {
                        val img = tileResources[d.level.coerceIn(1, 6) - 1]
                        androidx.compose.foundation.Image(
                            painter = painterResource(id = img),
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(0.85f),
                            contentScale = ContentScale.Crop,
                        )
                    }
                }
            }

            // Merge text
            state.lastMergeText?.let { text ->
                Text(
                    text = text,
                    color = AccentYellow,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 8.dp),
                )
            }

            // Game over overlay
            if (state.isGameOver) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    GameOverOverlay(state.score, state.highScore,
                        onRestart = { viewModel.startGame() },
                        onBack = {
                            viewModel.saveScore(); onBack()
                        },
                    )
                }
            }
        }
    }
}

@Composable
private fun ScoreCard(label: String, value: String, color: Color, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = DarkCard),
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp, horizontal = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(text = value, color = color, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Text(text = label, color = TextSecondary, fontSize = 9.sp)
        }
    }
}

@Composable
private fun GameOverOverlay(
    score: Int, highScore: Int,
    onRestart: () -> Unit, onBack: () -> Unit,
) {
    val isNewRecord = score >= highScore && score > 0
    Card(
        modifier = Modifier.fillMaxWidth(0.85f),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = DarkCard),
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text("💥", fontSize = 56.sp)
            Spacer(Modifier.height(8.dp))
            Text("遊戲結束",
                style = MaterialTheme.typography.titleLarge,
                color = Color(0xFFE53935), fontWeight = FontWeight.Bold)
            if (isNewRecord) {
                Spacer(Modifier.height(4.dp))
                Text("🎊 新紀錄！", color = AccentYellow, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.height(12.dp))
            Text("最終分數", color = TextSecondary, fontSize = 13.sp)
            Text("$score", color = AccentYellow, fontSize = 40.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(16.dp))
            Button(
                onClick = onRestart,
                colors = ButtonDefaults.buttonColors(containerColor = AccentOrange, contentColor = Color.White),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth(0.7f),
            ) { Text("🔄 再玩一次", fontWeight = FontWeight.Bold) }
            Spacer(Modifier.height(8.dp))
            Button(
                onClick = onBack,
                colors = ButtonDefaults.buttonColors(containerColor = DarkSurface, contentColor = TextPrimary),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth(0.5f),
            ) { Text("← 返回", fontWeight = FontWeight.SemiBold) }
        }
    }
}
