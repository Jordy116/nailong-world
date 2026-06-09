package com.nailong.world.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nailong.world.data.model.CheckInState
import com.nailong.world.data.model.ContentItem
import com.nailong.world.data.model.GameItem
import com.nailong.world.data.model.LiveStreamInfo
import com.nailong.world.data.repository.NailongRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class HomeUiState(
    val liveInfo: LiveStreamInfo = LiveStreamInfo(),
    val hotGames: List<GameItem> = emptyList(),
    val recommendedContent: List<ContentItem> = emptyList(),
    val checkIn: CheckInState = CheckInState(),
    val isLoading: Boolean = true,
    val error: String? = null,
)

class HomeViewModel : ViewModel() {

    private val repository = NailongRepository()

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadHomeData()
    }

    fun loadHomeData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                val liveInfo = repository.getLiveStreamInfo()
                val hotGames = repository.getHotGames()
                val content = repository.getRecommendedContent()
                val checkIn = repository.getCheckInState()
                _uiState.value = HomeUiState(
                    liveInfo = liveInfo,
                    hotGames = hotGames,
                    recommendedContent = content,
                    checkIn = checkIn,
                    isLoading = false,
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "載入失敗",
                )
            }
        }
    }

    fun performCheckIn() {
        viewModelScope.launch {
            try {
                val newState = repository.performCheckIn()
                _uiState.value = _uiState.value.copy(checkIn = newState)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = "簽到失敗，請稍後再試")
            }
        }
    }
}
