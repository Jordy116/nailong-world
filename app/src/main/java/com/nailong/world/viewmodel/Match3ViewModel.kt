package com.nailong.world.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.nailong.world.ui.game.match3.BoardPosition
import com.nailong.world.ui.game.match3.Match3Engine
import com.nailong.world.ui.game.match3.Tile
import kotlinx.coroutines.delay

/**
 * Game state that can be observed by the UI.
 */
data class Match3GameState(
    val board: Array<Array<Tile>> = emptyArray(),
    val score: Int = 0,
    val movesLeft: Int = 30,
    val isGameOver: Boolean = false,
    val isAnimating: Boolean = false,
    val selectedTile: BoardPosition? = null,
    val lastSwapResult: String? = null,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Match3GameState) return false
        return score == other.score &&
                movesLeft == other.movesLeft &&
                isGameOver == other.isGameOver &&
                isAnimating == other.isAnimating &&
                selectedTile == other.selectedTile
    }

    override fun hashCode(): Int {
        var result = score
        result = 31 * result + movesLeft
        result = 31 * result + isGameOver.hashCode()
        result = 31 * result + isAnimating.hashCode()
        result = 31 * result + (selectedTile?.hashCode() ?: 0)
        return result
    }
}

class Match3ViewModel : ViewModel() {

    private val engine = Match3Engine()

    var state by mutableStateOf(Match3GameState())
        private set

    /** Animating match positions for visual feedback */
    var matchAnimations by mutableStateOf<Set<BoardPosition>>(emptySet())
        private set

    /** Gravity animation positions */
    var gravityAnimations by mutableStateOf<Map<BoardPosition, BoardPosition>>(emptyMap())
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
            // First selection
            state = state.copy(selectedTile = pos)
            return
        }

        if (currentSelected == pos) {
            // Deselect
            state = state.copy(selectedTile = null)
            return
        }

        // Attempt swap
        val result = engine.trySwap(currentSelected, pos)
        if (!result.validSwap) {
            // Invalid swap — just deselect
            state = state.copy(selectedTile = null)
            return
        }

        // Valid swap — deselect, run cascade
        state = state.copy(selectedTile = null, isAnimating = true)

        // Show match animation briefly
        val allMatchedPositions = mutableSetOf<BoardPosition>()
        var allMatches = engine.findAllMatches()

        // Run the cascade in a viewModelScope would be better but we do it step-based
        // For simplicity, process all at once
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
        state = Match3GameState(
            board = engine.board.map { it.clone() }.toTypedArray(),
            score = engine.score,
            movesLeft = engine.movesLeft,
            isGameOver = engine.isGameOver(),
            isAnimating = false,
            selectedTile = state.selectedTile,
        )
    }
}
