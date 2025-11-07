package com.example.ptt.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.ptt.ui.format.*


@Composable

fun HeJumpGasPicker(
    title: String,
    o2Pct: Int,
    hePct: Int,
    onO2Change: (Int) -> Unit,
    onHeChange: (Int) -> Unit,
    step: Int = 1,   //  Granularität (1%, 2%, 5%, ...)
    modifier: Modifier = Modifier
) {
    // Vollbereich
    val o2All = remember(step) { percentRange(step) }
    val heAll = remember(step) { percentRange(step) }

    // Dynamisch filtern, damit O2 + He <= 100 bleibt
    val o2Opts = remember(hePct, step) { o2All.filter { it <= 100 - hePct } }
    val heOpts = remember(o2Pct, step) { heAll.filter { it <= 100 - o2Pct } }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(title, style = MaterialTheme.typography.titleMedium, textAlign = TextAlign.Center)

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
            PercentDropdownColumn(
                label = "O₂",
                valuePct = o2Pct,
                onChange = { onO2Change(it) },
                options = o2Opts
            )
            PercentDropdownColumn(
                label = "He",
                valuePct = hePct,
                onChange = { onHeChange(it) },
                options = heOpts
            )
        }
    }
}
