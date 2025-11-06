package com.example.ptt.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ptt.ui.components.DepthDropdown
import com.example.ptt.viewmodel.DeepstopViewModel


@Composable
fun DeepstopScreen(onBack: () -> Unit, vm: DeepstopViewModel = viewModel()) {
    val selectedDepth = vm.selectedDepth
    val result = vm.result


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
        }


        Spacer(Modifier.height(16.dp))


        Text("Deepstop Calculator", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(20.dp))


        Text("Maximum Depth")
        DepthDropdown(
            labelBuilder = { v: Int -> "$v m" },
            options = vm.options,
            selected = selectedDepth,
            onSelected = vm::onDepthSelected
        )


        Spacer(Modifier.height(32.dp))


        Text("Deepstop: ${result.deepstop} m", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(8.dp))
        Text("Powered by Tom's Theorem 😊:", style = MaterialTheme.typography.bodySmall)
        Text("max pressure ÷ 1.35 = stop pressure", style = MaterialTheme.typography.bodySmall)
    }
}