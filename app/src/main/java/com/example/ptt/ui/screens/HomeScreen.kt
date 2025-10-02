package com.example.ptt.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp


@Composable
fun HomeScreen(onDeepstop: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Peer's TEC-Tools", style = MaterialTheme.typography.headlineLarge)
        Spacer(Modifier.height(50.dp))
        OutlinedButton(
            onClick = onDeepstop,
            modifier = Modifier.width(240.dp).height(56.dp)
        ) { Text("Deepstop Calculator") }

        //Test Push
    }
}