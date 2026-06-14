package com.nailong.world.ui.components

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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

@Composable
fun NailongMatch3Banner(
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
                .height(164.dp)
                .background(
                    Brush.linearGradient(
                        listOf(
                            Color(0xFF3E1B85),
                            Color(0xFF7B35D8),
                            Color(0xFFFF6D42),
                            Color(0xFFFFC82E),
                        ),
                    ),
                    RoundedCornerShape(28.dp),
                ),
        ) {
            SoftBubble(Alignment.TopStart, 34.dp, (-18).dp, 88.dp, 0.08f)
            SoftBubble(Alignment.BottomEnd, 18.dp, 24.dp, 118.dp, 0.08f)
            FloatingTile(Alignment.TopEnd, (-96).dp, 18.dp, 24.dp, 14f, Color(0xFFFFF1A6), 0.22f)
            FloatingTile(Alignment.CenterEnd, (-120).dp, 6.dp, 20.dp, -12f, Color(0xFF6EE7B7), 0.18f)
            FloatingTile(Alignment.BottomStart, 116.dp, (-18).dp, 22.dp, 18f, Color(0xFFFFFFFF), 0.14f)

            // Left side readability veil.
            Box(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .fillMaxWidth(0.60f)
                    .fillMaxHeight()
                    .background(
                        Brush.horizontalGradient(
                            listOf(
                                Color.Black.copy(alpha = 0.18f),
                                Color.Black.copy(alpha = 0.06f),
                                Color.Transparent,
                            ),
                        ),
                    ),
            )

            Column(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .fillMaxWidth(0.57f)
                    .padding(start = 20.dp, top = 18.dp, bottom = 18.dp),
            ) {
                Text(
                    text = "奶龍消消樂",
                    color = Color.White,
                    fontSize = 24.sp,
                    lineHeight = 28.sp,
                    fontWeight = FontWeight.ExtraBold,
                )
                Spacer(modifier = Modifier.height(7.dp))
                Text(
                    text = "三消連線即消除，特殊方塊帶來連鎖爆擊！",
                    color = Color.White.copy(alpha = 0.82f),
                    fontSize = 12.sp,
                    lineHeight = 17.sp,
                    fontWeight = FontWeight.SemiBold,
                )
                Spacer(modifier = Modifier.weight(1f))
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(999.dp))
                        .background(Color(0xFFFFD12E))
                        .padding(horizontal = 17.dp, vertical = 9.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text("立即遊玩", color = Color(0xFF241600), fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.width(7.dp))
                    Text("→", color = Color(0xFF241600), fontSize = 15.sp, fontWeight = FontWeight.Bold)
                }
            }

            // Cleaner cutout with fewer white fringes than the previous thinking image.
            Image(
                painter = painterResource(id = R.drawable.nailong_cute_face),
                contentDescription = "奶龍消消樂角色",
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .offset(x = 18.dp, y = 14.dp)
                    .width(188.dp)
                    .height(150.dp),
                contentScale = ContentScale.Fit,
            )
        }
    }
}

@Composable
private fun BoxScope.SoftBubble(alignment: Alignment, x: androidx.compose.ui.unit.Dp, y: androidx.compose.ui.unit.Dp, size: androidx.compose.ui.unit.Dp, alpha: Float) {
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
private fun BoxScope.FloatingTile(
    alignment: Alignment,
    x: androidx.compose.ui.unit.Dp,
    y: androidx.compose.ui.unit.Dp,
    size: androidx.compose.ui.unit.Dp,
    rotation: Float,
    color: Color,
    alpha: Float,
) {
    Box(
        modifier = Modifier
            .align(alignment)
            .offset(x = x, y = y)
            .size(size)
            .rotate(rotation)
            .clip(RoundedCornerShape(7.dp))
            .background(color.copy(alpha = alpha)),
    )
}
