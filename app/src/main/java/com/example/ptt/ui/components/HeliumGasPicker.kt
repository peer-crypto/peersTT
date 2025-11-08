package com.example.ptt.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp


@Composable
fun HeJumpGasPicker(
    title: String,
    o2PctStr: String,
    hePctStr: String,
    onO2Change: (String) -> Unit,
    onHeChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val o2 = o2PctStr.toIntOrNull()
    val he = hePctStr.toIntOrNull()

    val o2Invalid = o2 == null || o2 !in 0..100
    val heInvalid = he == null || he !in 0..100
    val sumInvalid = (o2 ?: 0) + (he ?: 0) > 100

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(title, style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(8.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                CompactNumberField(
                    value = o2PctStr,
                    onValueChange = onO2Change,
                    placeholder = "O₂",
                    isError = o2Invalid || sumInvalid
                )
                Spacer(Modifier.width(4.dp))
                Text("%", style = MaterialTheme.typography.bodySmall)
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                CompactNumberField(
                    value = hePctStr,
                    onValueChange = onHeChange,
                    placeholder = "He",
                    isError = heInvalid || sumInvalid
                )
                Spacer(Modifier.width(4.dp))
                Text("%", style = MaterialTheme.typography.bodySmall)
            }
        }

        if (sumInvalid) {
            Spacer(Modifier.height(6.dp))
            Text(
                "O₂ + He > 100%",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}
