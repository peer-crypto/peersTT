package com.example.ptt.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ptt.viewmodel.RockbottomViewModel
import androidx.navigation.NavHostController
import androidx.compose.runtime.remember
import com.example.ptt.navigation.Route
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed


@Composable
fun RockbottomResultScreen(
    onBack: () -> Unit,
    nav: NavHostController, // NavController reinreichen
) {

    // shared VM vom RockBottom-Backstack holen
    val parentEntry = remember(nav) { nav.getBackStackEntry(Route.Rockbottom.path) }
    val vm: RockbottomViewModel = viewModel(parentEntry)

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(onClick = onBack) { Text("‹ Back") }
            Text("Rockbottom – Result", style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.width(48.dp))
        }

        Spacer(Modifier.height(16.dp))
        HorizontalDivider()
        Spacer(Modifier.height(16.dp))

        if (vm.calcGasL != null && vm.calcBar != null) {
            Text("Gas to first switch",
                style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(4.dp))
            Text("${vm.calcGasL} L",
                style = MaterialTheme.typography.headlineMedium)

            Spacer(Modifier.height(16.dp))

            Text("Required cylinder pressure",
                style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(4.dp))
            Text("${vm.calcBar} bar",
                style = MaterialTheme.typography.headlineMedium)

            // Platzhalter: Hier später „Herleitung/Segments“ einblenden
            Spacer(Modifier.height(24.dp))
            HorizontalDivider()
        } else {
            Text("No result available. Please go back and calculate.",
                style = MaterialTheme.typography.bodyMedium)
        }

        Spacer(Modifier.height(8.dp))

// "Details:"
        val sac = vm.sacPerDiver.toString().toDoubleOrNull() ?: 0.0
        val teamSac = sac * 2// Casten: String in Zahl
        val ascent = vm.ascentRateMpm.toString().toIntOrNull() ?: 0

        Row(    // beide Ausgben in einer Zeile
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text("Stress SAC:", style = MaterialTheme.typography.bodyMedium)
            Text(
                text = String.format("%.1f × 2 = %.1f L/min ;", sac, teamSac),
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "Ascent: ${ascent} m/min",
                style = MaterialTheme.typography.bodyMedium
            )
        }
        Spacer(Modifier.height(8.dp))


        val segments = vm.calcSegments
        if (segments.isNotEmpty()) {
            Spacer(Modifier.height(8.dp))
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                itemsIndexed(segments, key = { i, seg -> "${seg.label}_$i" }) { i, seg ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("${seg.label}", style = MaterialTheme.typography.bodyLarge)
                        Text("${seg.gasL} L", style = MaterialTheme.typography.bodyLarge)
                    }
                }

                // Summenzeile am Ende
                item {
                    Spacer(Modifier.height(8.dp))
                    HorizontalDivider()
                    Spacer(Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Summe Gas:", style = MaterialTheme.typography.titleMedium)
                        Text("${vm.calcGasL} L", style = MaterialTheme.typography.titleMedium)
                    }
                }
            }
        } else {
            Spacer(Modifier.height(8.dp))
            Text("Keine Segmentdaten vorhanden.", style = MaterialTheme.typography.bodyMedium)
        }

    }
}
