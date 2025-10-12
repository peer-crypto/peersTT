package com.example.ptt.ui.screens

import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.material3.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.ptt.ui.components.CompactNumberField
import com.example.ptt.viewmodel.ConsumptionViewModel
import com.example.ptt.ui.input.toDoubleOrNullDe


@Composable
fun ConsumptionScreen(

    onBack: () -> Unit,
    //onShowResult: () -> Unit,
    nav: NavHostController
) {
    val activity = LocalActivity.current as ComponentActivity
    val vm: ConsumptionViewModel = viewModel(activity)

    var nextDepth by remember { mutableStateOf("") }
    var nextMinutes by remember { mutableStateOf("") }
    var reject: ConsumptionViewModel.Fit.Rejected? by remember { mutableStateOf(null) }
    val isError = reject is ConsumptionViewModel.Fit.Rejected


    // Lokaler UI-State NUR für die Add-Zeile

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .navigationBarsPadding(),
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

        // Fülldruck
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Filling pressure (bar)", style = MaterialTheme.typography.labelSmall, modifier = Modifier.width(140.dp))
            CompactNumberField(
                value = vm.fillingPressureBar,
                onValueChange = { vm.fillingPressureBar = it }
            )
        }

        Spacer(Modifier.height(12.dp))
        Text("Levels", style = MaterialTheme.typography.labelLarge)
        Spacer(Modifier.height(8.dp))

        // Liste: READ-ONLY + Delete
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f, fill = false)
        ) {
            itemsIndexed(vm.levels) { i, lvl ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("#${i + 1}  @ ${lvl.depthM} m • ${lvl.durationMin} min")
                    IconButton(onClick = { vm.removeLevel(i) }) {
                        Icon(Icons.Default.Delete, contentDescription = "Remove level")
                    }
                }
            }
        }

        HorizontalDivider(
            Modifier.padding(vertical = 8.dp),
            DividerDefaults.Thickness,
            DividerDefaults.color
        )

        // ADD-ZEILE UNTER DER LISTE
        Text("Add level", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(6.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedTextField(
                value = nextDepth,
                onValueChange = { nextDepth = it; reject = null },
                label = { Text("Depth (m)") },
                modifier = Modifier.weight(1f),
                isError = isError,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)

            )
            OutlinedTextField(
                value = nextMinutes,
                onValueChange = { nextMinutes = it; reject = null },
                label = { Text("Minutes") },
                modifier = Modifier.weight(1f),
                isError = isError,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
            Button(
                enabled = vm.settingsSnapshot != null,
                onClick = {
                    val d = nextDepth.toDoubleOrNullDe()
                    val t = nextMinutes.toDoubleOrNullDe()
                    if (d == null || t == null) {
                        reject = ConsumptionViewModel.Fit.Rejected  // <- nur Flag setzen
                        return@Button
                    }
                    when (vm.canAddAnotherLevel(d, t)) {
                        ConsumptionViewModel.Fit.Full -> {
                            vm.addLevel(d, t)
                            reject = null
                            // optional: Felder leeren
                            // nextDepth = ""; nextMinutes = ""
                        }
                        ConsumptionViewModel.Fit.Rejected -> {
                            reject = ConsumptionViewModel.Fit.Rejected // Outline rot, kein Text
                        }
                    }
                }
            ) { Text("Add") }
        }


        Spacer(Modifier.height(8.dp))
        // Optional: Calculate-Button etc. später
    }
}
