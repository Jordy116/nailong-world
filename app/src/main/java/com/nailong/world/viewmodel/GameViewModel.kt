package com.nailong.world.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nailong.world.data.model.GameItem
import com.nailong.world.data.repository.NailongRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class GameUiState(
    val games: List<GameItem> = emptyList(),
    val selectedCategory: String = "all",
    val isLoading: Boolean = true,
)

class GameViewModel : ViewModel() {

    private val repository = NailongRepository()

    private val _uiState = MutableStateFlow(GameUiState())
    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()

    init {
        loadGames()
    }

    fun loadGames() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            val games = repository.getAllGames()
            _uiState.value = GameUiState(
                games = games,
                selectedCategory = _uiState.value.selectedCategory,
                isLoading = false,
            )
        }
    }

    fun selectCategory(category: String) {
        _uiState.value = _uiState.value.copy(selectedCategory = category)
    }

    fun getFilteredGames(): List<GameItem> {
        val state = _uiState.value
        return when (state.selectedCategory) {
            "all" -> state.games
            else -> state.games.filter { it.category == state.selectedCategory }
        }
    }
}
