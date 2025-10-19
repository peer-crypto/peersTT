package com.example.ptt.ui.screens


import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.ptt.ui.components.CompactNumberField
import com.example.ptt.viewmodel.ConsumptionViewModel
import com.example.ptt.ui.input.toDoubleOrNullDe
import androidx.compose.ui.draw.alpha


@Composable
fun ConsumptionScreen(
    onBack: () -> Unit,
    onShowResult: () -> Unit,
    vm: com.example.ptt.viewmodel.ConsumptionViewModel
) {

    var nextDepth by remember(vm.levels.size) {
        mutableStateOf(vm.defaultDepthStrOrEmpty())
    }
    var nextMinutes by remember(vm.levels.size) {
        mutableStateOf(vm.defaultMinutesStrOrEmpty())
    }
    var reject: ConsumptionViewModel.Fit.Rejected? by remember { mutableStateOf(null) }
    val isError = reject is ConsumptionViewModel.Fit.Rejected



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
            Text("Consumption Calculator", style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.width(48.dp))
        }

        Spacer(Modifier.height(12.dp))

        // Fülldruck
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "Filling pressure (bar)",
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.width(140.dp)
            )
            CompactNumberField(
                value = vm.fillingPressureBar,
                onValueChange = { vm.fillingPressureBar = it }
            )
        }

        Spacer(Modifier.height(18.dp))

        Text("Level", style = MaterialTheme.typography.titleMedium)


//  Bereits hinzugefügte Stops
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {

            vm.levels.forEachIndexed { i, lvl ->

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth(),
                )

                {

                    Spacer(Modifier.width(75.dp))

                    // Depht Column
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.width(100.dp)
                    ) {
                        Text(
                            "Depth (m)",
                            style = MaterialTheme.typography.labelSmall,
                            color = if (isError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface
                        )

                        CompactNumberField(
                            value = lvl.depthM.toString(),
                            onValueChange = {},
                        )
                    }

                    // Minutes Column
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.width(100.dp)
                    ) {
                        Text(
                            "Minutes",
                            style = MaterialTheme.typography.labelSmall,
                            color = if (isError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface

                        )
                        CompactNumberField(
                            value = lvl.durationMin.toString(),
                            onValueChange = {},
                        )
                    }
                    IconButton(onClick = { vm.removeLevel(i) }) {
                        Icon(
                            imageVector = Icons.Filled.Delete,
                            contentDescription = "Remove stop"
                        )
                    }
                }
            }
        }

// 2) Eingabezeile
        Spacer(Modifier.height(8.dp))
        val isError = reject is ConsumptionViewModel.Fit.Rejected

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            Spacer(Modifier.width(75.dp))

            // Depth column
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.width(100.dp)
            ) {
                Text(
                    "Depth (m)",
                    style = MaterialTheme.typography.labelSmall,
                    color = if (isError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface
                )
                CompactNumberField(
                    value = nextDepth,
                    onValueChange = { nextDepth = it; reject = null },

                )
            }

            Spacer(Modifier.width(5.dp)) // Abstand zwischen Depth und Minutes

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.width(100.dp)
            ) {
                Text(
                    "Minutes",
                    style = MaterialTheme.typography.labelSmall,
                    color = if (isError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface
                )
                CompactNumberField(
                    value = nextMinutes,
                    onValueChange = { nextMinutes = it; reject = null },
                )
            }

            // Dummy, um gleiche Breite zu erreichen
            IconButton(
                onClick = { /* no-op */ },
                enabled = false,
                modifier = Modifier
                    .size(48.dp)   // gleiche Touch-Target-Breite wie „echter“ Papierkorb
                    .alpha(0f)     // unsichtbar
            ) {
                Icon(Icons.Filled.Delete, contentDescription = null)
            }

        }
// 3) Add-Button (unter der Eingabezeile)
        Spacer(Modifier.height(8.dp))
        Button(
            enabled = vm.settingsSnapshot != null,
            onClick = {
                val d = nextDepth.toDoubleOrNullDe()
                val t = nextMinutes.toDoubleOrNullDe()
                if (d == null || t == null) {
                    reject = ConsumptionViewModel.Fit.Rejected;
                    return@Button
                }
                when (vm.canAddAnotherLevel(d, t)) {
                    ConsumptionViewModel.Fit.Full -> {
                        vm.addLevel(d, t)
                        reject = null
                    }

                    ConsumptionViewModel.Fit.Rejected -> reject =
                        ConsumptionViewModel.Fit.Rejected
                }
            }
        ) { Text("Add level") }

// Calculate

        Spacer(Modifier.height(12.dp))
        Button(
            enabled = vm.settingsSnapshot != null && vm.levels.isNotEmpty(),
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                if (vm.buildAndSummarize()) {
                    onShowResult()
                } else {
                    reject = ConsumptionViewModel.Fit.Rejected
                }
            }
        ) { Text("Calculate") }
    }
}

