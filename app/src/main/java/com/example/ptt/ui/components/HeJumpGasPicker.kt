package com.example.ptt.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp


@Composable
fun HeJumpGasPicker(
    title: String,
    o2Pct: Int,
    hePct: Int,
    onO2Change: (Int) -> Unit,
    onHeChange: (Int) -> Unit,
    o2Options: List<Int> = listOf(5, 10, 15, 18, 21, 28, 30, 32, 36, 40, 50, 80, 100),
    heOptions: List<Int> = listOf(0, 10, 18, 21, 25, 35, 45, 50, 55, 70),
    modifier: Modifier = Modifier
) {
    val n2 = (100 - o2Pct - hePct).coerceIn(0, 100)

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally   // 🔹 Titel + Inhalte zentrieren
    ) {
        Text(
            title,
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center                     // 🔹 Text mittig ausrichten
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            PercentDropdownColumn(
                label = "O₂",
                valuePct = o2Pct,
                onChange = onO2Change,
                options = o2Options
            )
            PercentDropdownColumn(
                label = "He",
                valuePct = hePct,
                onChange = onHeChange,
                options = heOptions
            )

        }

    }
}
