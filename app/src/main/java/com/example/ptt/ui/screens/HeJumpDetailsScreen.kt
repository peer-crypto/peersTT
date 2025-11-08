package com.example.ptt.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.ptt.domain.model.HeJumpResult
import com.example.ptt.domain.model.HeJumpViolation
import com.example.ptt.domain.model.Recommendation
import com.example.ptt.ui.components.GasMatrixTable
import kotlin.math.abs
import kotlin.math.roundToInt

@Composable
fun HeJumpDetailsScreen(
    onBack: () -> Unit,
    result: HeJumpResult?,
    altRec: Recommendation?,
    onApplyRecommendation: ((o2Pct: Int, hePct: Int) -> Unit)? = null
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .navigationBarsPadding(),
        verticalArrangement = Arrangement.Top
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(onClick = onBack) { Text("‹ Back") }
            Text("He Jump – Details", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.width(48.dp))
        }

        Spacer(Modifier.height(12.dp))

        if (result == null) {
            Text(
                "No result available.",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium
            )
            return
        }

        // --- Helper lambdas (nur Anzeige) ---
        fun pct(x: Double) = "${(x * 100.0).roundToInt()} %"
        fun pp(x: Double) = "${(x * 100.0).roundToInt()} pp"

        // Hauptstatus priorisiert: Lean→Heavy > OneFifth > OK
        val hasLeanToHeavy = HeJumpViolation.LeanToHeavy in result.violations
        val hasOneFifthViol = HeJumpViolation.OneFifthRule in result.violations


        val statusText: String
        val statusColor = if (hasLeanToHeavy || hasOneFifthViol)
            MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary

        statusText = when {
            hasOneFifthViol -> "⚠️ Jump violates 1/5 rule"
            else -> "✅ Jump within 1/5 rule"
        }

// 🔹 Status (zentriert)
        Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            Text(statusText, color = statusColor, style = MaterialTheme.typography.titleMedium)
        }

        Spacer(Modifier.height(24.dp))

// 🔹 kompakte Tabelle
        GasMatrixTable(result)

        Spacer(Modifier.height(16.dp))

// 🔹 1/5-Regel – Herleitung in EINER Zeile
        SectionTitle("One-fifth rule")
        val lhs = abs(result.deltaN2)           // |ΔN2|
        val rhs = 0.2 * abs(result.deltaHe)    // 0.2×|ΔHe|
        val lhsStr = "%.1f".format(lhs * 100)
        val heStr = "%.1f".format(abs(result.deltaHe) * 100)
        val rhsStr = "%.1f".format(rhs * 100)

        Text(
            "$lhsStr pp ≤ 0.2 × $heStr pp = $rhsStr pp",
            style = MaterialTheme.typography.bodyMedium,
            fontFamily = FontFamily.Monospace
        )

// kurze Bewertung

        val okCol  = Color(0xFF2E7D32)   // Material-Grün
        val errCol = MaterialTheme.colorScheme.error
        Text(
            if (result.withinOneFifthRule) "✓ satisfied" else "✗ violated",
            color = if (result.withinOneFifthRule) okCol else errCol,
            style = MaterialTheme.typography.bodyMedium,
            fontFamily = FontFamily.Monospace
        )

        Spacer(Modifier.height(16.dp))
        HorizontalDivider()
        Spacer(Modifier.height(16.dp))

// Flags
        val directOk = result.withinOneFifthRule &&
                (HeJumpViolation.LeanToHeavy !in result.violations)

        val primaryOk = (result.recommendedGas != null) &&
                (HeJumpViolation.LeanToHeavy !in result.violations)

// --- Primary (nur wenn erlaubt) ---
        if (primaryOk) {
            SectionTitle("Suggested mix (primary)")
            val recGas = result.recommendedGas!!
            KeyValueRow("O₂ (to)", pct(recGas.fO2))
            KeyValueRow("He (to)", pct(recGas.fHe))

            onApplyRecommendation?.let { apply ->
                Spacer(Modifier.height(8.dp))
                val recO2 = (recGas.fO2 * 100.0).roundToInt()
                val recHe = (recGas.fHe * 100.0).roundToInt()
                Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    Button(
                        onClick = { apply(recO2, recHe) },
                        modifier = Modifier.wrapContentWidth()
                    ) {
                        Text("Apply")
                        Spacer(Modifier.height(16.dp))
                    }
                }
            }
        }



// --- Alternative nur zeigen, wenn direkter Wechsel NICHT ok ist ---
        if (!directOk) {
            when (altRec) {
                is Recommendation.TwoStep -> {
                    Spacer(Modifier.height(16.dp)); HorizontalDivider(); Spacer(Modifier.height(8.dp))
                    HorizontalDivider()
                    SectionTitle("Alternative (intermediate path)")
                    Text("Two-step suggestion:", style = MaterialTheme.typography.bodyMedium)
                    KeyValueRow("Step 1 – Intermediate O₂", pct(altRec.first.fO2))
                    KeyValueRow("Step 1 – Intermediate He", pct(altRec.first.fHe))
                    KeyValueRow("Step 2 – Target O₂", pct(altRec.second.fO2))
                    KeyValueRow("Step 2 – Target He", pct(altRec.second.fHe))
                }

                Recommendation.NoFeasible, null -> {
                    Spacer(Modifier.height(12.dp))
                    HorizontalDivider()
                    Text("No suggestion feasible (beta)", color = MaterialTheme.colorScheme.error)
                }
                // (Single tritt hier praktisch nicht auf, weil dann directOk true wäre)
                is Recommendation.Single -> { /* no-op */
                }
            }
        }
    }
}
    @Composable
    fun SectionTitle(title: String) {
        Text(title, style = MaterialTheme.typography.titleSmall)
        Spacer(Modifier.height(6.dp))
    }

    @Composable
    fun KeyValueRow(label: String, value: String) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(label, style = MaterialTheme.typography.bodyMedium)
            Text(value, style = MaterialTheme.typography.bodyMedium, textAlign = TextAlign.End)
        }
    }