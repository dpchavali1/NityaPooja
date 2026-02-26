package com.nityapooja.shared.ui.virtualpooja

import com.nityapooja.shared.data.local.entity.DeityEntity

/**
 * State models for the Virtual Pooja Room.
 */

// ═══════════════════════════════════════════════════════════
// Pooja offerings — 8 ritual items
// ═══════════════════════════════════════════════════════════

enum class PoojaItem(
    val labelTelugu: String,
    val labelEnglish: String,
    val emoji: String,
) {
    DEEPAM("దీపం", "Deepam", "🪔"),
    PUSHPAM("పుష్పం", "Pushpam", "🌸"),
    NAIVEDYAM("నైవేద్యం", "Naivedyam", "🥥"),
    DHOOP("ధూపం", "Dhoop", "🪵"),
    ABHISHEKAM("అభిషేకం", "Abhishekam", "💧"),
    GHANTA("ఘంట", "Ghanta", "🔔"),
    HARATHI("హారతి", "Harathi", "🪔"),
    KUMKUM("కుంకుమ", "Kumkum", "🌺"),
}

enum class AbhishekamType(val labelTelugu: String, val labelEnglish: String, val emoji: String) {
    WATER("జలం", "Water", "💧"),
    MILK("పాలు", "Milk", "🥛"),
}

data class OfferingState(
    val item: PoojaItem,
    val isDone: Boolean = false,
    val isAnimating: Boolean = false,
)

// ═══════════════════════════════════════════════════════════
// Particle data for animations
// ═══════════════════════════════════════════════════════════

data class FloatingPetal(
    val id: Int,
    val startX: Float,      // fraction 0f..1f of altar width
    val rotation: Float,     // initial rotation degrees
    val sizeFactor: Float,   // 0.8f..1.3f
    val colorIndex: Int,     // index into petal emoji list
)

data class SmokeParticle(
    val id: Int,
    val startX: Float,       // fraction of altar width
    val driftX: Float,       // horizontal drift amount
)

// ═══════════════════════════════════════════════════════════
// Top-level UI State
// ═══════════════════════════════════════════════════════════

data class VirtualPoojaRoomUiState(
    val allDeities: List<DeityEntity> = emptyList(),
    val selectedDeityId: Int? = null,
    val selectedDeity: DeityEntity? = null,
    val offerings: Map<PoojaItem, OfferingState> = PoojaItem.entries.associateWith { OfferingState(it) },
    val abhishekamType: AbhishekamType = AbhishekamType.WATER,
    val isLoading: Boolean = true,
    val floatingPetals: List<FloatingPetal> = emptyList(),
    val smokeParticles: List<SmokeParticle> = emptyList(),
    val showCompletionBanner: Boolean = false,
    val showAbhishekamToggle: Boolean = false,
)
