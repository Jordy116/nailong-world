package com.nailong.world.ui.game.suika

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.material3.MaterialTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.nailong.world.R
import com.nailong.world.viewmodel.SuikaViewModel

// ── Colours ──
private val DarkBg = Color(0xFF12141C)
private val DarkCard = Color(0xFF1E2030)
private val DarkSurface = Color(0xFF2A2D3E)
private val AccentOrange = Color(0xFFFF6B35)
private val AccentYellow = Color(0xFFFFC107)
private val TextPrimary = Color(0xFFEEE8E4)
private val TextSecondary = Color(0xFF9A9490)
private val WallColor = Color(0xFF3A3A4A)
private val DangerRed = Color(0xFFFF3344)

// Tile resources
private val tileResources = listOf(
    R.drawable.tile_nailong_1, R.drawable.tile_nailong_2,
    R.drawable.tile_nailong_3, R.drawable.tile_nailong_4,
    R.drawable.tile_nailong_5, R.drawable.tile_nailong_6,
)

// Tile border colors
private val tileBorderColors = listOf(
    Color(0xFF4CAF50), Color(0xFFFF6B35), Color(0xFFFFC107),
    Color(0xFFE53935), Color(0xFF9C27B0), Color(0xFFB71C1C),
)

/** Game coordinate to screen pixel conversion */
data class GameViewTransform(
    val offsetX: Float,
    val offsetY: Float,
    val scale: Float,
)

@Composable
fun SuikaGameScreen(
    onBack: () -> Unit,
    viewModel: SuikaViewModel = viewModel(),
) {
    val state = viewModel.state
    var gameViewSize by remember { mutableStateOf(IntSize(1, 1)) }
    val density = LocalDensity.current

    with(density) { }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBg)
            .padding(top = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        // ── Top Bar ──
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

        // ── Score Row ──
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            ScoreCard("🏆 分數", "${state.score}", AccentYellow, Modifier.weight(1f))
            ScoreCard("⭐ 最高", "${state.highScore}", AccentOrange, Modifier.weight(1f))

            // Next dragon preview
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
                            val level = it.level.coerceIn(1, 6)
                            androidx.compose.foundation.Image(
                                painter = painterResource(id = tileResources[level - 1]),
                                contentDescription = null,
                                modifier = Modifier.fillMaxSize(0.85f),
                                contentScale = ContentScale.Crop,
                            )
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(8.dp))

        // ── Game Container ──
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = 8.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xFF0D0F17))
                .onSizeChanged { gameViewSize = it }
                .pointerInput(Unit) {
                    detectHorizontalDragGestures(
                        onDragEnd = { viewModel.dropDragon() },
                        onHorizontalDrag = { _, dragAmount ->
                            val scale = if (gameViewSize.width > 0)
                                SuikaContainer.WIDTH / gameViewSize.width else 1f
                            viewModel.setDropX(viewModel.state.dropX + dragAmount * scale)
                        },
                    )
                }
                .clickable { viewModel.dropDragon() },
        ) {
            // Canvas game render
            GameCanvas(
                state = state,
                viewSize = gameViewSize,
            )

            // Merge text overlay
            state.lastMergeText?.let { text ->
                Text(
                    text = text,
                    color = AccentYellow,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = 8.dp),
                )
            }

            // Game over overlay
            if (state.isGameOver) {
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
private fun GameCanvas(
    state: SuikaUiState,
    viewSize: IntSize,
) {
    Canvas(
        modifier = Modifier.fillMaxSize(),
    ) {
        val sx = viewSize.width.toFloat() / SuikaContainer.WIDTH
        val sy = viewSize.height.toFloat() / SuikaContainer.HEIGHT
        val s = minOf(sx, sy)
        val ox = (viewSize.width - SuikaContainer.WIDTH * s) / 2f
        val oy = (viewSize.height - SuikaContainer.HEIGHT * s) / 2f

        // Helper to transform game coord to screen coord
        fun gx(x: Float) = x * s + ox
        fun gy(y: Float) = y * s + oy
        fun gr(r: Float) = r * s

        // ── Draw walls ──
        val wallThick = gr(SuikaContainer.WALL_THICKNESS)

        // Draw background
        drawRect(
            color = Color(0xFF0D0F17),
            topLeft = Offset(gx(SuikaContainer.LEFT_WALL), gy(SuikaContainer.TOP_WALL)),
            size = Size(
                gx(SuikaContainer.RIGHT_WALL) - gx(SuikaContainer.LEFT_WALL),
                gy(SuikaContainer.BOTTOM_WALL) - gy(SuikaContainer.TOP_WALL),
            ),
        )

        // Draw walls
        // Left wall
        drawRect(Color(0xFF2A2A3A), Offset(0f, 0f),
            Size(gx(SuikaContainer.LEFT_WALL), viewSize.height.toFloat()))
        // Right wall
        drawRect(Color(0xFF2A2A3A),
            Offset(gx(SuikaContainer.RIGHT_WALL), 0f),
            Size(viewSize.width - gx(SuikaContainer.RIGHT_WALL), viewSize.height.toFloat()))
        // Bottom floor
        drawRect(Color(0xFF2A2A3A),
            Offset(0f, gy(SuikaContainer.BOTTOM_WALL)),
            Size(viewSize.width.toFloat(), 20f))

        // ── Danger line ──
        val dangerY = gy(SuikaContainer.DANGER_LINE)
        val flashAlpha = if (state.warningFlash) 1f else 0.3f
        drawLine(
            color = DangerRed.copy(alpha = flashAlpha),
            start = Offset(gx(SuikaContainer.LEFT_WALL + 4f), dangerY),
            end = Offset(gx(SuikaContainer.RIGHT_WALL - 4f), dangerY),
            strokeWidth = 2f,
            pathEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 6f), 0f),
        )

        // ── Draw dragons ──
        for (d in state.dragons) {
            if (!d.isActive) continue
            drawDragon(gx(d.x), gy(d.y), gr(d.radius), d.level)
        }

        // ── Draw current dragon preview ──
        state.currentDragon?.let { d ->
            val px = gx(d.x)
            val py = gy(SuikaContainer.DANGER_LINE + d.radius * 0.8f)
            val pr = gr(d.radius * 0.7f)
            drawDragon(px, py, pr, d.level)
        }

        // ── Draw particles ──
        for (p in state.particles) {
            drawCircle(
                color = AccentYellow.copy(alpha = p.alpha),
                radius = p.radius * s,
                center = Offset(gx(p.x), gy(p.y)),
            )
        }

        // ── Draw merge effects ──
        for (me in state.mergeEffects) {
            val alpha = (me.lifetime / 30f).coerceIn(0f, 1f)
            val r = 20f * s * (1f - me.lifetime / 30f)
            drawCircle(
                color = AccentOrange.copy(alpha = alpha * 0.5f),
                radius = r + 10f * s,
                center = Offset(gx(me.x), gy(me.y)),
                style = Stroke(width = 3f),
            )
        }
    }
}

private fun DrawScope.drawDragon(x: Float, y: Float, r: Float, level: Int) {
    val lvl = level.coerceIn(1, 6)
    val borderColor = tileBorderColors[lvl - 1]

    // Shadow
    drawCircle(Color.Black.copy(alpha = 0.3f), radius = r, center = Offset(x + 2f, y + 2f))
    // Body
    drawCircle(DarkSurface, radius = r, center = Offset(x, y))
    // Border
    drawCircle(borderColor, radius = r, center = Offset(x, y),
        style = Stroke(width = r * 0.12f))
}

@Composable
private fun GameOverOverlay(
    score: Int, highScore: Int,
    onRestart: () -> Unit, onBack: () -> Unit,
) {
    val isNewRecord = score >= highScore
    Card(
        modifier = Modifier.fillMaxWidth().padding(24.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = DarkCard),
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text("💥", fontSize = 56.sp)
            Spacer(Modifier.height(8.dp))
            Text("遊戲結束", style = MaterialTheme.typography.titleLarge,
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
