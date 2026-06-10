package com.nailong.world.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nailong.world.data.GameDataStore
import com.nailong.world.ui.game.memory.MEMORY_PAIRS
import com.nailong.world.ui.game.memory.MEMORY_ROWS
import com.nailong.world.ui.game.memory.MEMORY_COLS
import com.nailong.world.ui.game.memory.MemoryCard
import com.nailong.world.ui.game.memory.MemoryGameEngine
import com.nailong.world.ui.game.match3.model.LevelProgress
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

data class MemoryGameState(
    val cards: List<MemoryCard> = List(MEMORY_ROWS * MEMORY_COLS) { MemoryCard(it, 0) },
    val moves: Int = 0,
    val matchedPairs: Int = 0,
    val isComplete: Boolean = false,
    val elapsedSeconds: Int = 0,
    val bestTime: Int = 0,       // best time in seconds (from DataStore)
    val bestMoves: Int = 0,      // best moves (from DataStore)
    val isChecking: Boolean = false,  // waiting for flip-back delay
)

class MemoryViewModel : ViewModel() {

    private val engine = MemoryGameEngine()

    var state by mutableStateOf(MemoryGameState())
        private set

    init {
        resetGame()
    }

    fun resetGame() {
        engine.initGame()
        val store = LevelProgress.getStore()
        updateState()
        state = state.copy(
            bestTime = store.getMemoryBestTime(),
            bestMoves = store.getMemoryBestMoves(),
        )
        engine.startTimer()
    }

    fun onCardClick(index: Int) {
        if (state.isChecking || state.isComplete) return
        if (!engine.flipCard(index)) return

        updateState()

        if (engine.secondFlipped != null) {
            // Two cards flipped — check for match
            val first = engine.cards[engine.firstFlipped!!]
            val second = engine.cards[engine.secondFlipped!!]

            if (first.isMatched) {
                // Match! already handled in engine
                updateState()
                checkComplete()
            } else {
                // No match — wait and flip back
                state = state.copy(isChecking = true)
                viewModelScope.launch {
                    delay(900L)  // 0.9s delay
                    engine.resetFlips()
                    updateState()
                    state = state.copy(isChecking = false)
                }
            }
        }
    }

    private fun checkComplete() {
        if (engine.isComplete) {
            engine.stopTimer()
            val store = LevelProgress.getStore()
            store.saveMemoryGame(engine.moves, engine.elapsedSeconds)
            updateState()
            state = state.copy(
                bestTime = store.getMemoryBestTime(),
                bestMoves = store.getMemoryBestMoves(),
            )
        }
    }

    private fun updateState() {
        state = MemoryGameState(
            cards = engine.cards.toList(),
            moves = engine.moves,
            matchedPairs = engine.matchedPairs,
            isComplete = engine.isComplete,
            elapsedSeconds = engine.elapsedSeconds,
            bestTime = state.bestTime,
            bestMoves = state.bestMoves,
            isChecking = state.isChecking,
        )
    }

    // Called by timer effect in the screen
    fun tick() {
        if (!engine.isComplete && !state.isComplete) {
            engine.tickSecond()
            state = state.copy(elapsedSeconds = engine.elapsedSeconds)
        }
    }
}
