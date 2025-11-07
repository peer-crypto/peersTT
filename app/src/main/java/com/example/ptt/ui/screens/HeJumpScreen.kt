package com.example.ptt.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.ptt.domain.model.HeJumpViolation
import com.example.ptt.viewmodel.HeJumpViewModel
import com.example.ptt.ui.components.HeJumpGasPickerManual

@Composable
fun HeJumpScreen(
    onBack: () -> Unit,
    onShowDetails: () -> Unit,
    vm: HeJumpViewModel
)

{

    val fromO2 = vm.fromO2Pct.toIntOrNull()
    val fromHe = vm.fromHePct.toIntOrNull()
    val toO2   = vm.toO2Pct.toIntOrNull()
    val toHe   = vm.toHePct.toIntOrNull()

    fun validPair(o: Int?, h: Int?) =
        o != null && h != null && o in 0..100 && h in 0..100 && (o + h) <= 100

    val inputsValid = validPair(fromO2, fromHe) && validPair(toO2, toHe)

    val hasLeanToHeavy: Boolean =
        vm.result?.violations?.contains(HeJumpViolation.LeanToHeavy) == true

    val hasOneFifthViolation: Boolean =
        vm.result?.violations?.contains(HeJumpViolation.OneFifthRule) == true

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .navigationBarsPadding()
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(onClick = onBack) { Text("‹ Back") }
            Text("He-Jump Calculator", style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.width(48.dp))
        }

        Spacer(Modifier.height(16.dp))

        HeJumpGasPickerManual(
            title = "From Gas",
            o2PctStr = vm.fromO2Pct,
            hePctStr = vm.fromHePct,
            onO2Change = { vm.updateFrom(o2Pct = it) },
            onHeChange = { vm.updateFrom(hePct = it) }
        )

        Spacer(Modifier.height(16.dp))

        HeJumpGasPickerManual(
            title = "To Gas",
            o2PctStr = vm.toO2Pct,
            hePctStr = vm.toHePct,
            onO2Change = { vm.updateTo(o2Pct = it) },
            onHeChange = { vm.updateTo(hePct = it) }
        )


        Spacer(Modifier.height(24.dp))

        // Calculate: aktiv nur wenn Eingaben geändert wurden / noch nie gerechnet

            Button(
                enabled = vm.needsRecalc && inputsValid,
                onClick = { vm.calculate() },
                modifier = Modifier.fillMaxWidth()
            ) { Text("Calculate") }


            Spacer(Modifier.height(16.dp))

            // Ergebnis nur zeigen, wenn vorhanden
            Box(
                modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                vm.result?.let { r ->

                    val statusText: String
                    val statusColor: Color

                    when {
                        hasLeanToHeavy -> {
                            statusText =
                                "❌ Never switch from a helium-lean gas to a helium-rich gas during ascent!"
                            statusColor = MaterialTheme.colorScheme.error
                        }

                        hasOneFifthViolation -> {
                            statusText = "⚠️ Jump violates 1/5 rule"
                            statusColor = MaterialTheme.colorScheme.error
                        }

                        else -> {
                            statusText = "✅ Jump within 1/5 rule"
                            statusColor = MaterialTheme.colorScheme.primary
                        }
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = statusText,
                            color = statusColor,
                            style = MaterialTheme.typography.titleMedium,
                            textAlign = TextAlign.Center
                        )
                    }
                }

            }
            Spacer(Modifier.height(12.dp))

            // Details-Button: aktiv nur bei aktuellem Ergebnis (needsRecalc == false)

            Row(
                modifier = Modifier.align(Alignment.End),
                // horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally)
            ) {
                Button(
                    onClick = { onShowDetails() },
                    enabled = vm.result != null &&
                            !vm.needsRecalc &&
                            !hasLeanToHeavy,   // 🔹 deaktiviert bei gefährlichem Switch

                ) {
                    Text("Show details")
                }
            }
        }
    }