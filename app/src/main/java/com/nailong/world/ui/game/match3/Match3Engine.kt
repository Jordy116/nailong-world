package com.nailong.world.ui.game.match3

import kotlin.random.Random
import kotlin.random.nextInt

/**
 * Core match-3 game engine.
 * 7×10 grid (7 columns × 10 rows). Tile type 0..5 = 6 奶龍 images.
 */

const val BOARD_COLS = 7
const val BOARD_ROWS = 10
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

data class CascadeStep(
    val matchedPositions: List<BoardPosition>,
    val pointsGained: Int,
    val comboCount: Int,
)

class Match3Engine {

    val board = Array(BOARD_ROWS) { Array(BOARD_COLS) { Tile(0) } }
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
    var bestScore: Int = 0
        private set

    fun initBoard() {
        score = 0
        isVictory = false
        bestScore = 0
        fillBoard()
        while (true) {
            val matches = findAllMatches()
            if (matches.isEmpty()) break
            for (match in matches)
                for (pos in match)
                    board[pos.row][pos.col] = Tile(Random.nextInt(TILE_TYPES))
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

    fun setBestScore(score: Int) { bestScore = score }

    private fun placeObstacles(count: Int) {
        var placed = 0
        var attempts = 0
        while (placed < count && attempts < 100) {
            val row = Random.nextInt(BOARD_ROWS)
            val col = Random.nextInt(BOARD_COLS)
            if (board[row][col].isPlayable) {
                board[row][col] = Tile.Obstacle
                placed++
            }
            attempts++
        }
    }

    private fun fillBoard() {
        for (row in 0 until BOARD_ROWS)
            for (col in 0 until BOARD_COLS)
                board[row][col] = Tile(Random.nextInt(TILE_TYPES))
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
        if (matches.isEmpty()) { swap(pos1, pos2); return false }
        if (isLevelMode) movesLeft--
        return true
    }

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
                for (pos in match) board[pos.row][pos.col] = Tile.Empty
            }
            val points = matchedCount * 10 * matchedCount * combo
            score += points
            steps.add(CascadeStep(allMatched.toList(), points, combo))
            combo++
            applyGravity()
            fillEmpty()
        }
        if (score > bestScore) bestScore = score
        if (isLevelMode && score >= targetScore) isVictory = true
        return steps
    }

    fun processFullCascade(): Int = processCascadeSteps().sumOf { it.pointsGained }

    private fun swap(pos1: BoardPosition, pos2: BoardPosition) {
        val t = board[pos1.row][pos1.col]
        board[pos1.row][pos1.col] = board[pos2.row][pos2.col]
        board[pos2.row][pos2.col] = t
    }

    private fun applyGravity() {
        for (col in 0 until BOARD_COLS) {
            var wr = BOARD_ROWS - 1
            for (row in (BOARD_ROWS - 1) downTo 0) {
                if (!board[row][col].isEmpty && !board[row][col].isObstacle) {
                    board[wr][col] = board[row][col]
                    if (wr != row) board[row][col] = Tile.Empty
                    wr--
                }
            }
        }
    }

    private fun fillEmpty() {
        for (col in 0 until BOARD_COLS)
            for (row in 0 until BOARD_ROWS)
                if (board[row][col].isEmpty)
                    board[row][col] = Tile(Random.nextInt(TILE_TYPES))
    }

    fun findAllMatches(): List<List<BoardPosition>> {
        val all = mutableListOf<List<BoardPosition>>()
        for (row in 0 until BOARD_ROWS) {
            var c = 0
            while (c < BOARD_COLS) {
                val t = board[row][c]; if (!t.isPlayable) { c++; continue }
                var end = c + 1
                while (end < BOARD_COLS && board[row][end].type == t.type) end++
                if (end - c >= 3) all.add((c until end).map { BoardPosition(row, it) })
                c = end
            }
        }
        for (col in 0 until BOARD_COLS) {
            var r = 0
            while (r < BOARD_ROWS) {
                val t = board[r][col]; if (!t.isPlayable) { r++; continue }
                var end = r + 1
                while (end < BOARD_ROWS && board[end][col].type == t.type) end++
                if (end - r >= 3) all.add((r until end).map { BoardPosition(it, col) })
                r = end
            }
        }
        return all
    }

    fun hasValidMoves(): Boolean {
        for (row in 0 until BOARD_ROWS) {
            for (col in 0 until BOARD_COLS) {
                if (!board[row][col].isPlayable) continue
                if (col + 1 < BOARD_COLS && board[row][col + 1].isPlayable) {
                    swap(BoardPosition(row, col), BoardPosition(row, col + 1))
                    val h = findAllMatches().isNotEmpty()
                    swap(BoardPosition(row, col), BoardPosition(row, col + 1))
                    if (h) return true
                }
                if (row + 1 < BOARD_ROWS && board[row + 1][col].isPlayable) {
                    swap(BoardPosition(row, col), BoardPosition(row + 1, col))
                    val h = findAllMatches().isNotEmpty()
                    swap(BoardPosition(row, col), BoardPosition(row + 1, col))
                    if (h) return true
                }
            }
        }
        return false
    }

    fun shuffleBoard() {
        do {
            for (row in 0 until BOARD_ROWS)
                for (col in 0 until BOARD_COLS)
                    if (board[row][col].isPlayable)
                        board[row][col] = Tile(Random.nextInt(TILE_TYPES))
        } while (!hasValidMoves() || findAllMatches().isNotEmpty())
        while (true) {
            val m = findAllMatches(); if (m.isEmpty()) break
            for (match in m) for (pos in match)
                if (board[pos.row][pos.col].isPlayable)
                    board[pos.row][pos.col] = Tile(Random.nextInt(TILE_TYPES))
        }
    }

    fun isGameOver(): Boolean = isLevelMode && movesLeft <= 0 && score < targetScore
}
