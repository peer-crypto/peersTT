package com.example.ptt.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.ptt.domain.model.HeJumpResult


// Überladene Versionen
@Composable
fun GasMatrixTable(result: HeJumpResult) {
    GasMatrixTable(
        fromO2 = result.fromO2, fromHe = result.fromHe, fromN2 = result.fromN2,
        toO2   = result.toO2,   toHe   = result.toHe,   toN2   = result.toN2
    )
}
@Composable
fun GasMatrixTable(
    fromO2: Double, fromHe: Double, fromN2: Double,
    toO2: Double,   toHe: Double,   toN2: Double
) {
    fun pct1(x: Double) = String.format("%.1f %%", x * 100)
    fun deltaFmt(x: Double) = (if (x >= 0) "+" else "−") +
            String.format("%.1f %%", kotlin.math.abs(x) * 100)

    val dO2 = toO2 - fromO2
    val dHe = toHe - fromHe
    val dN2 = toN2 - fromN2

    val okCol  = Color(0xFF2E7D32)
    val errCol = MaterialTheme.colorScheme.error

    // Einfache integrierte Prüfung der 1/5-Regel
    val withinOneFifth = kotlin.math.abs(dN2) <= 0.2 * kotlin.math.abs(dHe)
    val deltaColor = if (withinOneFifth) okCol else errCol

    Column(Modifier.fillMaxWidth()) {
        // Kopfzeile
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("", Modifier.weight(1.1f))
            Text("O₂", Modifier.weight(1f), style = MaterialTheme.typography.bodyMedium, textAlign = TextAlign.End)
            Text("He", Modifier.weight(1f), style = MaterialTheme.typography.bodyMedium, textAlign = TextAlign.End)
            Text("N₂", Modifier.weight(1f), style = MaterialTheme.typography.bodyMedium, textAlign = TextAlign.End)
        }

        // From-Zeile
        Row(Modifier.fillMaxWidth().padding(vertical = 2.dp)) {
            Text("From", Modifier.weight(1.1f))
            Text(pct1(fromO2), Modifier.weight(1f), textAlign = TextAlign.End, fontFamily = FontFamily.Monospace)
            Text(pct1(fromHe), Modifier.weight(1f), textAlign = TextAlign.End, fontFamily = FontFamily.Monospace)
            Text(pct1(fromN2), Modifier.weight(1f), textAlign = TextAlign.End, fontFamily = FontFamily.Monospace)
        }

        // To-Zeile
        Row(Modifier.fillMaxWidth().padding(vertical = 2.dp)) {
            Text("To", Modifier.weight(1.1f))
            Text(pct1(toO2), Modifier.weight(1f), textAlign = TextAlign.End, fontFamily = FontFamily.Monospace)
            Text(pct1(toHe), Modifier.weight(1f), textAlign = TextAlign.End, fontFamily = FontFamily.Monospace)
            Text(pct1(toN2), Modifier.weight(1f), textAlign = TextAlign.End, fontFamily = FontFamily.Monospace)
        }

        HorizontalDivider(Modifier.padding(vertical = 4.dp))

        // Δ-Zeile
        Row(Modifier.fillMaxWidth().padding(top = 2.dp)) {
            Text("Δ", Modifier.weight(1.1f))
            Text(deltaFmt(dO2),
                Modifier.weight(1f),
                textAlign = TextAlign.End,
                fontFamily = FontFamily.Monospace)
            Text(deltaFmt(dHe),
                Modifier.weight(1f),
                textAlign = TextAlign.End,
                fontFamily = FontFamily.Monospace,
                color = deltaColor)
            Text(deltaFmt(dN2),
                Modifier.weight(1f),
                textAlign = TextAlign.End,
                fontFamily = FontFamily.Monospace,
                color = deltaColor)
        }
    }
}
