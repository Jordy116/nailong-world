package com.nailong.world.ui.game.match3

import kotlin.random.Random

/**
 * Core match-3 game engine.
 * Handles board state, matching, cascading, and scoring.
 *
 * Board is 8x8 grid. Each tile has a type (0..5) corresponding to one of the 6 奶龍 images.
 * Type -1 means empty.
 */

const val BOARD_SIZE = 8
const val TILE_TYPES = 6

data class Tile(val type: Int) {
    companion object {
        val Empty = Tile(-1)
    }
}

data class BoardPosition(val row: Int, val col: Int)

data class SwapResult(
    val matches: List<List<BoardPosition>>,
    val gainedScore: Int,
    val validSwap: Boolean,
)

class Match3Engine {

    val board = Array(BOARD_SIZE) { Array(BOARD_SIZE) { Tile(0) } }
    var score = 0
        private set
    var movesLeft = 30
        private set

    /** Initialize a new board with no initial matches */
    fun initBoard() {
        score = 0
        movesLeft = 30
        fillBoard()
        // Remove any initial matches by regenerating
        while (true) {
            val matches = findAllMatches()
            if (matches.isEmpty()) break
            for (match in matches) {
                for (pos in match) {
                    board[pos.row][pos.col] = Tile(Random.nextInt(TILE_TYPES))
                }
            }
        }
    }

    private fun fillBoard() {
        for (row in 0 until BOARD_SIZE) {
            for (col in 0 until BOARD_SIZE) {
                board[row][col] = Tile(Random.nextInt(TILE_TYPES))
            }
        }
    }

    /** Check if two adjacent positions can be swapped */
    fun areAdjacent(pos1: BoardPosition, pos2: BoardPosition): Boolean {
        val dr = kotlin.math.abs(pos1.row - pos2.row)
        val dc = kotlin.math.abs(pos1.col - pos2.col)
        return (dr == 1 && dc == 0) || (dr == 0 && dc == 1)
    }

    /** Attempt a swap. Returns match results if valid. */
    fun trySwap(pos1: BoardPosition, pos2: BoardPosition): SwapResult {
        if (!areAdjacent(pos1, pos2)) {
            return SwapResult(emptyList(), 0, false)
        }

        // Perform swap
        swap(pos1, pos2)

        // Check for matches
        val matches = findAllMatches()
        if (matches.isEmpty()) {
            // Swap back — invalid move
            swap(pos1, pos2)
            return SwapResult(emptyList(), 0, false)
        }

        movesLeft--

        // Calculate score
        var gainedScore = 0
        val matchedTiles = mutableSetOf<BoardPosition>()
        for (match in matches) {
            for (pos in match) {
                matchedTiles.add(pos)
            }
            gainedScore += match.size * 10 * match.size
        }
        score += gainedScore

        return SwapResult(matches, gainedScore, true)
    }

    /** Remove matched tiles, apply gravity, fill gaps. Returns sequence of board states for animation. */
    fun processCascade(): List<List<BoardPosition>> {
        val cascadeSteps = mutableListOf<List<BoardPosition>>()

        while (true) {
            // Remove matched tiles (type = -1)
            val matches = findAllMatches()
            if (matches.isEmpty()) break

            val matchedPositions = matches.flatten().distinct()
            cascadeSteps.add(matchedPositions.toList())

            // Remove them
            for (pos in matchedPositions) {
                board[pos.row][pos.col] = Tile.Empty
            }

            // Apply gravity
            applyGravity()

            // Fill empty spaces at top
            fillEmpty()
        }

        return cascadeSteps
    }

    /** Remove all matches iteratively (full cascade with scoring) */
    fun processFullCascade(): Int {
        var totalGained = 0
        var comboMultiplier = 1

        while (true) {
            val matches = findAllMatches()
            if (matches.isEmpty()) break

            var matchedCount = 0
            for (match in matches) {
                matchedCount += match.size
                for (pos in match) {
                    board[pos.row][pos.col] = Tile.Empty
                }
            }

            val points = matchedCount * 10 * matchedCount * comboMultiplier
            totalGained += points
            comboMultiplier++

            applyGravity()
            fillEmpty()
        }

        if (totalGained > 0) {
            score += totalGained
        }
        return totalGained
    }

    /** Apply gravity: move tiles down to fill empty spaces */
    private fun applyGravity() {
        for (col in 0 until BOARD_SIZE) {
            var writeRow = BOARD_SIZE - 1
            for (row in (BOARD_SIZE - 1) downTo 0) {
                if (board[row][col].type != -1) {
                    board[writeRow][col] = board[row][col]
                    if (writeRow != row) {
                        board[row][col] = Tile.Empty
                    }
                    writeRow--
                }
            }
        }
    }

    /** Fill remaining empty tiles at the top with random new tiles */
    private fun fillEmpty() {
        for (col in 0 until BOARD_SIZE) {
            for (row in 0 until BOARD_SIZE) {
                if (board[row][col].type == -1) {
                    board[row][col] = Tile(Random.nextInt(TILE_TYPES))
                }
            }
        }
    }

    private fun swap(pos1: BoardPosition, pos2: BoardPosition) {
        val temp = board[pos1.row][pos1.col]
        board[pos1.row][pos1.col] = board[pos2.row][pos2.col]
        board[pos2.row][pos2.col] = temp
    }

    /** Find all matches (3+ in a row/column) */
    fun findAllMatches(): List<List<BoardPosition>> {
        val allMatches = mutableListOf<List<BoardPosition>>()

        // Horizontal matches
        for (row in 0 until BOARD_SIZE) {
            var col = 0
            while (col < BOARD_SIZE) {
                val type = board[row][col].type
                if (type == -1) { col++; continue }
                var end = col + 1
                while (end < BOARD_SIZE && board[row][end].type == type) {
                    end++
                }
                if (end - col >= 3) {
                    allMatches.add((col until end).map { BoardPosition(row, it) })
                }
                col = end
            }
        }

        // Vertical matches
        for (col in 0 until BOARD_SIZE) {
            var row = 0
            while (row < BOARD_SIZE) {
                val type = board[row][col].type
                if (type == -1) { row++; continue }
                var end = row + 1
                while (end < BOARD_SIZE && board[end][col].type == type) {
                    end++
                }
                if (end - row >= 3) {
                    allMatches.add((row until end).map { BoardPosition(it, col) })
                }
                row = end
            }
        }

        return allMatches
    }

    /** Check if any valid moves exist on the board */
    fun hasValidMoves(): Boolean {
        for (row in 0 until BOARD_SIZE) {
            for (col in 0 until BOARD_SIZE) {
                // Try swap right
                if (col + 1 < BOARD_SIZE) {
                    swap(BoardPosition(row, col), BoardPosition(row, col + 1))
                    if (findAllMatches().isNotEmpty()) {
                        swap(BoardPosition(row, col), BoardPosition(row, col + 1))
                        return true
                    }
                    swap(BoardPosition(row, col), BoardPosition(row, col + 1))
                }
                // Try swap down
                if (row + 1 < BOARD_SIZE) {
                    swap(BoardPosition(row, col), BoardPosition(row + 1, col))
                    if (findAllMatches().isNotEmpty()) {
                        swap(BoardPosition(row, col), BoardPosition(row + 1, col))
                        return true
                    }
                    swap(BoardPosition(row, col), BoardPosition(row + 1, col))
                }
            }
        }
        return false
    }

    /** Shuffle the board if no valid moves */
    fun shuffleBoard() {
        do {
            for (row in 0 until BOARD_SIZE) {
                for (col in 0 until BOARD_SIZE) {
                    board[row][col] = Tile(Random.nextInt(TILE_TYPES))
                }
            }
        } while (!hasValidMoves() || findAllMatches().isNotEmpty())

        // Clear initial matches
        while (true) {
            val matches = findAllMatches()
            if (matches.isEmpty()) break
            for (match in matches) {
                for (pos in match) {
                    board[pos.row][pos.col] = Tile(Random.nextInt(TILE_TYPES))
                }
            }
        }
    }

    fun isGameOver(): Boolean = movesLeft <= 0
}
