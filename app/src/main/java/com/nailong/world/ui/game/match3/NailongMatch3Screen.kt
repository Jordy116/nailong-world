package com.nailong.world.ui.game.match3

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.nailong.world.R
import com.nailong.world.viewmodel.Match3ViewModel

private val DarkBackground = Color(0xFF12141C)
private val DarkCard = Color(0xFF1E2030)
private val DarkSurface = Color(0xFF2A2D3E)
private val AccentOrange = Color(0xFFFF6B35)
private val AccentYellow = Color(0xFFFFC107)
private val TextPrimary = Color(0xFFEEE8E4)
private val TextSecondary = Color(0xFF9A9490)

private val tileResources = listOf(
    R.drawable.tile_nailong_1,
    R.drawable.tile_nailong_2,
    R.drawable.tile_nailong_3,
    R.drawable.tile_nailong_4,
    R.drawable.tile_nailong_5,
    R.drawable.tile_nailong_6,
)

/** Minimum drag distance in dp to trigger a swap */
private const val SWIPE_THRESHOLD_DP = 15f

@Composable
fun NailongMatch3Screen(
    onBack: () -> Unit = {},
    viewModel: Match3ViewModel = viewModel(),
) {
    val state = viewModel.state

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
            .padding(top = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        // Top Bar
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

        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
        ) {
            StatCard("🏆 分數", "${state.score}")
            StatCard("👟 步數", "${state.movesLeft}")
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (state.isGameOver) {
            GameOverOverlay(state.score, { viewModel.resetGame() }, onBack)
        } else {
            GameBoard(
                board = state.board,
                selectedTile = state.selectedTile,
                onTileClick = { viewModel.onTileClick(it) },
                onSwipe = { from, to -> viewModel.onSwipe(from, to) },
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            Button(
                onClick = { viewModel.resetGame() },
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
        modifier = Modifier.width(140.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = DarkCard),
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(text = label, color = TextSecondary, fontSize = 12.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = value, color = AccentYellow, fontSize = 24.sp, fontWeight = FontWeight.Bold)
        }
    }
}

/**
 * Track tile dimensions at runtime so drag gestures can resolve board positions.
 */
@Composable
private fun GameBoard(
    board: List<List<Tile>>,
    selectedTile: BoardPosition?,
    onTileClick: (BoardPosition) -> Unit,
    onSwipe: (BoardPosition, BoardPosition) -> Unit,
) {
    val density = LocalDensity.current

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp),
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

                    // Calculate swipe direction based on drag gesture
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1f)
                            .clip(RoundedCornerShape(6.dp))
                            .background(if (tile.type in 0 until TILE_TYPES) DarkSurface else Color(0xFF1A1C28))
                            .then(
                                if (isSelected) Modifier.border(2.dp, AccentOrange, RoundedCornerShape(6.dp))
                                else Modifier
                            )
                            .pointerInput(pos) {
                                detectDragGestures(
                                    onDragStart = {
                                        onTileClick(pos)
                                    },
                                    onDragEnd = {
                                        // handled by onDrag
                                    },
                                    onDragCancel = {
                                        // deselect handled by viewModel
                                    },
                                    onDrag = { change, dragAmount ->
                                        change.consume()
                                        val thresholdPx = with(density) { SWIPE_THRESHOLD_DP.dp.toPx() }
                                        val dx = dragAmount.x
                                        val dy = dragAmount.y

                                        val targetRow = when {
                                            dy < -thresholdPx -> pos.row - 1  // swipe up
                                            dy > thresholdPx -> pos.row + 1  // swipe down
                                            else -> pos.row
                                        }
                                        val targetCol = when {
                                            dx < -thresholdPx -> pos.col - 1  // swipe left
                                            dx > thresholdPx -> pos.col + 1  // swipe right
                                            else -> pos.col
                                        }

                                        if (targetRow != pos.row || targetCol != pos.col) {
                                            if (targetRow in 0 until BOARD_SIZE && targetCol in 0 until BOARD_SIZE) {
                                                onSwipe(pos, BoardPosition(targetRow, targetCol))
                                            }
                                        }
                                    },
                                )
                            },
                        contentAlignment = Alignment.Center,
                    ) {
                        if (tile.type in 0 until TILE_TYPES) {
                            val resId = tileResources[tile.type]
                            androidx.compose.foundation.Image(
                                painter = painterResource(id = resId),
                                contentDescription = "Tile ${tile.type}",
                                modifier = Modifier
                                    .fillMaxSize(),
                                contentScale = ContentScale.Crop,
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun GameOverOverlay(score: Int, onRestart: () -> Unit, onBack: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(32.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = DarkCard),
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(text = "🎉", fontSize = 48.sp)
            Spacer(modifier = Modifier.height(12.dp))
            Text(text = "遊戲結束！", style = MaterialTheme.typography.titleLarge, color = TextPrimary, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "最終分數", color = TextSecondary, fontSize = 14.sp)
            Text(text = "$score", color = AccentYellow, fontSize = 48.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(20.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Button(
                    onClick = onRestart,
                    colors = ButtonDefaults.buttonColors(containerColor = AccentOrange, contentColor = Color.White),
                    shape = RoundedCornerShape(12.dp),
                ) { Text("🔄 再玩一次", fontWeight = FontWeight.Bold) }
                Button(
                    onClick = onBack,
                    colors = ButtonDefaults.buttonColors(containerColor = DarkSurface, contentColor = TextPrimary),
                    shape = RoundedCornerShape(12.dp),
                ) { Text("← 返回", fontWeight = FontWeight.SemiBold) }
            }
        }
    }
}
