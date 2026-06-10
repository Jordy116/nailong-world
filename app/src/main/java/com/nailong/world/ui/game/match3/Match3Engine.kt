package com.nailong.world.ui.game.match3

import kotlin.random.Random
import kotlinx.coroutines.delay

/**
 * Core match-3 game engine.
 * 8x8 grid. Tile type 0..5 = 6 奶龍 images; type -1 = empty; type -2 = obstacle.
 */

const val BOARD_SIZE = 8
const val TILE_TYPES = 6
const val OBSTACLE_TYPE = -2

data class Tile(val type: Int) {
    companion object {
        val Empty = Tile(-1)
        val Obstacle = Tile(OBSTACLE_TYPE)
    }
    val isObstacle: Boolean get() = type == OBSTACLE_TYPE
    val isEmpty: Boolean get() = type == -1
    val isPlayable: Boolean get() = type in 0 until TILE_TYPES
}

data class BoardPosition(val row: Int, val col: Int)

/** A single step in the cascade: which positions matched / what score gained */
data class CascadeStep(
    val matchedPositions: List<BoardPosition>,
    val pointsGained: Int,
    val comboCount: Int,
)

class Match3Engine {

    val board = Array(BOARD_SIZE) { Array(BOARD_SIZE) { Tile(0) } }
    var score = 0
        private set
    var movesLeft: Int = 30
        private set
    var targetScore: Int = 0
        private set
    var isLevelMode: Boolean = false
        private set
    var isVictory: Boolean = false
        private set

    fun initBoard() {
        score = 0
        isVictory = false
        fillBoard()
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

    fun configureLevel(obstacles: Int, totalMoves: Int, target: Int) {
        movesLeft = totalMoves
        targetScore = target
        isLevelMode = true
        placeObstacles(obstacles)
    }

    fun configureInfinite() {
        movesLeft = Int.MAX_VALUE
        targetScore = 0
        isLevelMode = false
    }

    private fun placeObstacles(count: Int) {
        var placed = 0
        var attempts = 0
        while (placed < count && attempts < 100) {
            val row = Random.nextInt(BOARD_SIZE)
            val col = Random.nextInt(BOARD_SIZE)
            if (board[row][col].isPlayable) {
                board[row][col] = Tile.Obstacle
                placed++
            }
            attempts++
        }
    }

    private fun fillBoard() {
        for (row in 0 until BOARD_SIZE) {
            for (col in 0 until BOARD_SIZE) {
                board[row][col] = Tile(Random.nextInt(TILE_TYPES))
            }
        }
    }

    fun areAdjacent(pos1: BoardPosition, pos2: BoardPosition): Boolean {
        val dr = kotlin.math.abs(pos1.row - pos2.row)
        val dc = kotlin.math.abs(pos1.col - pos2.col)
        return (dr == 1 && dc == 0) || (dr == 0 && dc == 1)
    }

    fun trySwap(pos1: BoardPosition, pos2: BoardPosition): Boolean {
        if (!areAdjacent(pos1, pos2)) return false
        if (board[pos1.row][pos1.col].isObstacle || board[pos2.row][pos2.col].isObstacle) return false
        swap(pos1, pos2)
        val matches = findAllMatches()
        if (matches.isEmpty()) {
            swap(pos1, pos2)
            return false
        }
        if (isLevelMode) movesLeft--
        return true
    }

    /**
     * Process cascade one step at a time, returning each step for animation.
     * Returns all steps; caller decides timing.
     */
    fun processCascadeSteps(): List<CascadeStep> {
        val steps = mutableListOf<CascadeStep>()
        var combo = 1

        while (true) {
            val matches = findAllMatches()
            if (matches.isEmpty()) break

            val allMatched = mutableListOf<BoardPosition>()
            var matchedCount = 0
            for (match in matches) {
                matchedCount += match.size
                allMatched.addAll(match)
                for (pos in match) {
                    board[pos.row][pos.col] = Tile.Empty
                }
            }

            val points = matchedCount * 10 * matchedCount * combo
            score += points
            steps.add(CascadeStep(allMatched.toList(), points, combo))
            combo++

            applyGravity()
            fillEmpty()
        }

        if (isLevelMode && score >= targetScore) {
            isVictory = true
        }

        return steps
    }

    /** Legacy: process everything at once, return total points */
    fun processFullCascade(): Int {
        val steps = processCascadeSteps()
        return steps.sumOf { it.pointsGained }
    }

    private fun swap(pos1: BoardPosition, pos2: BoardPosition) {
        val temp = board[pos1.row][pos1.col]
        board[pos1.row][pos1.col] = board[pos2.row][pos2.col]
        board[pos2.row][pos2.col] = temp
    }

    private fun applyGravity() {
        for (col in 0 until BOARD_SIZE) {
            var writeRow = BOARD_SIZE - 1
            for (row in (BOARD_SIZE - 1) downTo 0) {
                if (!board[row][col].isEmpty && !board[row][col].isObstacle) {
                    board[writeRow][col] = board[row][col]
                    if (writeRow != row) board[row][col] = Tile.Empty
                    writeRow--
                }
            }
        }
    }

    private fun fillEmpty() {
        for (col in 0 until BOARD_SIZE) {
            for (row in 0 until BOARD_SIZE) {
                if (board[row][col].isEmpty) {
                    board[row][col] = Tile(Random.nextInt(TILE_TYPES))
                }
            }
        }
    }

    fun findAllMatches(): List<List<BoardPosition>> {
        val all = mutableListOf<List<BoardPosition>>()
        for (row in 0 until BOARD_SIZE) {
            var col = 0
            while (col < BOARD_SIZE) {
                val t = board[row][col]
                if (!t.isPlayable) { col++; continue }
                var end = col + 1
                while (end < BOARD_SIZE && board[row][end].type == t.type) end++
                if (end - col >= 3) all.add((col until end).map { BoardPosition(row, it) })
                col = end
            }
        }
        for (col in 0 until BOARD_SIZE) {
            var row = 0
            while (row < BOARD_SIZE) {
                val t = board[row][col]
                if (!t.isPlayable) { row++; continue }
                var end = row + 1
                while (end < BOARD_SIZE && board[end][col].type == t.type) end++
                if (end - row >= 3) all.add((row until end).map { BoardPosition(it, col) })
                row = end
            }
        }
        return all
    }

    fun hasValidMoves(): Boolean {
        for (row in 0 until BOARD_SIZE) {
            for (col in 0 until BOARD_SIZE) {
                if (!board[row][col].isPlayable) continue
                if (col + 1 < BOARD_SIZE && board[row][col + 1].isPlayable) {
                    swap(BoardPosition(row, col), BoardPosition(row, col + 1))
                    val has = findAllMatches().isNotEmpty()
                    swap(BoardPosition(row, col), BoardPosition(row, col + 1))
                    if (has) return true
                }
                if (row + 1 < BOARD_SIZE && board[row + 1][col].isPlayable) {
                    swap(BoardPosition(row, col), BoardPosition(row + 1, col))
                    val has = findAllMatches().isNotEmpty()
                    swap(BoardPosition(row, col), BoardPosition(row + 1, col))
                    if (has) return true
                }
            }
        }
        return false
    }

    fun shuffleBoard() {
        do {
            for (row in 0 until BOARD_SIZE)
                for (col in 0 until BOARD_SIZE)
                    if (board[row][col].isPlayable)
                        board[row][col] = Tile(Random.nextInt(TILE_TYPES))
        } while (!hasValidMoves() || findAllMatches().isNotEmpty())
        while (true) {
            val matches = findAllMatches()
            if (matches.isEmpty()) break
            for (match in matches)
                for (pos in match)
                    if (board[pos.row][pos.col].isPlayable)
                        board[pos.row][pos.col] = Tile(Random.nextInt(TILE_TYPES))
        }
    }

    fun isGameOver(): Boolean = isLevelMode && movesLeft <= 0 && score < targetScore
}
