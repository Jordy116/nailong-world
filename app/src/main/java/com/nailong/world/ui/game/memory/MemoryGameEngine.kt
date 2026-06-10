package com.nailong.world.ui.game.memory

import kotlin.random.Random

/**
 * Memory card matching game engine.
 * 4×3 grid (12 cards, 6 pairs).
 */

const val MEMORY_ROWS = 3
const val MEMORY_COLS = 4
const val MEMORY_TOTAL = MEMORY_ROWS * MEMORY_COLS  // 12
const val MEMORY_PAIRS = MEMORY_TOTAL / 2  // 6

data class MemoryCard(
    val id: Int,            // unique card ID (0..11)
    val pairId: Int,        // which pair (0..5) → maps to tile type
    var isFlipped: Boolean = false,
    var isMatched: Boolean = false,
)

class MemoryGameEngine {

    val cards = mutableListOf<MemoryCard>()
    var moves = 0
        private set
    var matchedPairs = 0
        private set
    var isComplete: Boolean = false
        private set
    var firstFlipped: Int? = null  // index of first flipped card
    var secondFlipped: Int? = null  // index of second flipped card

    /** Timer tracking */
    var elapsedSeconds = 0
        private set
    var isTimerRunning = false
        private set

    fun initGame() {
        cards.clear()
        // Create 6 pairs
        for (pairId in 0 until MEMORY_PAIRS) {
            cards.add(MemoryCard(id = pairId * 2, pairId = pairId))
            cards.add(MemoryCard(id = pairId * 2 + 1, pairId = pairId))
        }
        // Shuffle
        cards.shuffle(Random)
        moves = 0
        matchedPairs = 0
        isComplete = false
        firstFlipped = null
        secondFlipped = null
        elapsedSeconds = 0
    }

    /** Flip a card at the given index. Returns true if it was a valid flip. */
    fun flipCard(index: Int): Boolean {
        if (index !in cards.indices) return false
        val card = cards[index]
        if (card.isFlipped || card.isMatched) return false
        if (firstFlipped != null && secondFlipped != null) return false  // wait for reset

        card.isFlipped = true

        if (firstFlipped == null) {
            firstFlipped = index
        } else {
            secondFlipped = index
            moves++
            val firstCard = cards[firstFlipped!!]
            if (firstCard.pairId == card.pairId) {
                // Match!
                firstCard.isMatched = true
                card.isMatched = true
                matchedPairs++
                if (matchedPairs == MEMORY_PAIRS) {
                    isComplete = true
                }
                firstFlipped = null
                secondFlipped = null
                return true  // match, no need to flip back
            }
            // No match — caller must handle delay and reset
        }
        return true
    }

    /** Flip unmatched cards back. Call after delay. */
    fun resetFlips() {
        val first = firstFlipped ?: return
        val second = secondFlipped ?: return
        cards[first].isFlipped = false
        cards[second].isFlipped = false
        firstFlipped = null
        secondFlipped = null
    }

    fun tickSecond() {
        if (isTimerRunning && !isComplete) {
            elapsedSeconds++
        }
    }

    fun startTimer() { isTimerRunning = true }
    fun stopTimer() { isTimerRunning = false }
}
