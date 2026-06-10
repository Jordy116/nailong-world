package com.nailong.world.ui.game.match3.model

import com.nailong.world.data.GameDataStore

/**
 * Represents a game level with specific constraints.
 * Levels 1-4 are unlocked by default; 5-7 require the previous level to be cleared.
 */
data class LevelConfig(
    val id: Int,
    val name: String,
    val targetScore: Int,
    val moves: Int,
    val obstacles: Int = 0,
    val isLocked: Boolean = false,
)

/**
 * All level definitions for the game.
 */
val levels = listOf(
    LevelConfig(id = 1, name = "新手上路", targetScore = 200, moves = 20, obstacles = 0),
    LevelConfig(id = 2, name = "初試身手", targetScore = 400, moves = 20, obstacles = 3),
    LevelConfig(id = 3, name = "漸入佳境", targetScore = 700, moves = 22, obstacles = 5),
    LevelConfig(id = 4, name = "挑戰升級", targetScore = 1000, moves = 22, obstacles = 7),
    LevelConfig(id = 5, name = "高手對決", targetScore = 1500, moves = 25, obstacles = 10, isLocked = true),
    LevelConfig(id = 6, name = "奶龍大師", targetScore = 2000, moves = 25, obstacles = 12, isLocked = true),
    LevelConfig(id = 7, name = "傳說級別", targetScore = 3000, moves = 28, obstacles = 15, isLocked = true),
)

/**
 * Persistence layer backed by GameDataStore.
 * Must be initialized once with a GameDataStore instance before use.
 */
object LevelProgress {
    private var store: GameDataStore? = null

    fun init(dataStore: GameDataStore) {
        store = dataStore
    }

    private fun requireStore(): GameDataStore = store
        ?: throw IllegalStateException("LevelProgress.init() must be called with a GameDataStore instance")

    fun isUnlocked(levelId: Int): Boolean = requireStore().isLevelUnlocked(levelId)

    fun unlockNext(clearedLevelId: Int) {
        val nextId = clearedLevelId + 1
        if (nextId <= levels.size) {
            requireStore().unlockLevel(nextId)
        }
    }

    fun getHighScore(levelId: Int): Int = when (levelId) {
        -1 -> requireStore().getInfiniteHighScore()
        else -> requireStore().getLevelHighScore(levelId)
    }

    fun saveHighScore(levelId: Int, score: Int) {
        when (levelId) {
            -1 -> requireStore().saveInfiniteHighScore(score)
            else -> requireStore().saveLevelHighScore(levelId, score)
        }
    }

    fun resetAll() {
        requireStore().resetAll()
    }

    fun getStore(): GameDataStore = requireStore()
}

/**
 * Game mode selection.
 */
enum class GameMode {
    INFINITE,
    LEVEL,
}

/**
 * Parameters passed from the mode select screen to the actual game board.
 */
data class GameConfig(
    val mode: GameMode,
    val level: LevelConfig? = null,
) {
    val hasMoveLimit: Boolean get() = mode == GameMode.LEVEL
    val targetScore: Int get() = level?.targetScore ?: 0
    val maxMoves: Int get() = level?.moves ?: Int.MAX_VALUE
}
