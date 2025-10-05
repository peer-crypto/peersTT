package com.example.ptt.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ptt.viewmodel.RockbottomViewModel
import androidx.navigation.NavHostController
import androidx.compose.runtime.remember
import com.example.ptt.navigation.Routes


@Composable
fun RockbottomResultScreen(
    onBack: () -> Unit,
    nav: NavHostController, // NavController reinreichen
) {

    // shared VM vom RockBottom-Backstack holen
    val parentEntry = remember(nav) { nav.getBackStackEntry(Routes.ROCKBOTTOM) }
    val vm: RockbottomViewModel = viewModel(parentEntry)

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
            Text("RockBottom – Result", style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.width(48.dp))
        }

        Spacer(Modifier.height(16.dp))
        HorizontalDivider()
        Spacer(Modifier.height(16.dp))

        if (vm.calcGasL != null && vm.calcBar != null) {
            Text("Gas until first switch",
                style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(4.dp))
            Text("${vm.calcGasL} L",
                style = MaterialTheme.typography.headlineMedium)

            Spacer(Modifier.height(16.dp))

            Text("Required cylinder pressure",
                style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(4.dp))
            Text("${vm.calcBar} bar",
                style = MaterialTheme.typography.headlineMedium)

            // Platzhalter: Hier später „Herleitung/Segments“ einblenden
            Spacer(Modifier.height(24.dp))
            Text("Details will follow (derivation & segments)",
                style = MaterialTheme.typography.bodySmall)
        } else {
            Text("No result available. Please go back and calculate.",
                style = MaterialTheme.typography.bodyMedium)
        }
    }
}
