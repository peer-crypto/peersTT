package com.example.ptt.ui.screens


import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import com.example.ptt.domain.ConsumptionCalculator
import kotlin.math.ceil

import java.util.Locale


@Composable
fun ConsumptionDetailsScreen(
    vm: com.example.ptt.viewmodel.ConsumptionViewModel,
    onBack: () -> Unit

) {

    val model = vm.lastBuiltModel
    if (model == null) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Start) {
                IconButton(onClick = onBack) {
                    Icon(
                        Icons.Filled.ArrowBack,
                        contentDescription = "Back"
                    )
                }
                Spacer(Modifier.width(8.dp))
                Text("Details", style = MaterialTheme.typography.titleLarge)
            }
            Spacer(Modifier.height(16.dp))
            Text("Keine Berechnung vorhanden. Bitte erst auf Calculate im Consumption-Screen drücken.")
        }
        return
    }

    val details = ConsumptionCalculator.deriveDetails(model)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .navigationBarsPadding(), // Inhalt kollidiert nicht mit Systemleiste
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(onClick = onBack) { Text("‹ Back") }
            Text("Consumption–Details", style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.width(48.dp))
        }

        Spacer(Modifier.height(16.dp))
        HorizontalDivider()
        Spacer(Modifier.height(16.dp))

        // Parameter kurz oben
        val cfg = vm.lastBuiltModel?.settings
        val sac = cfg?.sacLpm ?: 0.0
        val ascent = cfg?.ascentRateMpm ?: 0.0
        val descent = cfg?.descentRateMpm ?: 0.0
        val cyl = cfg?.cylinderVolumeL ?: 0.0

        Text(
            text = String.format("SAC: %.1f L", sac),
            style = MaterialTheme.typography.bodySmall
        )
        Text(
            text = "Ascent: $ascent m/min",
            style = MaterialTheme.typography.bodySmall
        )

        Text(
            text = "Descent: $descent m/min",
            style = MaterialTheme.typography.bodySmall
        )

        Text(
            text = "Cylinder: $cyl L",
            style = MaterialTheme.typography.bodySmall
        )

        Text(
            text = "Filling Pressure: $cyl L",
            style = MaterialTheme.typography.bodySmall
        )

        Spacer(Modifier.height(16.dp))

        val summary = vm.lastSummary
        val model = vm.lastBuiltModel

        val totalLitersExact = summary?.usedLiters ?: 0.0
        val totalLitersUp = kotlin.math.ceil(totalLitersExact).toInt()
        val cylL = model?.settings?.cylinderVolumeL ?: 1.0
        val totalBarExact = totalLitersExact / cylL

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            contentPadding = PaddingValues(bottom = 24.dp)
        )
        {
            // 🔹 Einzelne Segmente
            itemsIndexed(details.legs) { idx, leg ->
                Column(Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        when (leg) {
                            is ConsumptionCalculator.Leg.MoveLeg -> {
                                val direction = if (leg.toM < leg.fromM) "Ascent" else "Descent"

                                Column {
                                    Text(
                                        "${idx + 1}: $direction ${leg.fromM} → ${leg.toM} m",
                                        style = MaterialTheme.typography.bodyLarge
                                    )


                                    Text(
                                        String.format(
                                            Locale.getDefault(),
                                            "%.0f L x %.1f bar x %.1f min",
                                            sac, leg.avgAta, leg.timeMin
                                        ),
                                        style = MaterialTheme.typography.bodySmall
                                    )

                                }
                                Text(
                                    text = "${ceil(leg.gasL).toInt()} L",
                                    style = MaterialTheme.typography.bodyLarge
                                )
                            }

                            is ConsumptionCalculator.Leg.LevelLeg -> {
                                Column {
                                    Text(
                                        "${idx + 1}: Level @ ${leg.depthM} m",
                                        style = MaterialTheme.typography.bodyLarge
                                    )

                                    Text(
                                        String.format(
                                            Locale.getDefault(),
                                            "%.0f L x %.1f bar x %.1f min",
                                            sac, leg.ata, leg.minutes
                                        ),

                                        style = MaterialTheme.typography.bodySmall
                                    )

                                }
                                Text(
                                    text = "${ceil(leg.gasL).toInt()} L",
                                    style = MaterialTheme.typography.bodyLarge
                                )
                            }
                        }
                    }

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
                    Text("Total gas:", style = MaterialTheme.typography.titleMedium)
                    Text("$totalLitersUp L", style = MaterialTheme.typography.titleMedium)
                }

                Spacer(Modifier.height(8.dp))

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        text = "÷ %s L".format(
                            String.format(
                                Locale.getDefault(),
                                "%.1f",
                                cylL
                            )
                        ),
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray,
                        fontFamily = FontFamily.Monospace
                    )
                    Text(
                        text = "= %s bar".format(
                            String.format(
                                Locale.getDefault(),
                                "%.2f",
                                totalBarExact
                            )
                        ),
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
        }
    }
}