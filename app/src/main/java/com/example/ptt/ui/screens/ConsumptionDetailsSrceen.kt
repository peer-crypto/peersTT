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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import com.example.ptt.domain.ConsumptionCalculator
import kotlin.math.ceil
import com.example.ptt.ui.format.*
import java.util.Locale

@Composable
fun ConsumptionDetailsScreen(
    vm: com.example.ptt.viewmodel.ConsumptionViewModel,
    onBack: () -> Unit

) {

    val summary = vm.lastSummary

    val model = vm.lastBuiltModel ?: run {
        // kurze Info und raus
        Column(
            Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text("Keine Berechnung vorhanden. Bitte zuerst Calculate im Consumption-Screen.")
        }
        return
    }

    val details = ConsumptionCalculator.deriveDetails(model)

    if (summary == null) {
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
                        Icons.AutoMirrored.Filled.ArrowBack,
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
        val startBar = model?.startBar ?: 0.0


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
            text = "Filling pressure: $startBar bar",
            style = MaterialTheme.typography.bodySmall
        )

        Spacer(Modifier.height(16.dp))

        val summary = vm.lastSummary
        val model = vm.lastBuiltModel

        val usedLiters = summary?.usedLiters ?: 0.0
        val usedBarExact = summary?.usedBar ?: 0.0
        val remainingBar = summary?.remainingBar ?: 0.0
        val cylL = model?.settings?.cylinderVolumeL ?: 1.0


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
                                val direction = if (leg.toM < leg.fromM) "Asc" else "Desc"

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

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.Start
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Total gas:", style = MaterialTheme.typography.titleMedium)
                        Text("${fmt0(usedLiters)} L", style = MaterialTheme.typography.titleMedium)
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Remaining bar:", style = MaterialTheme.typography.titleMedium)
                        Text(
                            "${fmt0(remainingBar)} bar",
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                }

                Spacer(Modifier.height(8.dp))

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.End
                ) {
                    // 1) Liter → bar
                    Text(
                        text = "${fmt0(usedLiters)} L ÷ ${fmt1(cylL)} L = ${fmt0(usedBarExact)} bar",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray,
                        fontFamily = FontFamily.Monospace
                    )

                    // 2) Startdruck – Verbrauch = Restdruck
                    Text(
                        text = "${fmt0(startBar)} bar − ${fmt0(usedBarExact)} bar = ${
                            fmt0(
                                remainingBar
                            )
                        } bar",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray,
                        fontFamily = FontFamily.Monospace
                    )
                }

            }
        }
    }
}