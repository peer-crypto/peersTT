package com.example.ptt.ui.screens

import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.runtime.*
import androidx.compose.material3.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.ptt.domain.settings.SettingsRepository
import com.example.ptt.ui.components.CompactNumberField





@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    nav: NavHostController
) {
    val settings by SettingsRepository.settings.collectAsState()
    val back = LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher
    val focus = LocalFocusManager.current


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .navigationBarsPadding(), // Inhalt kollidiert nicht mit Systemleiste
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally,

    ) {
        // Header
        Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
            TextButton(onClick = onBack) { Text("‹ Back") }
            Text("Settings", style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.width(48.dp))
        }

        Spacer(Modifier.height(16.dp))

        // SAC per diver (Double)
        SettingRowDouble(
            label = "SAC per diver (L/min)",
            value = settings.sacPerDiver
        ) { v -> SettingsRepository.update { it.copy(sacPerDiver = v) } }

        Spacer(Modifier.height(16.dp))

        // Cylinder (Int)
        SettingRowInt(
            label = "Cylinder (L)",
            value = settings.cylinderL
        ) { v -> SettingsRepository.update { it.copy(cylinderL = v) } }

        Spacer(Modifier.height(16.dp))

        // Stress factor (Double)
        SettingRowDouble(
            label = "Stress factor",
            value = settings.stressFactor
        ) { v -> SettingsRepository.update { it.copy(stressFactor = v) } }

        Spacer(Modifier.height(16.dp))

        // Delay at bottom (Int)
        SettingRowInt(
            label = "Delay at bottom (min)",
            value = settings.delayMin
        ) { v -> SettingsRepository.update { it.copy(delayMin = v) } }

        Spacer(Modifier.height(16.dp))

        // Ascent rate
        SettingRowInt(
            label = "Ascent rate (m/min)",
            value = settings.ascentRateMpm
        ) { v -> SettingsRepository.update { it.copy(ascentRateMpm = v) } }

        Spacer(Modifier.height(16.dp))

        // Descent rate
        SettingRowInt(
            label = "Descent rate (m/min)",
            value = settings.descentRateMpm
        ) { v -> SettingsRepository.update { it.copy(descentRateMpm = v) } }


        Spacer(modifier = Modifier.height(32.dp))   //  schiebt Button nach unten

        Button(
            onClick = { focus.clearFocus(); onBack() },
            modifier = Modifier.width(120.dp).height(48.dp),
                    colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF0072C5),
            contentColor = Color.White
        ),
        ) { Text("Done") }
    }
}


/* ---------- Helfer-Composables ---------- */

@Composable
private fun SettingRowDouble(
    label: String,
    value: Double,
    onValidChange: (Double) -> Unit
) {
    // lokaler Text-State, initial aus Settings
    var text by rememberSaveable(value) { mutableStateOf(value.toString()) }
    // wenn Settings extern geändert wurden, Text spiegeln
    LaunchedEffect(value) { text = value.toString() }

    LabeledNumberRow(
        label = label,
        value = text,
        onValueChange = { newText ->
            text = newText
            val normalized = newText.replace(',', '.')
            normalized.toDoubleOrNull()?.let(onValidChange)
        }
    )
}

@Composable
private fun SettingRowInt(
    label: String,
    value: Int,
    onValidChange: (Int) -> Unit
) {
    var text by rememberSaveable(value) { mutableStateOf(value.toString()) }
    LaunchedEffect(value) { text = value.toString() }

    LabeledNumberRow(
        label = label,
        value = text,
        onValueChange = { newText ->
            text = newText
            newText.toIntOrNull()?.let(onValidChange)
        }
    )
}

@Composable
private fun LabeledNumberRow(
    label: String,
    value: String,
    onValueChange: (String) -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            label,
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier.width(180.dp) // etwas mehr Platz für längere Labels
        )
        CompactNumberField(
            value = value,
            onValueChange = onValueChange
        )
    }


}



