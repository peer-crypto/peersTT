package com.example.ptt.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun HomeScreen(
    onDeepstop: () -> Unit,
    onRockbottom: () -> Unit,
    onSettings: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .navigationBarsPadding(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,

    ) {
        Text("Peer's TEC-Tools", style = MaterialTheme.typography.headlineLarge)

        Spacer(Modifier.height(75.dp))

        OutlinedButton(
            onClick = onDeepstop,
            modifier = Modifier.width(240.dp).height(56.dp)
        ) { Text("Deepstop Calculator") }

        Spacer(Modifier.height(16.dp))

        OutlinedButton(
            onClick = onRockbottom,
            modifier = Modifier.width(240.dp).height(56.dp)
        ) { Text("Rockbottom Calculator") }


        Spacer(modifier = Modifier.weight(1f))   //  schiebt Button nach unten

        OutlinedButton(
            onClick = onSettings,
                colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF0072C5),
                contentColor = Color.White
            ),
            modifier = Modifier.width(120.dp).height(48.dp)
        ) { Text("Settings") }

    }
}
