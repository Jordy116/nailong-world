package com.nailong.world.ui.community

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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nailong.world.ui.theme.GradientEnd
import com.nailong.world.ui.theme.GradientStart
import com.nailong.world.ui.theme.NailongOrange
import com.nailong.world.ui.theme.TextSecondary

/**
 * Community screen — placeholder showing a feed of mock posts.
 * In production this would connect to a backend social feed API.
 */
@Composable
fun CommunityScreen(modifier: Modifier = Modifier) {
    val mockPosts = listOf(
        CommunityPost("奶龍小編", "今天奶龍去了故宮，猜猜他看到了什麼？🐉🏯", "128", "15"),
        CommunityPost("龍龍愛好者", "剛剛在奶龍消消樂破了最高分！有誰要挑戰嗎？🎮", "89", "23"),
        CommunityPost("畫畫的龍", "我畫了一幅奶龍版的《星空》，大家覺得像嗎？🎨", "256", "42"),
        CommunityPost("奶龍音樂迷", "奶龍音樂盒新曲目上線了，超可愛的旋律～🎵", "67", "9"),
    )

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(top = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item {
            Text(
                text = "🏘️ 奶龍社區",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(horizontal = 16.dp),
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "和龍友們一起分享快樂！",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary,
                modifier = Modifier.padding(horizontal = 16.dp),
            )
        }
        items(mockPosts) { post ->
            CommunityPostCard(post)
        }
        item { Spacer(modifier = Modifier.height(80.dp)) }
    }
}

private data class CommunityPost(
    val author: String,
    val content: String,
    val likes: String,
    val comments: String,
)

@Composable
private fun CommunityPostCard(post: CommunityPost) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.horizontalGradient(listOf(GradientStart, GradientEnd)),
                        ),
                    contentAlignment = Alignment.Center,
                ) {
                    Text("🐉", fontSize = 20.sp)
                }
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = post.author,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = post.content,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(24.dp),
            ) {
                Text(
                    text = "❤️ ${post.likes}",
                    style = MaterialTheme.typography.labelMedium,
                    color = TextSecondary,
                )
                Text(
                    text = "💬 ${post.comments}",
                    style = MaterialTheme.typography.labelMedium,
                    color = TextSecondary,
                )
            }
        }
    }
}
