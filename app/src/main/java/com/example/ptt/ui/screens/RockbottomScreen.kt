package com.example.ptt.ui.screens

import com.example.ptt.ui.components.CompactNumberField
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ptt.viewmodel.RockbottomViewModel
import androidx.activity.compose.LocalActivity
import androidx.activity.ComponentActivity

@Composable

fun RockbottomScreen(
    onBack: () -> Unit,
    onShowResult: () -> Unit)
    {

    val activity = LocalActivity.current as ComponentActivity
    val vm: RockbottomViewModel = viewModel(activity)

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
            Text("RockBottom-Calculator", style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.width(48.dp))
        }



        Spacer(Modifier.height(12.dp))

        // Bottom depth
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "Bottom depth (m)",
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.width(120.dp)
            )
            CompactNumberField(
                value = vm.depthM,
                onValueChange = { vm.depthM = it }
            )
        }

        Spacer(Modifier.height(12.dp))

        // Switch depth
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "Switch depth (m)",
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.width(120.dp)
            )
            CompactNumberField(
                value = vm.switchDepthM,
                onValueChange = { vm.switchDepthM = it }
            )
        }

        Spacer(Modifier.height(15.dp))

        // Deco stops
        Text("Decostops before switch (ascending):", style = MaterialTheme.typography.labelLarge)
        Spacer(Modifier.height(8.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f, fill = false)
        ) {
            itemsIndexed(vm.decoStops) { index, stop ->

                val depthInvalid = !vm.isStopDepthValidAt(index)
                val minutesInvalid = !vm.isStopMinutesValidAt(index)

                Row(
                    verticalAlignment = Alignment.Top,
                    horizontalArrangement = Arrangement.Center,   // Inhalte mittig ausrichten
                    modifier = Modifier.fillMaxWidth()
                ) {

                    Spacer(Modifier.width(75.dp))

                    // Depth colum
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.width(100.dp)
                    ) {
                        Text("Depth (m)",
                            style = MaterialTheme.typography.labelSmall,
                            color = if (depthInvalid) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface
                        )
                        CompactNumberField(
                            value = stop.depthM,
                            onValueChange = { vm.updateDecoStopDepth(index, it) }
                        )
                    }

                    Spacer(Modifier.width(5.dp)) // Abstand zwischen Depth und Minutes

                    // Minutes column
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.width(100.dp)
                    ) {
                        Text("Minutes",
                            style = MaterialTheme.typography.labelSmall,
                                    color = if (minutesInvalid) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface
                        )
                        CompactNumberField(
                            value = stop.minutes,
                            onValueChange = { vm.updateDecoStopMinutes(index, it) }
                        )
                    }

                    //Spacer(Modifier.width(12.dp)) // Abstand zum Delete-Button
                    IconButton(onClick = { vm.removeDecoStop(index) },
                        modifier = Modifier.requiredSize(56.dp)) {
                        Icon(Icons.Default.Delete,
                            modifier = Modifier.size(20.dp),
                            contentDescription = "Remove stop")
                    }
                }
            }
        }

        Spacer(Modifier.height(8.dp))

// Add-Stop Button nur anzeigen, wenn < 7 Stops
        if (vm.canAddAnotherStop()) {
            Button(onClick = { vm.addDecoStop() }) { Text("Add stop") }
        }

        Spacer(Modifier.height(16.dp))

// Calculate Button
        Button(
            onClick = {
                vm.calculateRockbottom()
                if (vm.calcGasL != null && vm.calcBar != null) onShowResult()
            },
            enabled = !vm.hasInvalidStops,
            modifier = Modifier.fillMaxWidth()
        ) { Text("Calculate") }



    }
}

