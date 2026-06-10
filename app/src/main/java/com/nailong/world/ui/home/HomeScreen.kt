package com.nailong.world.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.nailong.world.ui.components.ContentCard
import com.nailong.world.ui.components.NailongLiveBanner
import com.nailong.world.ui.components.NailongMatch3Banner
import com.nailong.world.ui.components.SectionHeader
import com.nailong.world.ui.theme.NailongPrimary
import com.nailong.world.ui.theme.NailongSecondary
import com.nailong.world.viewmodel.HomeViewModel

@Composable
fun HomeScreen(
    onNavigateToGame: () -> Unit = {},
    onPlayMatch3: () -> Unit = {},
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = viewModel(),
) {
    val state by viewModel.uiState.collectAsState()

    Column(modifier = modifier.fillMaxSize()) {
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            item { GalleryHeader() }

            item {
                Spacer(modifier = Modifier.height(12.dp))
                NailongLiveBanner(onClick = { })
            }

            item {
                Spacer(modifier = Modifier.height(12.dp))
                NailongMatch3Banner(onClick = onPlayMatch3)
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
                SectionHeader(title = "推介")
            }

            items(state.recommendedContent) { content ->
                val emoji = when (content.category) {
                    "gallery" -> "🖼️"
                    "music" -> "🎵"
                    "sticker" -> "😆"
                    else -> "🎯"
                }
                ContentCard(
                    emoji = emoji, title = content.title,
                    description = content.description, badgeText = content.badgeText,
                    onClick = { },
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                )
            }

            item { Spacer(modifier = Modifier.height(80.dp)) }
        }
    }
}

@Composable
private fun GalleryHeader() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .clip(RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp))
            .background(
                Brush.horizontalGradient(listOf(NailongPrimary, NailongSecondary)),
            ),
        contentAlignment = Alignment.Center,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = "🐉", fontSize = 36.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "奶龍藝術館",
                style = MaterialTheme.typography.displayLarge,
                color = Color.White,
            )
            Text(
                text = "NAILONG ART GALLERY",
                style = MaterialTheme.typography.labelMedium,
                color = Color.White.copy(alpha = 0.8f),
                letterSpacing = 4.sp,
            )
        }
    }
}
