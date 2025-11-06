package com.example.ptt.domain.model

// Regel-Verstöße (UI formuliert die Texte)
enum class HeJumpViolation { OneFifthRule, LeanToHeavy }

// OC/CCR (zukunftssicher; aktuell OC)
enum class Mode { OC, CCR }

// Gas-Mix als Fraktionen (0.0 .. 1.0)
data class GasMix(
    val fO2: Double,
    val fHe: Double
) {
    val fN2: Double get() = 1.0 - fO2 - fHe
    fun isValid(): Boolean = fO2 >= 0.0 && fHe >= 0.0 && fO2 + fHe <= 1.0
}

// Ergebnis der Domain-Berechnung – KEINE UI-Texte!
data class HeJumpResult(
    val mode: Mode = Mode.OC,

    val fromO2: Double, val toO2: Double,
    val fromHe: Double, val toHe: Double,
    val fromN2: Double, val toN2: Double,

    val deltaHe: Double,
    val deltaN2: Double,
    val allowedDeltaN2: Double,

    val withinOneFifthRule: Boolean,
    val violations: Set<HeJumpViolation> = emptySet(),

    val recommendedGas: GasMix? = null,
    val recommendationNote: String? = null
)
