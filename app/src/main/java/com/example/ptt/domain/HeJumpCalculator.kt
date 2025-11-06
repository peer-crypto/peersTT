package com.example.ptt.domain

import com.example.ptt.domain.model.GasMix
import com.example.ptt.domain.model.HeJumpResult
import com.example.ptt.domain.model.HeJumpViolation
import com.example.ptt.domain.model.Mode
import kotlin.math.abs

object HeJumpCalculator {

    fun checkOC(from: GasMix, to: GasMix): HeJumpResult {
        val fromO2 = from.fO2; val fromHe = from.fHe; val fromN2 = from.fN2
        val toO2   = to.fO2;   val toHe   = to.fHe;   val toN2   = to.fN2

        val deltaHe = toHe - fromHe
        val deltaN2 = toN2 - fromN2

        val allowedDeltaN2 = 0.2 * abs(deltaHe)
        val withinOneFifth = abs(deltaN2) <= allowedDeltaN2 || deltaN2 <= 1e-9

        // Verstöße sammeln
        val ruleViolations = mutableSetOf<HeJumpViolation>()
        if (!withinOneFifth) ruleViolations += HeJumpViolation.OneFifthRule
        if (fromHe < toHe)   ruleViolations += HeJumpViolation.LeanToHeavy

        // Empfehlung nur bei 1/5-Verstoß
        var recommended: GasMix? = null
        var recNote: String? = null

        // fHe_to* = fHe_from + (fO2_from - fO2_to) / 0.8
        if (!withinOneFifth) {
            val fHeRecommended = fromHe + (fromO2 - toO2) / 0.8
            val fHeClamped = fHeRecommended.coerceIn(0.0, 1.0 - toO2)
            recommended = GasMix(fO2 = toO2, fHe = fHeClamped)
            recNote = when {
                abs(fHeRecommended - fHeClamped) < 1e-6 -> "BOUNDARY_HE"
                fHeClamped == 0.0                       -> "HE_ZERO_NITROX"
                else                                    -> "FEASIBLE_RANGE"
            }
        }

        return HeJumpResult(
            mode = Mode.OC,
            fromO2 = fromO2, toO2 = toO2,
            fromHe = fromHe, toHe = toHe,
            fromN2 = fromN2, toN2 = toN2,
            deltaHe = deltaHe,
            deltaN2 = deltaN2,
            allowedDeltaN2 = allowedDeltaN2,
            withinOneFifthRule = withinOneFifth,
            violations = ruleViolations,
            recommendedGas = recommended,
            recommendationNote = recNote
        )
    }
}
