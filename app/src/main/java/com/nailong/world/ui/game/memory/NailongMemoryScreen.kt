package com.nailong.world.ui.game.memory

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.nailong.world.R
import com.nailong.world.viewmodel.MemoryGameState
import com.nailong.world.viewmodel.MemoryViewModel

// ── Colours ──
private val DarkBg = Color(0xFF12141C)
private val DarkCard = Color(0xFF1E2030)
private val DarkSurface = Color(0xFF2A2D3E)
private val AccentOrange = Color(0xFFFF6B35)
private val AccentYellow = Color(0xFFFFC107)
private val TextPrimary = Color(0xFFEEE8E4)
private val TextSecondary = Color(0xFF9A9490)
private val SuccessGreen = Color(0xFF4CAF50)

// Tile border colors matching match3
private val tileBorderColors = listOf(
    Color(0xFF4CAF50), Color(0xFFFF6B35), Color(0xFFFFC107),
    Color(0xFFE53935), Color(0xFF9C27B0), Color(0xFFB71C1C),
)

private val tileResources = listOf(
    R.drawable.tile_nailong_1, R.drawable.tile_nailong_2,
    R.drawable.tile_nailong_3, R.drawable.tile_nailong_4,
    R.drawable.tile_nailong_5, R.drawable.tile_nailong_6,
)

/** Format seconds to mm:ss */
private fun formatTime(sec: Int): String = "${sec / 60}:${(sec % 60).toString().padStart(2, '0')}"

@Composable
fun NailongMemoryScreen(
    onBack: () -> Unit,
    viewModel: MemoryViewModel = viewModel(),
) {
    val state = viewModel.state

    // Timer tick every second
    LaunchedEffect(Unit) {
        while (true) {
            kotlinx.coroutines.delay(1000)
            viewModel.tick()
        }
    }

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
                modifier = Modifier.clickable(onClick = onBack),
            )
            Spacer(Modifier.weight(1f))
            Text(
                text = "🃏 奶龍記憶翻牌",
                color = TextPrimary, fontSize = 16.sp, fontWeight = FontWeight.Bold,
            )
            Spacer(Modifier.weight(1f))
            Text(
                text = "重置", color = Color(0xFFE53935), fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.clickable(onClick = { viewModel.resetGame() }),
            )
        }

        Spacer(Modifier.height(8.dp))

        // ── Stats Cards ──
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            StatCard("👆 翻牌", "${state.moves}", Modifier.weight(1f))
            StatCard("⏱ 時間", formatTime(state.elapsedSeconds), Modifier.weight(1f))
            StatCard("🎯 配對", "${state.matchedPairs}/${MEMORY_PAIRS}", Modifier.weight(1f))
        }

        Spacer(Modifier.height(8.dp))

        // Best record
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.End,
        ) {
            Text(
                text = "🏆 最佳: ${formatTime(state.bestTime)} / ${state.bestMoves}步",
                color = AccentYellow, fontSize = 12.sp,
            )
        }

        Spacer(Modifier.height(12.dp))

        // ── Card Grid 4×3 ──
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
        ) {
            if (state.isComplete) {
                VictoryOverlay(
                    moves = state.moves,
                    time = state.elapsedSeconds,
                    bestTime = state.bestTime,
                    bestMoves = state.bestMoves,
                    onRestart = { viewModel.resetGame() },
                    onBack = onBack,
                )
            } else {
                CardGrid(state, onCardClick = { viewModel.onCardClick(it) })
            }
        }
    }
}

@Composable
private fun StatCard(label: String, value: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = DarkCard),
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(text = value, color = AccentYellow, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Text(text = label, color = TextSecondary, fontSize = 10.sp)
        }
    }
}

// ── Card Grid ──
@Composable
private fun CardGrid(state: MemoryGameState, onCardClick: (Int) -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(horizontal = 4.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        for (row in 0 until MEMORY_ROWS) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                for (col in 0 until MEMORY_COLS) {
                    val index = row * MEMORY_COLS + col
                    val card = if (index < state.cards.size) state.cards[index] else null
                    if (card != null) {
                        MemoryCardView(
                            card = card,
                            onClick = { onCardClick(index) },
                            modifier = Modifier.weight(1f),
                        )
                    }
                }
            }
        }
    }
}

// ── Flip Card with 3D Rotation ──
@Composable
private fun MemoryCardView(
    card: MemoryCard,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val isFaceUp = card.isFlipped || card.isMatched

    // Rotation from 0 to 180 degrees
    val rotation by animateFloatAsState(
        targetValue = if (isFaceUp) 180f else 0f,
        animationSpec = tween(durationMillis = 400),
        label = "flip",
    )

    val borderColor = if (card.isMatched) SuccessGreen
        else if (isFaceUp && card.pairId in 0 until 6) tileBorderColors[card.pairId]
        else Color(0xFF333344)

    Box(
        modifier = modifier
            .fillMaxHeight()
            .aspectRatio(0.75f)
            .graphicsLayer {
                rotationY = rotation
                // Hide when facing away
                this.alpha = if (rotation in 1f..179f) 0.99f else 1f
            }
            .clip(RoundedCornerShape(10.dp))
            .background(DarkSurface)
            .clickable(onClick = onClick)
            .padding(0.dp),
        contentAlignment = Alignment.Center,
    ) {
        if (rotation <= 90f) {
            // Back face (0° ~ 90°)
            CardBack()
        } else {
            // Front face (90° ~ 180°) — show tile image
            if (card.pairId in 0 until 6) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(3.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(DarkSurface)
                        .then(
                            if (card.isMatched) Modifier.background(SuccessGreen.copy(alpha = 0.15f))
                            else Modifier
                        ),
                    contentAlignment = Alignment.Center,
                ) {
                    androidx.compose.foundation.Image(
                        painter = painterResource(id = tileResources[card.pairId]),
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(0.85f),
                        contentScale = ContentScale.Crop,
                    )
                }
            } else {
                Text("?", color = TextSecondary, fontSize = 20.sp)
            }
        }
    }
}

// ── Card Back Design ──
@Composable
private fun CardBack() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1A1C28), RoundedCornerShape(10.dp))
            .then(
                Modifier
                    .padding(4.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(
                        Brush.verticalGradient(listOf(Color(0xFF2A2D3E), Color(0xFF1E2030))),
                    ),
            ),
        contentAlignment = Alignment.Center,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("🐉", fontSize = 24.sp)
            Spacer(Modifier.height(4.dp))
            Text("?", color = AccentYellow.copy(alpha = 0.6f), fontSize = 14.sp, fontWeight = FontWeight.Bold)
        }
    }
}

// ── Victory Overlay ──
@Composable
private fun VictoryOverlay(
    moves: Int, time: Int, bestTime: Int, bestMoves: Int,
    onRestart: () -> Unit, onBack: () -> Unit,
) {
    val isNewRecord = time <= bestTime || moves <= bestMoves

    Card(
        modifier = Modifier.fillMaxWidth().padding(24.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = DarkCard),
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text("🎉", fontSize = 56.sp)
            Spacer(Modifier.height(8.dp))
            Text("記憶翻牌通關！",
                style = MaterialTheme.typography.titleLarge,
                color = SuccessGreen, fontWeight = FontWeight.Bold)
            if (isNewRecord) {
                Spacer(Modifier.height(4.dp))
                Text("🎊 新紀錄！", color = AccentYellow, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.height(12.dp))
            Text("用時", color = TextSecondary, fontSize = 13.sp)
            Text(formatTime(time), color = AccentYellow, fontSize = 36.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(4.dp))
            Text("翻牌 ${moves} 次", color = TextPrimary, fontSize = 15.sp)
            Spacer(Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("🏆 最佳時間", color = TextSecondary, fontSize = 10.sp)
                    Text(formatTime(if (bestTime < 999) bestTime else time), color = AccentYellow, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("🏆 最少步數", color = TextSecondary, fontSize = 10.sp)
                    Text("${if (bestMoves < 999) bestMoves else moves} 步", color = AccentYellow, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                }
            }
            Spacer(Modifier.height(20.dp))
            Button(onClick = onRestart,
                colors = ButtonDefaults.buttonColors(containerColor = AccentOrange, contentColor = Color.White),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth(0.7f),
            ) { Text("🔄 再玩一次", fontWeight = FontWeight.Bold) }
            Spacer(Modifier.height(8.dp))
            Button(onClick = onBack,
                colors = ButtonDefaults.buttonColors(containerColor = DarkSurface, contentColor = TextPrimary),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth(0.5f),
            ) { Text("← 返回", fontWeight = FontWeight.SemiBold) }
        }
    }
}
