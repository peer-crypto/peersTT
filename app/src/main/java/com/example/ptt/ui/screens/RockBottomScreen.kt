package com.example.ptt.ui.screens


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
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
import com.example.ptt.viewmodel.RockBottomViewModel
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType

@Composable
fun RockBottomScreen(onBack: () -> Unit, vm: RockBottomViewModel = viewModel()) {
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
            Text("RockBottom-Calculator", style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.width(48.dp))
        }

        Spacer(Modifier.height(16.dp))

        // SAC pro Taucher (inkl. Stressfaktor, vom Nutzer bestimmt)
        Text("SAC per diver (l/min @ 1 ata)", style = MaterialTheme.typography.labelLarge)
        OutlinedTextField(
            value = vm.sacPerDiver,
            onValueChange = { input ->
                // Nur Zahlen oder leer erlauben
                if (input.isEmpty() || input.all { it.isDigit() }) vm.sacPerDiver = input
            },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.width(240.dp)
        )

        Spacer(Modifier.height(5.dp))

        Text("Team-SAC: ${vm.sacTeamLpm} l/min", style = MaterialTheme.typography.bodyMedium)

        // Cylinder size (L)
        Spacer(Modifier.height(12.dp))
        Text("Cylinder size (L)", style = MaterialTheme.typography.labelLarge)
        OutlinedTextField(
            value = vm.cylinderL,
            onValueChange = { input ->
                if (input.isEmpty() || input.all { it.isDigit() }) vm.cylinderL = input
            },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.width(240.dp)
        )

// Depth (m)
        Spacer(Modifier.height(12.dp))
        Text("Depth (m)", style = MaterialTheme.typography.labelLarge)
        OutlinedTextField(
            value = vm.depthM,
            onValueChange = { input ->
                if (input.isEmpty() || input.all { it.isDigit() }) vm.depthM = input
            },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.width(240.dp)
        )

// Switch depth (m)
        Spacer(Modifier.height(12.dp))
        Text("Gasswitch depth (m)", style = MaterialTheme.typography.labelLarge)
        OutlinedTextField(
            value = vm.switchDepthM,
            onValueChange = { input ->
                if (input.isEmpty() || input.all { it.isDigit() }) vm.switchDepthM = input
            },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.width(240.dp)
        )
        Text("Decostops before switch", style = MaterialTheme.typography.labelLarge)
        Spacer(Modifier.height(8.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            itemsIndexed(vm.decoStops) { index, stop ->
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Depth
                    OutlinedTextField(
                        value = stop.depthM,
                        onValueChange = { vm.updateDecoStopDepth(index, it) },
                        label = { Text("Depth (m)") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        isError = stop.depthM.isNotEmpty() && !vm.isStopInRange(stop.depthM),
                        modifier = Modifier.width(140.dp)
                    )
                    // Minutes
                    OutlinedTextField(
                        value = stop.minutes,
                        onValueChange = { vm.updateDecoStopMinutes(index, it) },
                        label = { Text("Minutes") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.width(120.dp)
                    )
                    // Remove
                    IconButton(
                        onClick = { vm.removeDecoStop(index) }
                    ) { Icon(Icons.Default.Delete, contentDescription = "Remove stop") }
                }
            }
        }

        Spacer(Modifier.height(8.dp))
        Button(onClick = { vm.addDecoStop() }) { Text("Add stop") }

    }
}
