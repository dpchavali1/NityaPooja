package com.nityapooja.app.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nityapooja.app.data.local.entity.BookmarkEntity
import com.nityapooja.app.data.local.entity.ReadingHistoryEntity
import com.nityapooja.app.data.repository.DevotionalRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val repository: DevotionalRepository,
) : ViewModel() {

    val bookmarks: StateFlow<List<BookmarkEntity>> = repository.getAllBookmarks()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val bookmarksByType: StateFlow<Map<String, List<BookmarkEntity>>> = repository.getAllBookmarks()
        .map { bookmarks -> bookmarks.groupBy { it.contentType } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyMap())

    val recentHistory: StateFlow<List<ReadingHistoryEntity>> = repository.getRecentHistory(15)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun clearHistory() {
        viewModelScope.launch { repository.clearHistory() }
    }
}
