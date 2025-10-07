package com.example.ptt.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.graphics.Color
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


// RockbottomDetailsScreen.kt
@Composable
fun RockbottomDetailsScreen(
    onBack: () -> Unit,
    nav: NavHostController,
) {
    // shared VM vom Rockbottom-Backstack holen
    val parentEntry = remember(nav) { nav.getBackStackEntry(Route.Rockbottom.path) }
    val vm: RockbottomViewModel = viewModel(parentEntry)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .navigationBarsPadding(), // Inhalt kollidiert nicht mit Systemleiste
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(onClick = onBack) { Text("‹ Back") }
            Text("Rockbottom – Details", style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.width(48.dp))
        }

        Spacer(Modifier.height(16.dp))
        HorizontalDivider()
        Spacer(Modifier.height(16.dp))

        // Optional: Parameter kurz oben
        val sac = vm.sacPerDiver.toString().toDoubleOrNull() ?: 0.0
        val teamSac = sac * 2
        val ascent = vm.ascentRateMpm.toString().toIntOrNull() ?: 0
        val delay = vm.delayMin.toString().toIntOrNull() ?: 0


        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = String.format("SAC: %.1f × 2 = %.1f L/min", sac, teamSac),
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "Ascent: $ascent m/min",
                style = MaterialTheme.typography.bodyMedium
            )
        }

        Text(
            text = "Delay: $delay min:",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.align(Alignment.Start)
        )

        Spacer(Modifier.height(16.dp))

        // Herleitung/Segmente
        val segments = vm.calcSegments // List<Segment(label: String, gasL: Int, formula: String)>

        if (segments.isEmpty()) {
            Text("Keine Details vorhanden.", style = MaterialTheme.typography.bodyMedium)
            return@Column
        }

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            // 🔹 Einzelne Segmente
            itemsIndexed(segments, key = { i, s -> "${s.label}_${s.gasL}_$i" }) { i, seg ->
                Column(Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("${i + 1}. ${seg.label}", style = MaterialTheme.typography.bodyLarge)
                        Text("${seg.gasL} L", style = MaterialTheme.typography.bodyLarge)
                    }
                    Text(
                        seg.formula,
                        style = MaterialTheme.typography.bodySmall,
                        fontFamily = FontFamily.Monospace,
                        color = Color.Gray
                    )
                }
            }

            // 🔹 Summenzeile am Ende
            item {
                Spacer(Modifier.height(8.dp))
                HorizontalDivider()
                Spacer(Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Total Gas:", style = MaterialTheme.typography.titleMedium)
                    Text("${vm.calcGasL} L", style = MaterialTheme.typography.titleMedium)
                }
            }

            //  Herleitung Umrechnung in bar
            item {
                val gas = vm.calcGasL ?: 0
                val cyl = vm.cylinderL.toIntOrNull() ?: 1
                val bar = vm.calcBar ?: 0   // falls noch null, dann 0

                Spacer(Modifier.height(8.dp))   // etwas Abstand zur vorherigen Zeile

                Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.End) {
                    Text(
                        String.format(java.util.Locale.GERMAN, "÷ %d L", cyl),
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray,
                        fontFamily = FontFamily.Monospace
                    )
                    Text(
                        String.format(java.util.Locale.GERMAN, "= %d bar", bar),
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }




        }
        //String.format(java.util.Locale.GERMAN, "%.1f bar  %d ÷ %d L", bar, gas, cyl),
        }
    }
