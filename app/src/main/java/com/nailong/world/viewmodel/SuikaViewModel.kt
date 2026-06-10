package com.nailong.world.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nailong.world.ui.game.suika.SuikaGameEngine
import com.nailong.world.ui.game.suika.SuikaDragon
import com.nailong.world.ui.game.suika.dragonLevels
import com.nailong.world.ui.game.match3.model.LevelProgress
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

data class SuikaUiState(
    val dragons: List<SuikaDragon> = emptyList(),
    val currentDragon: SuikaDragon? = null,
    val nextDragon: SuikaDragon? = null,
    val dropX: Float = 0f,
    val score: Int = 0,
    val highScore: Int = 0,
    val isGameOver: Boolean = false,
    val particles: List<SuikaGameParticle> = emptyList(),
    val mergeEffects: List<SuikaMergeEffect> = emptyList(),
    val warningFlash: Boolean = false,
    val lastMergeText: String? = null,
)

data class SuikaGameParticle(
    val x: Float, val y: Float, val alpha: Float, val radius: Float,
)

data class SuikaMergeEffect(
    val x: Float, val y: Float, val level: Int, val lifetime: Int,
)

class SuikaViewModel : ViewModel() {

    private val engine = SuikaGameEngine()

    var state by mutableStateOf(SuikaUiState())
        private set

    init {
        startGame()
    }

    fun startGame() {
        engine.loadHighScore(LevelProgress.getHighScore(-2))  // -2 = suika game
        engine.initGame()
        updateState()
        // Start game loop
        viewModelScope.launch {
            while (isActive && !engine.isGameOver) {
                engine.update()
                updateState()
                delay(20)  // ~50fps — smoother visual perf
            }
        }
    }

    fun setDropX(x: Float) {
        if (engine.isGameOver) return
        engine.dropX = x.coerceIn(
            SuikaDropConstants.LEFT_LIMIT,
            SuikaDropConstants.RIGHT_LIMIT,
        )
        // Update current dragon preview position
        engine.currentDragon?.let {
            it.x = engine.dropX
        }
        updateState()
    }

    fun dropDragon() {
        if (engine.isGameOver) return
        val dragon = engine.currentDragon ?: return
        if (engine.isValidDrop(engine.dropX, dragon.radius)) {
            engine.dropDragon()
            updateState()
        }
    }

    private fun updateState() {
        state = SuikaUiState(
            dragons = engine.dragons.toList(),
            currentDragon = engine.currentDragon?.copy(),
            nextDragon = engine.nextDragon?.copy(),
            dropX = engine.dropX,
            score = engine.score,
            highScore = engine.highScore,
            isGameOver = engine.isGameOver,
            particles = engine.particles.map {
                SuikaGameParticle(it.x, it.y, it.alpha, it.radius)
            },
            mergeEffects = engine.mergeEffects.map {
                SuikaMergeEffect(it.x, it.y, it.level, it.lifetime)
            },
            warningFlash = engine.warningFlash,
            lastMergeText = engine.lastMergeText,
        )
    }

    /** Save score when leaving */
    fun saveScore() {
        LevelProgress.saveHighScore(-2, engine.score)  // -2 = suika
    }
}

object SuikaDropConstants {
    const val LEFT_LIMIT = 40f
    const val RIGHT_LIMIT = 320f
}
