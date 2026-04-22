package com.nityapooja.shared.ui.badges

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nityapooja.shared.data.local.dao.BadgeDao
import com.nityapooja.shared.data.local.dao.JapaSessionDao
import com.nityapooja.shared.data.local.entity.BadgeEntity
import com.nityapooja.shared.data.local.entity.BadgeType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate

class BadgeViewModel(
    private val japaSessionDao: JapaSessionDao,
    private val badgeDao: BadgeDao,
) : ViewModel() {

    val unlockedBadges: StateFlow<List<BadgeEntity>> = badgeDao.getAllBadges()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _newBadge = MutableStateFlow<BadgeType?>(null)
    val newBadge: StateFlow<BadgeType?> = _newBadge.asStateFlow()

    init {
        checkAndUnlockBadges()
    }

    fun checkAndUnlockBadges() {
        viewModelScope.launch {
            val totalMalas = japaSessionDao.getTotalMalas().first()
            val dates = japaSessionDao.getAllSessionDates().first()
            val activeDays = dates.size

            val checks: List<Pair<BadgeType, Boolean>> = listOf(
                BadgeType.PRATHAMAM to (totalMalas >= 1),
                BadgeType.TRIPATAKA to hasConsecutiveDays(dates, 3),
                BadgeType.SAPTARISHI to (activeDays >= 7),
                BadgeType.EKADASHI_WARRIOR to (activeDays >= 11),
                BadgeType.SHATABHISHA to (totalMalas >= 100),
                BadgeType.SAHASRA_DEEPAM to (totalMalas >= 1000),
                BadgeType.VAIKUNTA_DWARAM to hasConsecutiveDays(dates, 30),
                BadgeType.TRIMURTI_VRAT to hasConsecutiveDays(dates, 21),
                BadgeType.MANDALA_SEVA to hasConsecutiveDays(dates, 40),
                BadgeType.SHATA_DEEPAM_STREAK to hasConsecutiveDays(dates, 108),
            )

            for ((badgeType, earned) in checks) {
                if (earned) {
                    maybeUnlock(badgeType)
                }
            }
        }
    }

    fun onQuizScore(score: Int, total: Int) {
        if (total > 0 && score.toDouble() / total >= 0.7) {
            viewModelScope.launch {
                maybeUnlock(BadgeType.PURANA_SCHOLAR)
            }
        }
    }

    fun dismissNewBadge() {
        val badge = _newBadge.value ?: return
        _newBadge.value = null
        viewModelScope.launch {
            badgeDao.markShown(badge.name)
        }
    }

    // -------------------------------------------------------------------------
    // Private helpers
    // -------------------------------------------------------------------------

    /**
     * Inserts the badge if not already present, then surfaces it as newBadge
     * if it hasn't been shown to the user yet.
     */
    private suspend fun maybeUnlock(badgeType: BadgeType) {
        val existing = badgeDao.getBadge(badgeType.name)
        if (existing == null) {
            val entity = BadgeEntity(
                badgeType = badgeType.name,
                unlockedAt = Clock.System.now().toEpochMilliseconds(),
                shownToUser = false,
            )
            badgeDao.insertBadge(entity)
            // Surface celebration dialog only once
            if (_newBadge.value == null) {
                _newBadge.value = badgeType
            }
        } else if (!existing.shownToUser && _newBadge.value == null) {
            // Was unlocked in a previous session but never shown (e.g. app was killed)
            _newBadge.value = badgeType
        }
    }

    /**
     * Returns true if the given sorted-descending date strings contain at least
     * [requiredDays] calendar days in an unbroken consecutive run.
     */
    private fun hasConsecutiveDays(dates: List<String>, requiredDays: Int): Boolean {
        if (dates.size < requiredDays) return false

        val sorted = dates
            .mapNotNull { runCatching { LocalDate.parse(it) }.getOrNull() }
            .map { it.toEpochDays() }
            .sorted().distinct() // ascending, deduped

        if (sorted.size < requiredDays) return false

        var consecutive = 1
        var prev = sorted.first()
        for (day in sorted.drop(1)) {
            if (day == prev + 1) {
                consecutive++
                if (consecutive >= requiredDays) return true
            } else {
                consecutive = 1
            }
            prev = day
        }
        return consecutive >= requiredDays
    }
}
