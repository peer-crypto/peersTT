package com.example.ptt.domain

import com.example.ptt.domain.model.GasMix
import com.example.ptt.domain.model.HeJumpResult
import com.example.ptt.domain.model.HeJumpViolation
import com.example.ptt.domain.model.Mode
import com.example.ptt.domain.model.Recommendation
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

        // Empfehlung
        var recommended: GasMix? = null
        var recNote: String? = null

        // ──> NUR DIESEN BLOCK verwenden (und die frühere fHeRecommended-Zeile oben entfernen)
        if (!withinOneFifth) {
            // Grenzwert der 1/5-Regel bei fixem O2_to:
            val fHeTarget = fromHe + (fromO2 - toO2) / 0.8
            val eps = 1e-9

            // Wenn dafür Helium ERHÖHT werden müsste (lean→heavy), KEIN Primärvorschlag
            if (fHeTarget > fromHe + eps) {
                recommended = null
                recNote = null
            } else {
                val fHeClamped = fHeTarget.coerceIn(0.0, 1.0 - toO2)
                recommended = GasMix(fO2 = toO2, fHe = fHeClamped)
                recNote = when {
                    abs(fHeTarget - fHeClamped) < 1e-6 -> "BOUNDARY_HE"
                    fHeClamped == 0.0                  -> "HE_ZERO_NITROX"
                    else                               -> "FEASIBLE_RANGE"
                }
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


    // domain/HeJumpCalculator.kt
    private fun oneFifthOk(a: GasMix, b: GasMix): Boolean {
        val dHe = b.fHe - a.fHe
        val dN2 = (1 - b.fO2 - b.fHe) - (1 - a.fO2 - a.fHe)
        return kotlin.math.abs(dN2) <= 0.2 * kotlin.math.abs(dHe) || dN2 <= 1e-12
    }

    private fun leanToHeavy(a: GasMix, b: GasMix) = b.fHe > a.fHe
    private fun valid(g: GasMix) = g.fO2 >= 0 && g.fHe >= 0 && g.fO2 + g.fHe <= 1

    // Standardmixe (gern anpassen)
    private val STANDARD_MIXES = listOf(
        GasMix(0.50, 0.00), // Nx50
        GasMix(0.80, 0.00), // Nx80
        GasMix(1.00, 0.00), // O2
        GasMix(0.21, 0.35), // 21/35
        GasMix(0.18, 0.45), // 18/45
        GasMix(0.15, 0.55), // 15/55
        GasMix(0.12, 0.65), // 12/65
        GasMix(0.10, 0.70)  // 10/70
    )

    fun recommendIntermediate(from: GasMix, to: GasMix): Recommendation {
        if (valid(to) && oneFifthOk(from, to) && !leanToHeavy(from, to)) {
            return Recommendation.Single(to)
        }
        val candidates = STANDARD_MIXES.filter { s ->
            valid(s)
                    && oneFifthOk(from, s) && !leanToHeavy(from, s)
                    && oneFifthOk(s, to) && !leanToHeavy(s, to)
        }
        if (candidates.isEmpty()) return Recommendation.NoFeasible
        val best = candidates.minBy { s ->  // „nahe“ am Ziel
            kotlin.math.abs(s.fO2 - to.fO2) + kotlin.math.abs(s.fHe - to.fHe)
        }
        return Recommendation.TwoStep(best, to)
    }
}