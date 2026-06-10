package com.nailong.world.ui.game.match3

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
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
import com.nailong.world.ui.game.match3.model.GameConfig
import com.nailong.world.ui.game.match3.model.GameMode
import com.nailong.world.ui.game.match3.model.levels
import com.nailong.world.viewmodel.Match3ViewModel

// ── Colours ──
private val DarkBg = Color(0xFF12141C)
private val DarkCard = Color(0xFF1E2030)
private val DarkSurface = Color(0xFF2A2D3E)
private val AccentOrange = Color(0xFFFF6B35)
private val AccentYellow = Color(0xFFFFC107)
private val TextPrimary = Color(0xFFEEE8E4)
private val TextSecondary = Color(0xFF9A9490)
private val SuccessGreen = Color(0xFF4CAF50)
private val FailRed = Color(0xFFE53935)

// Tile border colors (matching description):
// 0=蔬菜奶龍(白)→Green, 1=摸肚子胖奶龍→Orange, 2=抱胸大眼奶龍→Yellow
// 3=仰頭尖叫奶龍→Red, 4=摸下巴點頭奶龍→Purple, 5=仰頭大笑奶龍→DeepRed
private val tileBorderColors = listOf(
    Color(0xFF4CAF50),  // Green
    Color(0xFFFF6B35),  // Orange
    Color(0xFFFFC107),  // Yellow
    Color(0xFFE53935),  // Red
    Color(0xFF9C27B0),  // Purple
    Color(0xFFB71C1C),  // Deep Red
)

private val tileResources = listOf(
    R.drawable.tile_nailong_1, R.drawable.tile_nailong_2,
    R.drawable.tile_nailong_3, R.drawable.tile_nailong_4,
    R.drawable.tile_nailong_5, R.drawable.tile_nailong_6,
)

private const val SWIPE_THRESHOLD_DP = 15f
private const val BORDER_WIDTH = 2.5f

@Composable
fun NailongMatch3Screen(
    onBack: () -> Unit,
    viewModel: Match3ViewModel = viewModel(),
) {
    val state = viewModel.state
    val modeLabel = when (state.gameMode) {
        GameMode.INFINITE -> "∞ 無限模式"
        GameMode.LEVEL -> "第 ${state.levelId} 關"
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBg),
    ) {
        // ── Top Bar ──
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp)
                .padding(top = 24.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "←", color = AccentOrange, fontSize = 20.sp,
                modifier = Modifier.clickable(onClick = {
                    viewModel.saveScoreOnExit(); onBack()
                }),
            )
            Spacer(modifier = Modifier.weight(1f))
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("奶龍消消樂", color = TextPrimary, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                Text(modeLabel, color = AccentYellow, fontSize = 11.sp)
            }
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = "重置", color = FailRed, fontSize = 13.sp, fontWeight = FontWeight.SemiBold,
                modifier = Modifier.clickable(onClick = {
                    viewModel.startGame(GameConfig(state.gameMode,
                        if (state.levelId > 0) levels.find { it.id == state.levelId } else null))
                }),
            )
        }

        // ── Score Cards Row ──
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            ScoreCard("得分", "${state.score}", TextSecondary, Modifier.weight(1f))
            ScoreCard("🏆 最佳", "${state.bestScore}", AccentYellow, Modifier.weight(1f))
            ScoreCard("∞ 模式", modeLabel, AccentYellow, Modifier.weight(1f))
        }

        Spacer(modifier = Modifier.height(6.dp))

        // ── Hint Text ──
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(text = "🤚", fontSize = 16.sp)
            Spacer(modifier = Modifier.width(6.dp))
            Text("點擊/滑動交換方塊", color = AccentYellow, fontSize = 12.sp)
        }

        Spacer(modifier = Modifier.height(4.dp))

        // ── Game Board (7 cols x 10 rows, tall layout) ──
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 6.dp),
        ) {
            when {
                state.isVictory -> VictoryOverlay(state.score, state.levelId, onBack)
                state.isGameOver -> DefeatOverlay(state.score, state.targetScore, {
                    viewModel.startGame(GameConfig(state.gameMode,
                        if (state.levelId > 0) levels.find { it.id == state.levelId } else null))
                }, onBack)
                else -> Box(modifier = Modifier.fillMaxSize()) {
                    GameBoard(
                        board = state.board,
                        selectedTile = state.selectedTile,
                        matchedPositions = state.matchedPositions,
                        onTileClick = { viewModel.onTileClick(it) },
                        onSwipe = { from, to -> viewModel.onSwipe(from, to) },
                    )
                    state.comboText?.let { ComboPopup(it) }
                }
            }
        }

        // ── Bottom Preview Row ──
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(DarkCard)
                .padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
        ) {
            for (type in 0 until TILE_TYPES) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(DarkSurface)
                        .border(BORDER_WIDTH.dp, tileBorderColors[type], RoundedCornerShape(6.dp)),
                    contentAlignment = Alignment.Center,
                ) {
                    androidx.compose.foundation.Image(
                        painter = painterResource(id = tileResources[type]),
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(0.85f),
                        contentScale = ContentScale.Crop,
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
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp, horizontal = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(text = label, color = TextSecondary, fontSize = 10.sp)
            Text(text = value, color = color, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }
    }
}

// ── Combo Popup ──
@Composable
private fun ComboPopup(text: String) {
    var visible by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf(false) }
    androidx.compose.runtime.LaunchedEffect(text) { visible = true }
    val scale by animateFloatAsState(if (visible) 1f else 0.3f, tween(200), label = "s")
    val alpha by animateFloatAsState(if (visible) 1f else 0f, tween(300), label = "a")
    if (visible) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Box(
                modifier = Modifier
                    .scale(scale)
                    .background(
                        Brush.horizontalGradient(listOf(AccentOrange, AccentYellow)),
                        RoundedCornerShape(20.dp),
                    )
                    .padding(horizontal = 28.dp, vertical = 14.dp),
            ) {
                Text(text, color = Color.Black, fontSize = 22.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
    androidx.compose.runtime.LaunchedEffect(text) { kotlinx.coroutines.delay(500); visible = false }
}

// ── Game Board 7x10 ──
@Composable
private fun GameBoard(
    board: List<List<Tile>>,
    selectedTile: BoardPosition?,
    matchedPositions: Set<BoardPosition>,
    onTileClick: (BoardPosition) -> Unit,
    onSwipe: (BoardPosition, BoardPosition) -> Unit,
) {
    val density = LocalDensity.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 4.dp),
        verticalArrangement = Arrangement.spacedBy(3.dp),
    ) {
        for (row in 0 until BOARD_ROWS) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                horizontalArrangement = Arrangement.spacedBy(3.dp),
            ) {
                for (col in 0 until BOARD_COLS) {
                    val pos = BoardPosition(row, col)
                    val tile = if (row < board.size && col < board[row].size) board[row][col] else Tile(-1)
                    val isSelected = selectedTile == pos
                    val isMatched = pos in matchedPositions

                    val bgColor = when {
                        tile.isObstacle -> Color(0xFF11131A)
                        tile.isPlayable -> DarkSurface
                        else -> Color(0xFF1A1C28)
                    }
                    val borderColor = if (tile.isPlayable && tile.type in 0 until TILE_TYPES)
                        tileBorderColors[tile.type] else Color(0xFF333344)
                    val alpha by animateFloatAsState(
                        if (isMatched) 0f else 1f, tween(300), label = "f",
                    )

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .clip(RoundedCornerShape(6.dp))
                            .background(bgColor)
                            .then(
                                if (tile.isPlayable || tile.isObstacle)
                                    Modifier.border(BORDER_WIDTH.dp, borderColor, RoundedCornerShape(6.dp))
                                else Modifier
                            )
                            .then(
                                if (isSelected) Modifier.border(2.dp, Color.White, RoundedCornerShape(6.dp)) else Modifier
                            )
                            .then(
                                if (tile.isPlayable) Modifier.pointerInput(pos) {
                                    detectDragGestures(
                                        onDragStart = { onTileClick(pos) },
                                        onDrag = { ch, amt ->
                                            ch.consume()
                                            val t = with(density) { SWIPE_THRESHOLD_DP.dp.toPx() }
                                            val dx = amt.x; val dy = amt.y
                                            val tr = if (dy < -t) pos.row - 1 else if (dy > t) pos.row + 1 else pos.row
                                            val tc = if (dx < -t) pos.col - 1 else if (dx > t) pos.col + 1 else pos.col
                                            if ((tr != pos.row || tc != pos.col) && tr in 0 until BOARD_ROWS && tc in 0 until BOARD_COLS)
                                                onSwipe(pos, BoardPosition(tr, tc))
                                        },
                                    )
                                } else Modifier
                            ),
                        contentAlignment = Alignment.Center,
                    ) {
                        when {
                            tile.isObstacle -> Text("🧱", fontSize = 14.sp)
                            tile.isPlayable -> androidx.compose.foundation.Image(
                                painter = painterResource(id = tileResources[tile.type]),
                                contentDescription = null,
                                modifier = Modifier.fillMaxSize(0.88f),
                                contentScale = ContentScale.Crop,
                            )
                        }
                    }
                }
            }
        }
    }
}

// ── Overlays ──

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
            Text("🎉", fontSize = 56.sp)
            Spacer(Modifier.height(8.dp))
            Text("通關成功！", style = MaterialTheme.typography.titleLarge, color = SuccessGreen, fontWeight = FontWeight.Bold)
            if (levelId > 0) { Spacer(Modifier.height(4.dp)); Text("第 ${levelId} 關完成", color = TextSecondary, fontSize = 14.sp) }
            Spacer(Modifier.height(8.dp))
            Text("得分", color = TextSecondary, fontSize = 13.sp)
            Text("$score", color = AccentYellow, fontSize = 40.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(20.dp))
            Button(onClick = onContinue,
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
            Text("😞", fontSize = 56.sp)
            Spacer(Modifier.height(8.dp))
            Text("挑戰失敗", style = MaterialTheme.typography.titleLarge, color = FailRed, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(4.dp))
            Text("目標: $targetScore 分", color = TextSecondary, fontSize = 13.sp)
            Text("得分: $score 分", color = TextSecondary, fontSize = 13.sp)
            Spacer(Modifier.height(20.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Button(onClick = onRestart,
                    colors = ButtonDefaults.buttonColors(containerColor = AccentOrange, contentColor = Color.White),
                    shape = RoundedCornerShape(12.dp),
                ) { Text("🔄 再試一次", fontWeight = FontWeight.Bold) }
                Button(onClick = onBack,
                    colors = ButtonDefaults.buttonColors(containerColor = DarkSurface, contentColor = TextPrimary),
                    shape = RoundedCornerShape(12.dp),
                ) { Text("← 返回", fontWeight = FontWeight.SemiBold) }
            }
        }
    }
}
