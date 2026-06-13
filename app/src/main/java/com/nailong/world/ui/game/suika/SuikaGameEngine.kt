package com.nailong.world.ui.game.suika

import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.random.Random

// ── Level Definitions ──
data class DragonLevel(
    val level: Int,        // 1..6
    val name: String,
    val radius: Float,     // in game coordinates
    val score: Int,        // points when merged
    val spawnWeight: Int,  // higher = more common for spawning
)

val dragonLevels = listOf(
    DragonLevel(1, "小奶龍", 20f, 10, 50),
    DragonLevel(2, "成長奶龍", 30f, 30, 30),
    DragonLevel(3, "活力奶龍", 45f, 80, 20),
    DragonLevel(4, "強壯奶龍", 65f, 200, 0),
    DragonLevel(5, "巨型奶龍", 90f, 500, 0),
    DragonLevel(6, "傳說大奶龍", 120f, 1500, 0),
)

// ── Physics Constants ──
private const val GRAVITY = 0.35f
private const val WALL_RESTITUTION = 0.35f
private const val DRAGON_RESTITUTION = 0.2f
private const val FRICTION = 0.985f
private const val AIR_DRAG = 0.998f
private const val MAX_FALL_SPEED = 14f
private const val POSITION_CORRECTION_PERCENT = 0.72f
private const val POSITION_SLOP = 0.5f
private const val COLLISION_SOLVER_ITERATIONS = 3

// ── Game Entities ──
data class SuikaDragon(
    val id: Int,
    val level: Int,        // 1..6
    var x: Float,
    var y: Float,
    var vx: Float = 0f,
    var vy: Float = 0f,
    val radius: Float,     // from level definition
    val score: Int,         // from level definition
    var isActive: Boolean = true,
    var mergeCooldown: Int = 0,  // frames until can merge again
)

data class Particle(
    var x: Float,
    var y: Float,
    var vx: Float,
    var vy: Float,
    var alpha: Float = 1f,
    var radius: Float,
)

// ── Game State ──
data class SuikaGameState(
    val dragons: List<SuikaDragon> = emptyList(),
    val currentDragon: SuikaDragon? = null,
    val nextDragon: SuikaDragon? = null,
    val dropX: Float = 0f,
    val score: Int = 0,
    val highScore: Int = 0,
    val isGameOver: Boolean = false,
    val isAnimating: Boolean = false,
    val particles: List<Particle> = emptyList(),
    val mergeEffects: List<MergeEffect> = emptyList(),
    val warningFlash: Boolean = false,
    val lastMergeText: String? = null,
)

data class MergeEffect(
    val x: Float,
    val y: Float,
    val level: Int,
    var lifetime: Int = 30,
)

// ── Container dimensions (game coordinates) ──
object SuikaContainer {
    const val WIDTH = 360f       // game coordinate width
    const val HEIGHT = 640f      // game coordinate height
    const val WALL_THICKNESS = 8f
    val LEFT_WALL = WALL_THICKNESS
    val RIGHT_WALL = WIDTH - WALL_THICKNESS
    val TOP_WALL = 0f
    val BOTTOM_WALL = HEIGHT
    val DANGER_LINE = HEIGHT * 0.15f  // 15% from top
    const val DANGER_DELAY_FRAMES = 120  // 2 seconds at 60fps
}

class SuikaGameEngine {

    var dragons = mutableListOf<SuikaDragon>()
    var score = 0
        private set
    var highScore = 0
    var isGameOver = false
        private set
    var currentDragon: SuikaDragon? = null
    var nextDragon: SuikaDragon? = null
    var dropX = SuikaContainer.WIDTH / 2f
    var particles = mutableListOf<Particle>()
    var mergeEffects = mutableListOf<MergeEffect>()
    var warningFlash = false
    var lastMergeText: String? = null
    private var nextId = 1
    private var dangerTimer = 0
    private var dragonInDanger = false

    fun initGame() {
        dragons.clear()
        score = 0
        isGameOver = false
        particles.clear()
        mergeEffects.clear()
        nextId = 1
        dangerTimer = 0
        dragonInDanger = false
        warningFlash = false
        lastMergeText = null
        spawnNext()
        spawnNext()
        dropX = SuikaContainer.WIDTH / 2f
    }

    fun loadHighScore(hs: Int) { highScore = hs }

    /** Spawn a random dragon (level 1-3) for the NEXT slot */
    fun spawnNext() {
        val totalWeight = dragonLevels.take(3).sumOf { it.spawnWeight }
        var r = Random.nextInt(totalWeight)
        var chosenLevel = 1
        for (dl in dragonLevels.take(3)) {
            r -= dl.spawnWeight
            if (r < 0) {
                chosenLevel = dl.level
                break
            }
        }
        val def = dragonLevels[chosenLevel - 1]
        val dragon = SuikaDragon(
            id = nextId++,
            level = chosenLevel,
            x = dropX,
            y = 0f,
            radius = def.radius,
            score = def.score,
        )
        if (currentDragon == null) {
            currentDragon = dragon
        } else if (nextDragon == null) {
            nextDragon = dragon
        } else {
            // Queue: promote next to current, new becomes next
            currentDragon = nextDragon
            nextDragon = dragon
        }
    }

    /** Drop the current dragon */
    fun dropDragon() {
        val dragon = currentDragon ?: return
        if (!dragon.isActive) return
        // Position at dropX near the top
        dragon.x = dropX.coerceIn(
            SuikaContainer.LEFT_WALL + dragon.radius,
            SuikaContainer.RIGHT_WALL - dragon.radius,
        )
        dragon.y = SuikaContainer.DANGER_LINE + dragon.radius + 20f
        dragon.vy = 2f  // initial downward velocity
        dragon.vx = 0f
        dragons.add(dragon)
        currentDragon = null
        spawnNext()
        // Reset danger timer when a new dragon drops
        dangerTimer = 0
        dragonInDanger = false
    }

    /** Update physics for one frame */
    fun update() {
        if (isGameOver) return

        // Integrate movement for each active dragon.
        for (d in dragons) {
            if (!d.isActive) continue
            if (d.mergeCooldown > 0) d.mergeCooldown--

            d.vy = (d.vy + GRAVITY).coerceAtMost(MAX_FALL_SPEED)
            d.vx *= FRICTION
            d.vy *= AIR_DRAG

            d.x += d.vx
            d.y += d.vy

            keepInsideContainer(d)
        }

        // Multiple solver passes prevent large dragons from tunnelling / sticking
        // when several bodies overlap in the same frame.
        repeat(COLLISION_SOLVER_ITERATIONS) {
            var mergedThisPass = false

            loop@ for (i in dragons.indices) {
                for (j in i + 1 until dragons.size) {
                    val a = dragons[i]
                    val b = dragons[j]
                    if (!a.isActive || !b.isActive) continue

                    if (resolveDragonCollision(a, b)) {
                        if (a.level == b.level && a.mergeCooldown <= 0 && b.mergeCooldown <= 0) {
                            if (a.level < dragonLevels.size) {
                                mergeDragons(i, j)
                            } else {
                                bigBang(i, j)
                            }
                            mergedThisPass = true
                            break@loop
                        }
                    }
                }
            }

            dragons.removeAll { !it.isActive }
            dragons.forEach { if (it.isActive) keepInsideContainer(it) }
            if (mergedThisPass) return@repeat
        }

        // Remove inactive dragons
        dragons.removeAll { !it.isActive }

        // Update particles
        val iter = particles.iterator()
        while (iter.hasNext()) {
            val p = iter.next()
            p.x += p.vx
            p.y += p.vy
            p.vy += 0.1f
            p.alpha -= 0.03f
            if (p.alpha <= 0f) iter.remove()
        }

        // Update merge effects
        val mIter = mergeEffects.iterator()
        while (mIter.hasNext()) {
            val me = mIter.next()
            me.lifetime--
            if (me.lifetime <= 0) mIter.remove()
        }

        // Danger zone check
        checkDangerZone()
    }

    private fun keepInsideContainer(d: SuikaDragon) {
        if (d.x - d.radius < SuikaContainer.LEFT_WALL) {
            d.x = SuikaContainer.LEFT_WALL + d.radius
            if (d.vx < 0f) d.vx = -d.vx * WALL_RESTITUTION
        }
        if (d.x + d.radius > SuikaContainer.RIGHT_WALL) {
            d.x = SuikaContainer.RIGHT_WALL - d.radius
            if (d.vx > 0f) d.vx = -d.vx * WALL_RESTITUTION
        }
        if (d.y + d.radius > SuikaContainer.BOTTOM_WALL) {
            d.y = SuikaContainer.BOTTOM_WALL - d.radius
            if (d.vy > 0f) d.vy = -d.vy * WALL_RESTITUTION
            if (abs(d.vy) < 0.25f) d.vy = 0f
            if (abs(d.vx) < 0.03f) d.vx = 0f
        }
        if (d.y - d.radius < SuikaContainer.TOP_WALL) {
            d.y = SuikaContainer.TOP_WALL + d.radius
            if (d.vy < 0f) d.vy = -d.vy * WALL_RESTITUTION
        }
    }

    /**
     * Resolve circle collision and return true if the two dragons are touching.
     * Handles exact-overlap cases deterministically so newly spawned / merged
     * dragons cannot pass through or become permanently stuck together.
     */
    private fun resolveDragonCollision(a: SuikaDragon, b: SuikaDragon): Boolean {
        var dx = b.x - a.x
        var dy = b.y - a.y
        var distSq = dx * dx + dy * dy
        val minDist = a.radius + b.radius
        val minDistSq = minDist * minDist

        if (distSq >= minDistSq) return false

        if (distSq < 0.0001f) {
            // Pick a stable pseudo-random normal from ids when centers overlap.
            val angle = ((a.id * 31 + b.id * 17) % 360) * Math.PI.toFloat() / 180f
            dx = cos(angle)
            dy = sin(angle)
            distSq = 1f
        }

        val dist = sqrt(distSq)
        val nx = dx / dist
        val ny = dy / dist
        val penetration = minDist - dist
        val correction = max(penetration - POSITION_SLOP, 0f) * POSITION_CORRECTION_PERCENT / 2f

        a.x -= nx * correction
        a.y -= ny * correction
        b.x += nx * correction
        b.y += ny * correction

        val relVx = b.vx - a.vx
        val relVy = b.vy - a.vy
        val relVn = relVx * nx + relVy * ny
        if (relVn < 0f) {
            val impulse = -(1f + DRAGON_RESTITUTION) * relVn / 2f
            a.vx -= impulse * nx
            a.vy -= impulse * ny
            b.vx += impulse * nx
            b.vy += impulse * ny
        }

        return true
    }

    private fun mergeDragons(i: Int, j: Int) {
        val a = dragons[i]
        val b = dragons[j]
        val newLevel = a.level + 1
        val def = dragonLevels[newLevel - 1]

        // Create merged dragon at midpoint
        val mx = (a.x + b.x) / 2f
        val my = (a.y + b.y) / 2f
        val merged = SuikaDragon(
            id = nextId++,
            level = newLevel,
            x = mx,
            y = my,
            radius = def.radius,
            score = def.score,
            mergeCooldown = 5,
        )

        a.isActive = false
        b.isActive = false
        dragons.add(merged)

        // Add score
        score += def.score
        if (score > highScore) highScore = score

        // Particles
        spawnMergeParticles(mx, my, def.radius)
        mergeEffects.add(MergeEffect(mx, my, newLevel))
        lastMergeText = "合成 ${def.name}！+${def.score}"
    }

    private fun bigBang(i: Int, j: Int) {
        val a = dragons[i]
        val b = dragons[j]
        val mx = (a.x + b.x) / 2f
        val my = (a.y + b.y) / 2f

        a.isActive = false
        b.isActive = false

        // Bonus score
        val bonus = 3000
        score += bonus
        if (score > highScore) highScore = score

        // Big explosion particles
        for (k in 0..50) {
            val angle = Random.nextFloat() * Math.PI.toFloat() * 2
            val speed = Random.nextFloat() * 8f + 2f
            particles.add(Particle(
                x = mx + Random.nextFloat() * 40f - 20f,
                y = my + Random.nextFloat() * 40f - 20f,
                vx = cos(angle) * speed,
                vy = sin(angle) * speed - 2f,
                radius = Random.nextFloat() * 6f + 2f,
            ))
        }
        mergeEffects.add(MergeEffect(mx, my, 7)) // special big bang effect
        lastMergeText = "💥 大爆炸！+3000"
    }

    private fun spawnMergeParticles(x: Float, y: Float, radius: Float) {
        for (k in 0..15) {
            val angle = Random.nextFloat() * Math.PI.toFloat() * 2
            val speed = Random.nextFloat() * 4f + 1f
            particles.add(Particle(
                x = x, y = y,
                vx = cos(angle) * speed,
                vy = sin(angle) * speed - 2f,
                radius = Random.nextFloat() * 3f + 1f,
            ))
        }
    }

    private fun checkDangerZone() {
        val dangerLine = SuikaContainer.DANGER_LINE
        var anyAbove = false
        for (d in dragons) {
            if (d.isActive && d.y - d.radius < dangerLine) {
                anyAbove = true
                // Check if velocity is very low (nearly stopped)
                if (abs(d.vy) < 0.5f && abs(d.vx) < 0.5f) {
                    if (!dragonInDanger) {
                        dragonInDanger = true
                        dangerTimer = 0
                    }
                }
            }
        }

        if (!anyAbove) {
            dragonInDanger = false
            dangerTimer = 0
            warningFlash = false
            return
        }

        if (dragonInDanger) {
            dangerTimer++
            warningFlash = dangerTimer % 20 < 10  // flash at 3Hz
            if (dangerTimer >= SuikaContainer.DANGER_DELAY_FRAMES) {
                isGameOver = true
            }
        }
    }

    /** Check if drop position is valid (not blocked by existing dragons) */
    fun isValidDrop(x: Float, radius: Float): Boolean {
        val dropY = SuikaContainer.DANGER_LINE + radius + 20f
        for (d in dragons) {
            if (!d.isActive) continue
            val dx = d.x - x
            val dy = d.y - dropY
            if (dx * dx + dy * dy < (d.radius + radius + 5f) * (d.radius + radius + 5f)) return false
        }
        return true
    }
}
