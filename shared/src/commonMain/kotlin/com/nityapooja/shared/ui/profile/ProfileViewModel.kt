package com.nityapooja.shared.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nityapooja.shared.data.local.entity.BookmarkEntity
import com.nityapooja.shared.data.local.entity.ReadingHistoryEntity
import com.nityapooja.shared.data.repository.DevotionalRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val repository: DevotionalRepository,
) : ViewModel() {

    val allBookmarks: StateFlow<List<BookmarkEntity>> = repository.getAllBookmarks()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val bookmarksByType: StateFlow<Map<String, List<BookmarkEntity>>> = repository.getAllBookmarks()
        .map { bookmarks -> bookmarks.groupBy { it.contentType } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyMap())

    val recentHistory: StateFlow<List<ReadingHistoryEntity>> = repository.getRecentHistory(15)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun clearHistory() {
        viewModelScope.launch {
            repository.clearHistory()
        }
    }
}
