package com.nailong.world.ui.community

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nailong.world.R
import com.nailong.world.ui.components.AnimatedBadge
import com.nailong.world.ui.components.ModernHeroHeader
import com.nailong.world.ui.components.ModernPageBackground
import com.nailong.world.ui.components.PressableScaleCard
import com.nailong.world.ui.theme.NailongGlow
import com.nailong.world.ui.theme.NailongPrimary
import com.nailong.world.ui.theme.NailongSecondary
import kotlinx.coroutines.delay

@Composable
fun CommunityScreen(modifier: Modifier = Modifier) {
    val mockPosts = listOf(
        CommunityPost("奶龍小編", "官方", "今天奶龍去了故宮，猜猜他看到了什麼？🐉🏯", "128", "15"),
        CommunityPost("龍龍愛好者", "高分挑戰", "剛剛在奶龍消消樂破了最高分！有誰要挑戰嗎？🎮", "89", "23"),
        CommunityPost("畫畫的龍", "創作", "我畫了一幅奶龍版的《星空》，大家覺得像嗎？🎨", "256", "42"),
        CommunityPost("奶龍音樂迷", "音樂", "奶龍音樂盒新曲目上線了，超可愛的旋律～🎵", "67", "9"),
    )

    ModernPageBackground(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            item {
                ModernHeroHeader(
                    eyebrow = "COMMUNITY",
                    title = "奶龍社區",
                    subtitle = "分享攻略、創作與每一個可愛瞬間",
                    emoji = "💬",
                    imageRes = R.drawable.nailong_hug,
                )
            }
            itemsIndexed(mockPosts) { index, post ->
                var visible by remember { mutableStateOf(false) }
                LaunchedEffect(Unit) {
                    delay(70L * index)
                    visible = true
                }
                AnimatedVisibility(
                    visible = visible,
                    enter = fadeIn(tween(360)) + slideInVertically(tween(360)) { it / 5 },
                ) {
                    CommunityPostCard(
                        post = post,
                        modifier = Modifier.padding(horizontal = 16.dp),
                    )
                }
            }
            item { Spacer(modifier = Modifier.height(96.dp)) }
        }
    }
}

private data class CommunityPost(
    val author: String,
    val tag: String,
    val content: String,
    val likes: String,
    val comments: String,
)

@Composable
private fun CommunityPostCard(post: CommunityPost, modifier: Modifier = Modifier) {
    PressableScaleCard(onClick = { }, modifier = modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(Brush.linearGradient(listOf(NailongPrimary, NailongSecondary, NailongGlow))),
                    contentAlignment = Alignment.Center,
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.nailong_simple),
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(0.9f),
                        contentScale = ContentScale.Fit,
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = post.author,
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Bold,
                    )
                    Text(
                        text = "剛剛 · 奶龍世界",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                AnimatedBadge(post.tag)
            }

            Spacer(modifier = Modifier.height(14.dp))
            Text(
                text = post.content,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Spacer(modifier = Modifier.height(14.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                StatPill("❤️", post.likes)
                StatPill("💬", post.comments)
                StatPill("↗", "分享")
            }
        }
    }
}

@Composable
private fun StatPill(icon: String, value: String) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(999.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.65f))
            .padding(horizontal = 11.dp, vertical = 7.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(icon, fontSize = 12.sp)
        Spacer(modifier = Modifier.width(4.dp))
        Text(value, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}
