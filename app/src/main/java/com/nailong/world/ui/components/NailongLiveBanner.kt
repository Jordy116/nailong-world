package com.nailong.world.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nailong.world.R

/**
 * 奶龍直播入口長方形 Banner 組件
 *
 * 設計規格：
 * - 黃橙漸層滿版背景
 * - 左側奶龍擁抱圖片（邊緣漸層融合）
 * - 右側文字區 + 右下角 CTA 按鈕
 * - 右上角呼吸燈「直播中」標籤
 */
@Composable
fun NailongLiveBanner(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        onClick = onClick,
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(140.dp)
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            Color(0xFFFF8C42),  // 橙
                            Color(0xFFFFB300),  // 金黃
                            Color(0xFFFFC107),  // 黃
                        ),
                    ),
                    shape = RoundedCornerShape(20.dp),
                ),
        ) {
            // ── 左側：奶龍圖片（邊緣融合） ──
            Box(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .fillMaxWidth(0.45f)
                    .height(140.dp)
                    .clip(RoundedCornerShape(topStart = 20.dp, bottomStart = 20.dp)),
                contentAlignment = Alignment.CenterStart,
            ) {
                // 底圖
                androidx.compose.foundation.Image(
                    painter = painterResource(id = R.drawable.live_banner_image),
                    contentDescription = "奶龍直播",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp),
                    contentScale = ContentScale.Crop,
                )

                // 右側漸層遮罩 — 讓圖片邊緣與背景無縫融合
                Box(
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .width(40.dp)
                        .height(140.dp)
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    Color(0xFFFFC107),
                                ),
                            ),
                        ),
                )
            }

            // ── 右側文字區 ──
            Column(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .fillMaxWidth()
                    .padding(start = 20.dp, end = 12.dp, top = 12.dp, bottom = 12.dp),
            ) {
                // 主標題
                Text(
                    text = "奶龍直播",
                    color = Color(0xFF2D1B00),
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                )

                Spacer(modifier = Modifier.height(4.dp))

                // 副標題
                Text(
                    text = "立即加入，與奶龍互動",
                    color = Color(0xFF5C3A00),
                    fontSize = 12.sp,
                )

                Spacer(modifier = Modifier.weight(1f))

                // ── 右下角 CTA 按鈕 ──
                Row(
                    modifier = Modifier
                        .align(Alignment.End)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFF2D1B00))
                        .padding(horizontal = 14.dp, vertical = 7.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "加入入口",
                        color = Color(0xFFFFC107),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "→",
                        color = Color(0xFFFFC107),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }

            // ── 右上角「直播中」標籤 ──
            LiveIndicator(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 8.dp, end = 8.dp),
            )
        }
    }
}

/**
 * 呼吸燈「直播中」標籤
 */
@Composable
private fun LiveIndicator(modifier: Modifier = Modifier) {
    // 呼吸燈動畫：alpha 0.3 → 1.0 → 0.3 無限循環
    val infiniteTransition = rememberInfiniteTransition(label = "liveBlink")
    val blinkAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 800, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "blinkAlpha",
    )

    Row(
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .background(Color.Black.copy(alpha = 0.5f))
            .padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // 呼吸小紅點
        Box(
            modifier = Modifier
                .size(7.dp)
                .alpha(blinkAlpha)
                .clip(CircleShape)
                .background(Color(0xFFFF3344)),
        )
        Spacer(modifier = Modifier.width(5.dp))
        Text(
            text = "LIVE",
            color = Color.White,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
        )
    }
}
