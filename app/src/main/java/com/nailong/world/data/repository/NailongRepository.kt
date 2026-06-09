package com.nailong.world.data.repository

import com.nailong.world.data.model.CheckInState
import com.nailong.world.data.model.ContentItem
import com.nailong.world.data.model.GameItem
import com.nailong.world.data.model.LiveStreamInfo
import com.nailong.world.data.model.UserProfile
import kotlinx.coroutines.delay

/**
 * Repository providing mock data for the prototype.
 * In production, this would connect to a backend API via Retrofit.
 */
class NailongRepository {

    /** Simulated network delay */
    private suspend fun simulateDelay() {
        delay(200L)
    }

    suspend fun getLiveStreamInfo(): LiveStreamInfo {
        simulateDelay()
        return LiveStreamInfo(
            isLive = true,
            title = "奶龍直播間",
            viewerCount = 1287,
        )
    }

    suspend fun getHotGames(): List<GameItem> {
        simulateDelay()
        return listOf(
            GameItem(
                id = "game_1",
                title = "奶龍消消樂",
                description = "三消連線即消除，特殊方塊帶來連鎖爆擊！",
                category = "hot",
            ),
            GameItem(
                id = "game_2",
                title = "奶龍跑酷",
                description = "控制奶龍跳躍翻滾，收集金幣穿越障礙！",
                category = "hot",
            ),
        )
    }

    suspend fun getRecommendedContent(): List<ContentItem> {
        simulateDelay()
        return listOf(
            ContentItem(
                id = "content_1",
                title = "奶龍藝術館",
                description = "奶龍穿越世界名畫，你能認出哪幅原作嗎？16 幅名畫等你探索。",
                category = "gallery",
                badgeText = "推薦",
            ),
            ContentItem(
                id = "content_2",
                title = "奶龍音樂盒",
                description = "收錄經典奶龍主題曲，隨時隨地聆聽可愛旋律。",
                category = "music",
                badgeText = "新上架",
            ),
            ContentItem(
                id = "content_3",
                title = "奶龍表情包",
                description = "超過 50 款可愛奶龍表情，讓你的聊天更加生動有趣。",
                category = "sticker",
                badgeText = "熱門",
            ),
        )
    }

    suspend fun getAllGames(): List<GameItem> {
        simulateDelay()
        return listOf(
            GameItem(id = "g1", title = "奶龍消消樂", description = "三消連線消除", category = "hot"),
            GameItem(id = "g2", title = "奶龍跑酷", description = "跑酷收集", category = "hot"),
            GameItem(id = "g3", title = "奶龍拼圖", description = "經典拼圖", category = "recommended"),
            GameItem(id = "g4", title = "奶龍跳一跳", description = "益智跳躍", category = "recommended"),
            GameItem(id = "g5", title = "奶龍釣魚", description = "休閒釣魚", category = "new"),
            GameItem(id = "g6", title = "奶龍廚房", description = "模擬烹飪", category = "new"),
        )
    }

    suspend fun getUserProfile(): UserProfile {
        simulateDelay()
        return UserProfile(nickname = "奶龍粉絲", level = 5, points = 2380)
    }

    suspend fun getCheckInState(): CheckInState {
        simulateDelay()
        return CheckInState(consecutiveDays = 3, hasCheckedInToday = false)
    }

    suspend fun performCheckIn(): CheckInState {
        simulateDelay()
        return CheckInState(consecutiveDays = 4, hasCheckedInToday = true)
    }
}
