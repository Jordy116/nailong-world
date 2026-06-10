package com.nailong.world.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.nailong.world.ui.game.match3.BOARD_SIZE
import com.nailong.world.ui.game.match3.BoardPosition
import com.nailong.world.ui.game.match3.Match3Engine
import com.nailong.world.ui.game.match3.Tile
import com.nailong.world.ui.game.match3.model.GameConfig
import com.nailong.world.ui.game.match3.model.GameMode
import com.nailong.world.ui.game.match3.model.LevelProgress

data class Match3GameState(
    val board: List<List<Tile>> = List(BOARD_SIZE) { List(BOARD_SIZE) { Tile(0) } },
    val score: Int = 0,
    val movesLeft: Int = 30,
    val targetScore: Int = 0,
    val isGameOver: Boolean = false,
    val isVictory: Boolean = false,
    val isAnimating: Boolean = false,
    val selectedTile: BoardPosition? = null,
    val gameMode: GameMode = GameMode.INFINITE,
    val levelId: Int = -1,
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

    /** Start a game with the given configuration */
    fun startGame(config: GameConfig) {
        currentConfig = config
        engine.initBoard()

        when (config.mode) {
            GameMode.INFINITE -> {
                engine.configureInfinite()
            }
            GameMode.LEVEL -> {
                val level = config.level
                if (level != null) {
                    engine.configureLevel(level.obstacles, level.moves, level.targetScore)
                }
            }
        }

        updateState(config)
    }

    fun onTileClick(pos: BoardPosition) {
        if (state.isAnimating || state.isGameOver || state.isVictory) return

        val currentSelected = state.selectedTile
        if (currentSelected == null) {
            state = state.copy(selectedTile = pos)
            return
        }

        if (currentSelected == pos) {
            state = state.copy(selectedTile = null)
            return
        }

        performSwap(currentSelected, pos)
    }

    fun onSwipe(from: BoardPosition, to: BoardPosition) {
        if (state.isAnimating || state.isGameOver || state.isVictory) return
        performSwap(from, to)
    }

    private fun performSwap(pos1: BoardPosition, pos2: BoardPosition) {
        if (!engine.trySwap(pos1, pos2)) {
            state = state.copy(selectedTile = null)
            return
        }

        state = state.copy(selectedTile = null, isAnimating = true)
        engine.processFullCascade()
        currentConfig?.let { updateState(it) }
        state = state.copy(isAnimating = false)
    }

    fun shuffleBoard() {
        if (state.isAnimating || state.isGameOver) return
        engine.shuffleBoard()
        currentConfig?.let { updateState(it) }
    }

    private fun updateState(config: GameConfig) {
        val boardList = engine.board.map { row ->
            row.map { it.copy() }.toList()
        }.toList()

        // Save progress on victory
        if (engine.isVictory) {
            if (config.mode == GameMode.LEVEL && config.level != null) {
                LevelProgress.saveHighScore(config.level.id, engine.score)
                LevelProgress.unlockNext(config.level.id)
            } else if (config.mode == GameMode.INFINITE) {
                LevelProgress.saveHighScore(-1, engine.score)
            }
        }

        state = Match3GameState(
            board = boardList,
            score = engine.score,
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
}
