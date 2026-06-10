package com.nailong.world.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nailong.world.ui.game.match3.BOARD_COLS
import com.nailong.world.ui.game.match3.BOARD_ROWS
import com.nailong.world.ui.game.match3.BoardPosition
import com.nailong.world.ui.game.match3.Match3Engine
import com.nailong.world.ui.game.match3.Tile
import com.nailong.world.ui.game.match3.model.GameConfig
import com.nailong.world.ui.game.match3.model.GameMode
import com.nailong.world.ui.game.match3.model.LevelProgress
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

data class Match3GameState(
    val board: List<List<Tile>> = List(BOARD_ROWS) { List(BOARD_COLS) { Tile(0) } },
    val score: Int = 0,
    val bestScore: Int = 0,
    val movesLeft: Int = 30,
    val targetScore: Int = 0,
    val isGameOver: Boolean = false,
    val isVictory: Boolean = false,
    val isAnimating: Boolean = false,
    val selectedTile: BoardPosition? = null,
    val gameMode: GameMode = GameMode.INFINITE,
    val levelId: Int = -1,
    val matchedPositions: Set<BoardPosition> = emptySet(),
    val comboText: String? = null,
)

class Match3ViewModel : ViewModel() {

    private val engine = Match3Engine()
    private var currentConfig: GameConfig? = null

    var state by mutableStateOf(Match3GameState())
        private set

    init {
        val config = com.nailong.world.GameConfigHolder.config
        startGame(config)
    }

    fun startGame(config: GameConfig) {
        currentConfig = config
        engine.initBoard()
        when (config.mode) {
            GameMode.INFINITE -> {
                engine.configureInfinite()
                engine.setBestScore(LevelProgress.getHighScore(-1))
            }
            GameMode.LEVEL -> {
                val level = config.level
                if (level != null) {
                    engine.configureLevel(level.obstacles, level.moves, level.targetScore)
                    engine.setBestScore(LevelProgress.getHighScore(level.id))
                }
            }
        }
        updateState(config)
    }

    fun onTileClick(pos: BoardPosition) {
        if (state.isAnimating || state.isGameOver || state.isVictory) return
        val cur = state.selectedTile
        if (cur == null) { state = state.copy(selectedTile = pos); return }
        if (cur == pos) { state = state.copy(selectedTile = null); return }
        performSwap(cur, pos)
    }

    fun onSwipe(from: BoardPosition, to: BoardPosition) {
        if (state.isAnimating || state.isGameOver || state.isVictory) return
        performSwap(from, to)
    }

    private fun performSwap(p1: BoardPosition, p2: BoardPosition) {
        if (!engine.trySwap(p1, p2)) { state = state.copy(selectedTile = null); return }
        state = state.copy(selectedTile = null, isAnimating = true)
        viewModelScope.launch { animateCascade() }
    }

    private suspend fun animateCascade() {
        val steps = engine.processCascadeSteps()
        val config = currentConfig ?: return

        for ((i, step) in steps.withIndex()) {
            state = state.copy(
                matchedPositions = step.matchedPositions.toSet(),
                comboText = "Combo x${step.comboCount}  +${step.pointsGained}",
                score = engine.score,
                bestScore = engine.bestScore,
            )
            delay(250)
            updateState(config)
            if (i < steps.size - 1) delay(200)
        }

        when {
            engine.isVictory && config.mode == GameMode.LEVEL && config.level != null -> {
                LevelProgress.saveHighScore(config.level.id, engine.score)
                LevelProgress.unlockNext(config.level.id)
            }
            config.mode == GameMode.INFINITE -> {
                LevelProgress.saveHighScore(-1, engine.score)
            }
        }

        state = Match3GameState(
            board = engine.board.map { r -> r.map { it.copy() }.toList() }.toList(),
            score = engine.score,
            bestScore = engine.bestScore,
            movesLeft = engine.movesLeft,
            targetScore = config.targetScore,
            isGameOver = engine.isGameOver(),
            isVictory = engine.isVictory,
            isAnimating = false,
            selectedTile = null,
            gameMode = config.mode,
            levelId = config.level?.id ?: -1,
        )
    }

    fun saveScoreOnExit() {
        val config = currentConfig ?: return
        if (config.mode == GameMode.INFINITE && engine.score > 0)
            LevelProgress.saveHighScore(-1, engine.score)
    }

    fun shuffleBoard() {
        if (state.isAnimating || state.isGameOver) return
        engine.shuffleBoard()
        currentConfig?.let { updateState(it) }
    }

    private fun updateState(config: GameConfig) {
        state = Match3GameState(
            board = engine.board.map { r -> r.map { it.copy() }.toList() }.toList(),
            score = engine.score,
            bestScore = engine.bestScore,
            movesLeft = engine.movesLeft,
            targetScore = config.targetScore,
            isGameOver = engine.isGameOver(),
            isVictory = engine.isVictory,
            isAnimating = state.isAnimating,
            selectedTile = null,
            gameMode = config.mode,
            levelId = config.level?.id ?: -1,
            matchedPositions = state.matchedPositions,
            comboText = state.comboText,
        )
    }
}
