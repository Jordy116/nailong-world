package com.nailong.world.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nailong.world.R

/**
 * 奶龍消消樂 Full-width 大橫幅入口 Banner
 *
 * 設計語言與 NailongLiveBanner 家族化一致：
 * - 滿版長方形大卡片，16dp 圓角 + 陰影
 * - 深紫到橙黃潮流漸變背景
 * - 左側：去背奶龍角色 + 半透明漂浮方塊
 * - 右側：主標題 + 描述 + CTA 按鈕
 */
@Composable
fun NailongMatch3Banner(
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
                .height(160.dp)
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            Color(0xFF2D1B69),  // 深紫
                            Color(0xFF6B2FA0),  // 紫
                            Color(0xFFFF6B35),  // 橙
                            Color(0xFFFFC107),  // 黃
                        ),
                    ),
                    shape = RoundedCornerShape(20.dp),
                ),
        ) {
            // ── 背景微光粒子 ──
            Box(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .offset(x = 40.dp, y = (-10).dp)
                    .size(60.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.06f)),
            )
            Box(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .offset(x = 80.dp, y = 10.dp)
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.05f)),
            )
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset(x = (-40).dp, y = 20.dp)
                    .size(50.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.07f)),
            )

            // ── 迷你三消棋盤底紋（由半透明方塊構成） ──
            // 漂浮方塊 1 — 左上
            Box(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .offset(x = 100.dp, y = 10.dp)
                    .size(28.dp)
                    .rotate(15f)
                    .alpha(0.25f)
                    .clip(RoundedCornerShape(4.dp))
                    .background(Color(0xFF4CAF50).copy(alpha = 0.4f)),
            )
            // 漂浮方塊 2 — 中上
            Box(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .offset(x = 130.dp, y = 40.dp)
                    .size(22.dp)
                    .rotate(-10f)
                    .alpha(0.2f)
                    .clip(RoundedCornerShape(4.dp))
                    .background(Color(0xFFE53935).copy(alpha = 0.35f)),
            )
            // 漂浮方塊 3 — 左中
            Box(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .offset(x = 110.dp)
                    .size(24.dp)
                    .rotate(25f)
                    .alpha(0.2f)
                    .clip(RoundedCornerShape(4.dp))
                    .background(Color(0xFFFFC107).copy(alpha = 0.3f)),
            )
            // 漂浮方塊 4 — 左下
            Box(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .offset(x = 120.dp, y = (-10).dp)
                    .size(20.dp)
                    .rotate(-20f)
                    .alpha(0.2f)
                    .clip(RoundedCornerShape(4.dp))
                    .background(Color(0xFF9C27B0).copy(alpha = 0.3f)),
            )

            // ── 左側：去背奶龍角色 ──
            androidx.compose.foundation.Image(
                painter = painterResource(id = R.drawable.match3_banner_char),
                contentDescription = "奶龍消消樂角色",
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .offset(x = (-5).dp, y = 15.dp)
                    .width(140.dp)
                    .height(170.dp),
                contentScale = ContentScale.Fit,
            )

            // ── 右側文字區 ──
            Column(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .fillMaxWidth(0.55f)
                    .padding(end = 16.dp, top = 16.dp, bottom = 12.dp),
            ) {
                // 主標題
                Text(
                    text = "奶龍消消樂",
                    color = Color.White,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                )

                Spacer(modifier = Modifier.height(6.dp))

                // 遊戲簡介
                Text(
                    text = "三消連線即消除，特殊方塊帶來連鎖爆擊！",
                    color = Color.White.copy(alpha = 0.7f),
                    fontSize = 12.sp,
                    lineHeight = 17.sp,
                )

                Spacer(modifier = Modifier.weight(1f))

                // CTA 按鈕
                Row(
                    modifier = Modifier
                        .align(Alignment.End)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFFFFC107))
                        .padding(horizontal = 16.dp, vertical = 9.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "立即遊玩",
                        color = Color(0xFF1C1C1E),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "→",
                        color = Color(0xFF1C1C1E),
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }
        }
    }
}
