package com.nailong.world.data.model

/**
 * Represents a game item shown on the Home and Game tabs.
 */
data class GameItem(
    val id: String,
    val title: String,
    val description: String,
    val iconUrl: String = "",
    val category: String = "recommended", // "hot", "recommended", "new"
    val isLive: Boolean = false,
)

/**
 * Represents a content piece in the gallery / shop section.
 */
data class ContentItem(
    val id: String,
    val title: String,
    val description: String,
    val thumbnailUrl: String = "",
    val category: String, // "gallery", "music", "sticker"
    val badgeText: String = "",
)

/**
 * Live-stream info for the quick-entry card on Home.
 */
data class LiveStreamInfo(
    val isLive: Boolean = true,
    val title: String = "奶龍直播間",
    val viewerCount: Int = 0,
    val streamUrl: String = "",
)

/**
 * User check-in state (shown on Home and Profile).
 */
data class CheckInState(
    val consecutiveDays: Int = 0,
    val hasCheckedInToday: Boolean = false,
    val dailyReward: String = "今日簽到領獎勵",
)

/**
 * User profile summary (used on "我的" tab).
 */
data class UserProfile(
    val nickname: String = "奶龍粉絲",
    val avatarUrl: String = "",
    val level: Int = 1,
    val points: Int = 0,
)
