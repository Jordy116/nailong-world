package com.nailong.world.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
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

@Composable
fun NailongLiveBanner(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(28.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        onClick = onClick,
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(154.dp)
                .background(
                    Brush.linearGradient(
                        listOf(
                            Color(0xFFFF8A3D),
                            Color(0xFFFFB21A),
                            Color(0xFFFFCA2E),
                        ),
                    ),
                    RoundedCornerShape(28.dp),
                ),
        ) {
            GlowBubble(Alignment.TopStart, (-22).dp, (-30).dp, 112.dp, 0.12f)
            GlowBubble(Alignment.BottomStart, 38.dp, 22.dp, 82.dp, 0.07f)
            GlowBubble(Alignment.CenterEnd, 24.dp, 8.dp, 130.dp, 0.08f)

            // Text readability veil. Keeps the illustration from visually fighting the title.
            Box(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .fillMaxWidth(0.58f)
                    .fillMaxHeight()
                    .background(
                        Brush.horizontalGradient(
                            listOf(
                                Color.White.copy(alpha = 0.24f),
                                Color.White.copy(alpha = 0.10f),
                                Color.Transparent,
                            ),
                        ),
                    ),
            )

            Column(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .fillMaxWidth(0.54f)
                    .padding(start = 20.dp, top = 18.dp, bottom = 18.dp),
            ) {
                Text(
                    text = "奶龍直播",
                    color = Color(0xFF231404),
                    fontSize = 25.sp,
                    fontWeight = FontWeight.ExtraBold,
                    lineHeight = 28.sp,
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "立即加入，與奶龍互動",
                    color = Color(0xFF4C2D06),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    lineHeight = 19.sp,
                )
                Spacer(modifier = Modifier.weight(1f))
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(999.dp))
                        .background(Color(0xFF231404).copy(alpha = 0.92f))
                        .padding(horizontal = 17.dp, vertical = 9.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text("加入入口", color = Color(0xFFFFCA2E), fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.width(7.dp))
                    Text("→", color = Color(0xFFFFCA2E), fontSize = 15.sp, fontWeight = FontWeight.Bold)
                }
            }

            Image(
                painter = painterResource(id = R.drawable.nailong_hug),
                contentDescription = "奶龍直播角色",
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .offset(x = 18.dp, y = 8.dp)
                    .width(210.dp)
                    .height(150.dp),
                contentScale = ContentScale.Fit,
            )

            LiveIndicator(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 12.dp, end = 12.dp),
            )
        }
    }
}

@Composable
private fun BoxScope.GlowBubble(alignment: Alignment, x: androidx.compose.ui.unit.Dp, y: androidx.compose.ui.unit.Dp, size: androidx.compose.ui.unit.Dp, alpha: Float) {
    Box(
        modifier = Modifier
            .align(alignment)
            .offset(x = x, y = y)
            .size(size)
            .clip(CircleShape)
            .background(Color.White.copy(alpha = alpha)),
    )
}

@Composable
private fun LiveIndicator(modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition(label = "liveBlink")
    val blinkAlpha by infiniteTransition.animateFloat(
        initialValue = 0.35f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 760, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "blinkAlpha",
    )

    Row(
        modifier = modifier
            .clip(RoundedCornerShape(999.dp))
            .background(Color.Black.copy(alpha = 0.42f))
            .padding(horizontal = 10.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(7.dp)
                .alpha(blinkAlpha)
                .clip(CircleShape)
                .background(Color(0xFFFF3344)),
        )
        Spacer(modifier = Modifier.width(5.dp))
        Text("LIVE", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
    }
}
