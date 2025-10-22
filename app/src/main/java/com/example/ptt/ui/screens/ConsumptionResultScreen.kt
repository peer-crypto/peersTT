// ConsumptionResultScreen.kt
package com.example.ptt.ui.screens

import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController


@Composable
fun ConsumptionResultScreen(
    vm: com.example.ptt.viewmodel.ConsumptionViewModel,
    onBack: () -> Unit,
    onShowDetails: () -> Unit,

) {

    // liest die zuletzt berechnete Summary aus dem VM
    val summary = vm.lastSummary
    val modelExists = vm.lastBuiltModel != null


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(onClick = onBack) { Text("‹ Back") }
            Text("Consumption Result", style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.width(48.dp))
        }

        Spacer(Modifier.height(16.dp))
        HorizontalDivider()
        Spacer(Modifier.height(16.dp))

        if (summary == null) {
            // Falls keine Summary vorhanden ist
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Keine Berechnung vorhanden.", style = MaterialTheme.typography.bodyLarge)
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "Bitte berechne zuerst im Consumption Screen.",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

        } else {
            // Format-Helfer (UI-seitig runden)
            fun fmt(v: Double) = String.format("%.0f", v)
            val rem = summary.remainingBar
            val remStr = if (rem < 0.0) "-${fmt(-rem)} bar" else fmt(rem) + " bar"

            Text(
                "Used Gas",
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(Modifier.height(4.dp))
            Text(
                "${fmt(summary.usedLiters)} L",
                style = MaterialTheme.typography.headlineMedium
            )

            Spacer(Modifier.height(16.dp))

            Text(
                "Remaining cylinder pressure",
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(Modifier.height(4.dp))

            Text(
                remStr,
                style = MaterialTheme.typography.headlineMedium
            )


            Spacer(Modifier.height(24.dp))
            HorizontalDivider()
            Spacer(Modifier.height(16.dp))

            // Buttons: Details (nur aktiv, wenn ein Model vorhanden ist) + evtl. weitere Aktionen
            Row(
                modifier = Modifier.align(Alignment.End),
                // horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally)
            ) {
                Button(
                    onClick = onShowDetails,
                    enabled = modelExists,
                ) {
                    Text("Details anzeigen")
                }

            }
        }
    }
}

