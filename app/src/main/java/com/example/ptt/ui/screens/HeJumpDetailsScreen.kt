package com.example.ptt.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.ptt.domain.model.HeJumpResult
import com.example.ptt.domain.model.HeJumpViolation
import kotlin.math.abs
import kotlin.math.roundToInt

@Composable
fun HeJumpDetailsScreen(
    onBack: () -> Unit,
    result: HeJumpResult?,
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
        fun pp(x: Double)  = "${(x * 100.0).roundToInt()} pp"

        // Hauptstatus priorisiert: Lean→Heavy > OneFifth > OK
        val hasLeanToHeavy  = HeJumpViolation.LeanToHeavy in result.violations
        val hasOneFifthViol = HeJumpViolation.OneFifthRule in result.violations

        val statusText: String
        val statusColor = if (hasLeanToHeavy || hasOneFifthViol)
            MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary

        statusText = when {
            hasLeanToHeavy -> "❌ Never switch from a helium-lean gas to a helium-rich gas during ascent!"
            hasOneFifthViol -> "⚠️ Jump violates 1/5 rule"
            else -> "✅ Jump within 1/5 rule"
        }

        // Status (zentriert)
        Box(
            modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                statusText,
                color = statusColor,
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center
            )
        }

        // --- From / To Übersicht ---
        SectionTitle("From gas")
        KeyValueRow("O₂", pct(result.fromO2))
        KeyValueRow("He", pct(result.fromHe))
        KeyValueRow("N₂", pct(result.fromN2))

        Spacer(Modifier.height(12.dp))

        SectionTitle("To gas")
        KeyValueRow("O₂", pct(result.toO2))
        KeyValueRow("He", pct(result.toHe))
        KeyValueRow("N₂", pct(result.toN2))

        Spacer(Modifier.height(16.dp))

        // --- Deltas ---
        SectionTitle("Changes (Δ)")
        KeyValueRow("ΔHe", pp(result.deltaHe))
        KeyValueRow("ΔN₂", pp(result.deltaN2))

        Spacer(Modifier.height(16.dp))

        // --- 1/5-Regel, mit eingesetzten Zahlen ---
        val lhs = abs(result.deltaN2)                // |ΔN₂|
        val rhs = 0.2 * abs(result.deltaHe)          // 0.2 * |ΔHe|
        SectionTitle("One-fifth rule")

// Beispiel: 3 pp ≤ 0.2 × 17 pp = 3.4 pp
        val lhsStr = "%.1f".format(lhs * 100)
        val heStr = "%.1f".format(abs(result.deltaHe) * 100)
        val rhsStr = "%.1f".format(rhs * 100)

        Text(
            "$lhsStr pp ≤ 0.2 × $heStr pp = $rhsStr pp",
            style = MaterialTheme.typography.bodyMedium,
            fontFamily = FontFamily.Monospace
        )

        // Bewertung
        Text(
            if (result.withinOneFifthRule) "✓ satisfied" else "✗ violated",
            color = if (result.withinOneFifthRule)
                MaterialTheme.colorScheme.primary
            else
                MaterialTheme.colorScheme.error,
            style = MaterialTheme.typography.bodyMedium,
            fontFamily = FontFamily.Monospace
        )

        // --- Zusatzregel Lean→Heavy ---
        if (hasLeanToHeavy) {
            Spacer(Modifier.height(12.dp))
            SectionTitle("Additional safety rule")
            Text(
                "Never switch from a helium-lean gas to a helium-rich gas during ascent.",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium
            )
        }

        // --- Empfehlung (falls vorhanden) ---
        result.recommendedGas?.let { rec ->
            Spacer(Modifier.height(16.dp))
            HorizontalDivider(Modifier, DividerDefaults.Thickness, DividerDefaults.color)
            Spacer(Modifier.height(12.dp))

            SectionTitle("Suggested mix (to satisfy 1/5)")
            KeyValueRow("O₂ (to)", pct(rec.fO2))
            KeyValueRow("He (to)", pct(rec.fHe))

            // Optional: Apply-Button, falls Callback bereitgestellt
            onApplyRecommendation?.let { apply ->
                Spacer(Modifier.height(8.dp))
                val recO2 = (rec.fO2 * 100.0).roundToInt()
                val recHe = (rec.fHe * 100.0).roundToInt()
                Button(
                    onClick = { apply(recO2, recHe) },
                    modifier = Modifier.fillMaxWidth()
                ) { Text("Apply suggestion ($recO2% O₂ / $recHe% He)") }
            }
        }
    }
}

@Composable
private fun SectionTitle(title: String) {
    Text(title, style = MaterialTheme.typography.titleSmall)
    Spacer(Modifier.height(6.dp))
}

@Composable
private fun KeyValueRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, style = MaterialTheme.typography.bodyMedium)
        Text(value, style = MaterialTheme.typography.bodyMedium)
    }
}
