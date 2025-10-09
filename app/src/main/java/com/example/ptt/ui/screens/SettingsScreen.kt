package com.example.ptt.ui.screens

import CompactNumberField
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ptt.viewmodel.RockbottomViewModel
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivity



@Composable
fun SettingsScreen(
    onBack: () -> Unit,
)

{
    val activity = LocalActivity.current as ComponentActivity
    val vm: RockbottomViewModel = viewModel(activity)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
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
            Text("Settings", style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.width(48.dp))
        }

        Spacer(Modifier.height(16.dp))

        // SAC per diver
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "SAC per diver (L/min)",
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.width(120.dp)
            )  // feste Breite für Label
            CompactNumberField(
                value = vm.sacPerDiver,
                onValueChange = { vm.sacPerDiver = it }
            )
        }

        Spacer(Modifier.height(16.dp))

        // Cylinder size
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "Cylinder (L)",
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.width(120.dp)
            )
            CompactNumberField(
                value = vm.cylinderL,
                onValueChange = { vm.cylinderL = it }
            )
        }

        Spacer(Modifier.height(16.dp))

      //Stress Faktor
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "Stress factor",
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.width(120.dp)
            )
            CompactNumberField(
                value = vm.stressFactor,
                onValueChange = { vm.stressFactor = it }
            )
        }

        Spacer(Modifier.height(16.dp))

        //delay
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "Delay at bottom (min)",
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.width(120.dp)
            )
            CompactNumberField(
                value = vm.delayMin,
                onValueChange = { vm.delayMin = it }
            )
        }

        Spacer(Modifier.height(16.dp))

        //Ascent Rate
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "Ascent Rate (m/min)",
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.width(120.dp)
            )
            CompactNumberField(
                value = vm.ascentRateMpm,
                onValueChange = { vm.ascentRateMpm = it }
            )
        }

    }
}