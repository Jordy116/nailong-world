package com.nailong.world.ui.game

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.nailong.world.ui.components.GameCard
import com.nailong.world.ui.theme.NailongOrange
import com.nailong.world.viewmodel.GameViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameScreen(
    modifier: Modifier = Modifier,
    viewModel: GameViewModel = viewModel(),
) {
    val state by viewModel.uiState.collectAsState()
    val categories = listOf("all" to "全部", "hot" to "最熱門", "recommended" to "推薦", "new" to "最新")

    if (state.isLoading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            CircularProgressIndicator(color = NailongOrange)
        }
        return
    }

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        // Category chips — full width first row
        item(span = { GridItemSpan(maxLineSpan) }) {
            androidx.compose.foundation.lazy.LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                items(categories.size) { index ->
                    val (key, label) = categories[index]
                    FilterChip(
                        selected = state.selectedCategory == key,
                        onClick = { viewModel.selectCategory(key) },
                        label = { Text(label) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = NailongOrange,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
                        ),
                        shape = RoundedCornerShape(20.dp),
                    )
                }
            }
        }

        // Game cards
        items(viewModel.getFilteredGames()) { game ->
            GameCard(
                title = game.title,
                description = game.description,
                onClick = { /* launch game */ },
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}
