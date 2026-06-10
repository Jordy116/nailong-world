package com.nailong.world.data

import android.content.Context
import android.content.SharedPreferences

/**
 * Persistent local storage for game progress and user data.
 * Uses SharedPreferences as a lightweight alternative to DataStore/Room.
 * Data is read/written synchronously for simplicity; call from viewModelScope.
 *
 * Keys stored:
 * - level_{id}_highscore: Int — high score per level (1..7)
 * - level_{id}_unlocked: Boolean — whether level is unlocked
 * - infinite_highscore: Int — infinite mode high score
 * - nickname: String — player's display name
 * - highest_level_unlocked: Int — highest level number reached
 */
class GameDataStore(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("nailong_game_data", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_INFINITE_HIGH_SCORE = "infinite_highscore"
        private const val KEY_NICKNAME = "nickname"
        private const val KEY_HIGHEST_LEVEL = "highest_level_unlocked"

        private fun levelHighScoreKey(id: Int) = "level_${id}_highscore"
        private fun levelUnlockedKey(id: Int) = "level_${id}_unlocked"

        // Default unlocked levels (1-4)
        private val DEFAULT_UNLOCKED = setOf(1, 2, 3, 4)
    }

    // ── Infinite Mode ──
    fun getInfiniteHighScore(): Int = prefs.getInt(KEY_INFINITE_HIGH_SCORE, 0)

    fun saveInfiniteHighScore(score: Int) {
        val current = getInfiniteHighScore()
        if (score > current) {
            prefs.edit().putInt(KEY_INFINITE_HIGH_SCORE, score).apply()
        }
    }

    // ── Level High Scores ──
    fun getLevelHighScore(levelId: Int): Int =
        prefs.getInt(levelHighScoreKey(levelId), 0)

    fun saveLevelHighScore(levelId: Int, score: Int) {
        val current = getLevelHighScore(levelId)
        if (score > current) {
            prefs.edit().putInt(levelHighScoreKey(levelId), score).apply()
        }
    }

    // ── Level Unlock ──
    fun isLevelUnlocked(levelId: Int): Boolean {
        if (levelId in DEFAULT_UNLOCKED) return true
        return prefs.getBoolean(levelUnlockedKey(levelId), false)
    }

    fun unlockLevel(levelId: Int) {
        prefs.edit().putBoolean(levelUnlockedKey(levelId), true).apply()
        // Update highest level
        val current = getHighestLevelUnlocked()
        if (levelId > current) {
            prefs.edit().putInt(KEY_HIGHEST_LEVEL, levelId).apply()
        }
    }

    fun getHighestLevelUnlocked(): Int =
        prefs.getInt(KEY_HIGHEST_LEVEL, 4)

    // ── Suika Game Best Record ──
    fun getSuikaHighScore(): Int = prefs.getInt("suika_high_score", 0)
    fun saveSuikaHighScore(score: Int) {
        val current = getSuikaHighScore()
        if (score > current) prefs.edit().putInt("suika_high_score", score).apply()
    }

    // ── Memory Game Best Records ──
    fun getMemoryBestTime(): Int = prefs.getInt("memory_best_time", 999)
    fun getMemoryBestMoves(): Int = prefs.getInt("memory_best_moves", 999)

    fun saveMemoryGame(moves: Int, seconds: Int) {
        with(prefs.edit()) {
            val currTime = getMemoryBestTime()
            val currMoves = getMemoryBestMoves()
            if (seconds < currTime) putInt("memory_best_time", seconds)
            if (moves < currMoves) putInt("memory_best_moves", moves)
            apply()
        }
    }

    // ── Nickname ──
    fun getNickname(): String = prefs.getString(KEY_NICKNAME, "奶龍守護者_9527") ?: "奶龍守護者_9527"

    fun saveNickname(name: String) {
        prefs.edit().putString(KEY_NICKNAME, name).apply()
    }

    // ── Derived: Title based on game achievements ──
    fun getPlayerTitle(): String {
        val infiniteScore = getInfiniteHighScore()
        val highestLevel = getHighestLevelUnlocked()
        return when {
            highestLevel >= 7 -> "🏆 傳說奶龍大師"
            highestLevel >= 6 -> "🔥 奶龍 Master"
            highestLevel >= 5 -> "💪 消消樂高手"
            infiniteScore >= 5000 -> "🌟 無限挑戰者"
            infiniteScore >= 2000 -> "✨ 消消樂新星"
            highestLevel >= 3 -> "🎯 消消樂 rookie"
            else -> "🐣 奶龍新手"
        }
    }

    // ── Game Stats for Profile ──
    data class PlayerStats(
        val nickname: String,
        val title: String,
        val infiniteHighScore: Int,
        val highestLevelUnlocked: Int,
        val totalLevels: Int = 7,
        val expCurrent: Int = 0,    // current exp
        val expMax: Int = 100,      // exp needed for next level
        val playerLevel: Int = 1,   // overall player level
    )

    fun getPlayerStats(): PlayerStats {
        val infinite = getInfiniteHighScore()
        val highestLevel = getHighestLevelUnlocked()

        // Calculate player level based on achievements
        val achievements = calculateAchievementPoints()
        val level = (achievements / 100) + 1
        val expInLevel = achievements % 100

        return PlayerStats(
            nickname = getNickname(),
            title = getPlayerTitle(),
            infiniteHighScore = infinite,
            highestLevelUnlocked = highestLevel,
            totalLevels = 7,
            expCurrent = expInLevel,
            expMax = 100,
            playerLevel = level,
        )
    }

    private fun calculateAchievementPoints(): Int {
        var points = 0
        // Points from level high scores
        for (id in 1..7) {
            val score = getLevelHighScore(id)
            if (score > 0) points += 50  // cleared
            if (score >= 1000) points += 30  // high score bonus
        }
        // Points from infinite mode
        val infinite = getInfiniteHighScore()
        points += infinite / 100
        // Points from unlocked levels
        points += (getHighestLevelUnlocked() - 1) * 20
        return points
    }

    // ── Reset ──
    fun resetAll() {
        prefs.edit().clear().apply()
    }

    // ── Level Progress Helper ──
    fun getLevelProgress(): List<LevelProgressInfo> {
        return (1..7).map { id ->
            LevelProgressInfo(
                levelId = id,
                name = when (id) {
                    1 -> "新手上路"
                    2 -> "初試身手"
                    3 -> "漸入佳境"
                    4 -> "挑戰升級"
                    5 -> "高手對決"
                    6 -> "奶龍大師"
                    7 -> "傳說級別"
                    else -> "未知"
                },
                highScore = getLevelHighScore(id),
                isUnlocked = isLevelUnlocked(id),
                isCleared = getLevelHighScore(id) > 0,
            )
        }
    }

    data class LevelProgressInfo(
        val levelId: Int,
        val name: String,
        val highScore: Int,
        val isUnlocked: Boolean,
        val isCleared: Boolean,
    )
}
