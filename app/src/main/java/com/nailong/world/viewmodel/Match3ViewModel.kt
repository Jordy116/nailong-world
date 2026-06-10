package com.nailong.world.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.nailong.world.ui.game.match3.BOARD_SIZE
import com.nailong.world.ui.game.match3.BoardPosition
import com.nailong.world.ui.game.match3.Match3Engine
import com.nailong.world.ui.game.match3.Tile

/**
 * Game state that can be observed by the UI.
 * Uses a List-based board for structural comparison.
 */
data class Match3GameState(
    val board: List<List<Tile>> = List(BOARD_SIZE) { List(BOARD_SIZE) { Tile(0) } },
    val score: Int = 0,
    val movesLeft: Int = 30,
    val isGameOver: Boolean = false,
    val isAnimating: Boolean = false,
    val selectedTile: BoardPosition? = null,
)

class Match3ViewModel : ViewModel() {

    private val engine = Match3Engine()

    var state by mutableStateOf(Match3GameState())
        private set

    init {
        resetGame()
    }

    fun resetGame() {
        engine.initBoard()
        updateState()
    }

    /** Handle tile selection / swap attempt */
    fun onTileClick(pos: BoardPosition) {
        if (state.isAnimating || state.isGameOver) return

        val currentSelected = state.selectedTile
        if (currentSelected == null) {
            state = state.copy(selectedTile = pos)
            return
        }

        if (currentSelected == pos) {
            state = state.copy(selectedTile = null)
            return
        }

        // Attempt swap
        performSwap(currentSelected, pos)
    }

    /** Handle swipe gesture between two adjacent positions */
    fun onSwipe(from: BoardPosition, to: BoardPosition) {
        if (state.isAnimating || state.isGameOver) return
        performSwap(from, to)
    }

    /** Common swap logic used by both tap and swipe */
    private fun performSwap(pos1: BoardPosition, pos2: BoardPosition) {
        val result = engine.trySwap(pos1, pos2)
        if (!result.validSwap) {
            state = state.copy(selectedTile = null)
            return
        }

        state = state.copy(selectedTile = null, isAnimating = true)
        engine.processFullCascade()
        updateState()
        state = state.copy(isAnimating = false)
    }

    fun shuffleBoard() {
        if (state.isAnimating) return
        engine.shuffleBoard()
        updateState()
    }

    private fun updateState() {
        val boardList = engine.board.map { row ->
            row.map { it.copy() }.toList()
        }.toList()

        state = Match3GameState(
            board = boardList,
            score = engine.score,
            movesLeft = engine.movesLeft,
            isGameOver = engine.isGameOver(),
            isAnimating = false,
            selectedTile = null,
        )
    }
}
