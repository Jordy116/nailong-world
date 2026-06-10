package com.nailong.world.ui.game.match3

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
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
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.nailong.world.R
import com.nailong.world.ui.game.match3.model.GameMode
import com.nailong.world.viewmodel.Match3ViewModel

private val DarkBg = Color(0xFF12141C)
private val DarkCard = Color(0xFF1E2030)
private val DarkSurface = Color(0xFF2A2D3E)
private val AccentOrange = Color(0xFFFF6B35)
private val AccentYellow = Color(0xFFFFC107)
private val TextPrimary = Color(0xFFEEE8E4)
private val TextSecondary = Color(0xFF9A9490)
private val SuccessGreen = Color(0xFF4CAF50)
private val FailRed = Color(0xFFE53935)

private val tileResources = listOf(
    R.drawable.tile_nailong_1, R.drawable.tile_nailong_2,
    R.drawable.tile_nailong_3, R.drawable.tile_nailong_4,
    R.drawable.tile_nailong_5, R.drawable.tile_nailong_6,
)

/** Animation durations for cascade */
private const val MATCHED_FADE_MS = 250
private const val COMBO_GAP_MS = 200

private const val SWIPE_THRESHOLD_DP = 15f

@Composable
fun NailongMatch3Screen(
    onBack: () -> Unit,
    viewModel: Match3ViewModel = viewModel(),
) {
    val state = viewModel.state

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBg)
            .padding(top = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        // ── Top Bar ──
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "← 返回", color = AccentOrange, fontSize = 14.sp,
                modifier = Modifier.clickable(onClick = onBack),
            )
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = "🐉 奶龍消消樂",
                style = MaterialTheme.typography.titleLarge,
                color = TextPrimary, fontWeight = FontWeight.Bold,
            )
            Spacer(modifier = Modifier.weight(1f))
            Box(modifier = Modifier.size(40.dp))
        }

        Spacer(modifier = Modifier.height(8.dp))

        // ── Stats Row ──
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
        ) {
            StatCard("🏆 分數", "${state.score}")
            if (state.gameMode == GameMode.LEVEL) {
                StatCard("🎯 目標", "${state.targetScore}")
                StatCard("👟 步數", "${state.movesLeft}")
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // ── Board or Overlay ──
        when {
            state.isVictory -> VictoryOverlay(
                score = state.score, levelId = state.levelId,
                onContinue = onBack,
            )
            state.isGameOver -> DefeatOverlay(
                score = state.score, targetScore = state.targetScore,
                onRestart = { viewModel.startGame(
                    com.nailong.world.ui.game.match3.model.GameConfig(
                        mode = state.gameMode,
                        level = if (state.levelId > 0)
                            com.nailong.world.ui.game.match3.model.levels.find { it.id == state.levelId }
                        else null
                    )
                ) },
                onBack = onBack,
            )
            else -> GameBoard(
                board = state.board,
                selectedTile = state.selectedTile,
                matchedPositions = state.matchedPositions,
                comboText = state.comboText,
                onTileClick = { viewModel.onTileClick(it) },
                onSwipe = { from, to -> viewModel.onSwipe(from, to) },
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // ── Action Buttons ──
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            Button(
                onClick = { viewModel.startGame(
                    com.nailong.world.ui.game.match3.model.GameConfig(
                        mode = state.gameMode,
                        level = if (state.levelId > 0)
                            com.nailong.world.ui.game.match3.model.levels.find { it.id == state.levelId }
                        else null
                    )
                ) },
                colors = ButtonDefaults.buttonColors(containerColor = DarkSurface, contentColor = TextPrimary),
                shape = RoundedCornerShape(12.dp),
            ) { Text("🔄 重開", fontWeight = FontWeight.SemiBold) }
            Button(
                onClick = { viewModel.shuffleBoard() },
                colors = ButtonDefaults.buttonColors(containerColor = DarkSurface, contentColor = TextPrimary),
                shape = RoundedCornerShape(12.dp),
            ) { Text("🔀 洗牌", fontWeight = FontWeight.SemiBold) }
        }
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun StatCard(label: String, value: String) {
    Card(
        modifier = Modifier.width(120.dp),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = DarkCard),
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(text = label, color = TextSecondary, fontSize = 11.sp)
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = value, color = AccentYellow, fontSize = 22.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun GameBoard(
    board: List<List<Tile>>,
    selectedTile: BoardPosition?,
    matchedPositions: Set<BoardPosition>,
    comboText: String?,
    onTileClick: (BoardPosition) -> Unit,
    onSwipe: (BoardPosition, BoardPosition) -> Unit,
) {
    val density = LocalDensity.current

    Box(modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp)) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(3.dp),
        ) {
            for (row in 0 until BOARD_SIZE) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(3.dp),
                ) {
                    for (col in 0 until BOARD_SIZE) {
                        val pos = BoardPosition(row, col)
                        val tile = if (row < board.size && col < board[row].size) board[row][col] else Tile(-1)
                        val isSelected = selectedTile == pos
                        val isMatched = pos in matchedPositions

                        val bgColor = when {
                            tile.isObstacle -> Color(0xFF11131A)
                            tile.isPlayable -> DarkSurface
                            else -> Color(0xFF1A1C28)
                        }

                        // Fade-out animation for matched tiles
                        val alpha by animateFloatAsState(
                            targetValue = if (isMatched) 0f else 1f,
                            animationSpec = tween(durationMillis = 300),
                            label = "tileAlpha",
                        )

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .aspectRatio(1f)
                                .clip(RoundedCornerShape(6.dp))
                                .background(bgColor)
                                .then(
                                    when {
                                        isSelected -> Modifier.border(2.dp, AccentOrange, RoundedCornerShape(6.dp))
                                        tile.isObstacle -> Modifier.border(1.dp, Color(0xFF333344), RoundedCornerShape(6.dp))
                                        else -> Modifier
                                    }
                                )
                                .then(
                                    if (tile.isPlayable) Modifier.pointerInput(pos) {
                                        detectDragGestures(
                                            onDragStart = { onTileClick(pos) },
                                            onDrag = { change, dragAmount ->
                                                change.consume()
                                                val thresholdPx = with(density) { SWIPE_THRESHOLD_DP.dp.toPx() }
                                                val dx = dragAmount.x; val dy = dragAmount.y
                                                val targetRow = when {
                                                    dy < -thresholdPx -> pos.row - 1
                                                    dy > thresholdPx -> pos.row + 1
                                                    else -> pos.row
                                                }
                                                val targetCol = when {
                                                    dx < -thresholdPx -> pos.col - 1
                                                    dx > thresholdPx -> pos.col + 1
                                                    else -> pos.col
                                                }
                                                if ((targetRow != pos.row || targetCol != pos.col) &&
                                                    targetRow in 0 until BOARD_SIZE && targetCol in 0 until BOARD_SIZE) {
                                                    onSwipe(pos, BoardPosition(targetRow, targetCol))
                                                }
                                            },
                                        )
                                    } else Modifier
                                ),
                            contentAlignment = Alignment.Center,
                        ) {
                            when {
                                tile.isObstacle -> Text(text = "🧱", fontSize = 16.sp)
                                tile.isPlayable -> {
                                    val resId = tileResources[tile.type]
                                    androidx.compose.foundation.Image(
                                        painter = painterResource(id = resId),
                                        contentDescription = "Tile ${tile.type}",
                                        modifier = Modifier.fillMaxSize(),
                                        contentScale = ContentScale.Crop,
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Combo text overlay
        if (comboText != null) {
            Text(
                text = comboText,
                color = AccentYellow,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 12.dp),
            )
        }
    }
}

@Composable
private fun VictoryOverlay(score: Int, levelId: Int, onContinue: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(32.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = DarkCard),
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(text = "🎉", fontSize = 56.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "通關成功！", style = MaterialTheme.typography.titleLarge, color = SuccessGreen, fontWeight = FontWeight.Bold)
            if (levelId > 0) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "第 ${levelId} 關完成", color = TextSecondary, fontSize = 14.sp)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "得分", color = TextSecondary, fontSize = 13.sp)
            Text(text = "$score", color = AccentYellow, fontSize = 40.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(20.dp))
            Button(
                onClick = onContinue,
                colors = ButtonDefaults.buttonColors(containerColor = AccentOrange, contentColor = Color.White),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth(0.6f),
            ) { Text("← 返回選單", fontWeight = FontWeight.Bold) }
        }
    }
}

@Composable
private fun DefeatOverlay(score: Int, targetScore: Int, onRestart: () -> Unit, onBack: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(32.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = DarkCard),
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(text = "😞", fontSize = 56.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "挑戰失敗", style = MaterialTheme.typography.titleLarge, color = FailRed, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = "目標: $targetScore 分", color = TextSecondary, fontSize = 13.sp)
            Text(text = "得分: $score 分", color = TextSecondary, fontSize = 13.sp)
            Spacer(modifier = Modifier.height(20.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Button(
                    onClick = onRestart,
                    colors = ButtonDefaults.buttonColors(containerColor = AccentOrange, contentColor = Color.White),
                    shape = RoundedCornerShape(12.dp),
                ) { Text("🔄 再試一次", fontWeight = FontWeight.Bold) }
                Button(
                    onClick = onBack,
                    colors = ButtonDefaults.buttonColors(containerColor = DarkSurface, contentColor = TextPrimary),
                    shape = RoundedCornerShape(12.dp),
                ) { Text("← 返回", fontWeight = FontWeight.SemiBold) }
            }
        }
    }
}
