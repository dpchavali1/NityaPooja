package com.nityapooja.shared.data.local.entity

enum class FamilyDayType(val displayNameEn: String, val displayNameTel: String, val emoji: String) {
    BIRTHDAY("Birthday", "పుట్టినరోజు", "🎂"),
    ANNIVERSARY("Anniversary", "వివాహ వార్షికోత్సవం", "💑"),
    TITHI("Tithi / Shraddha", "తిథి / శ్రాద్ధం", "🪔"),
    CUSTOM("Special Day", "ప్రత్యేక రోజు", "⭐")
}
