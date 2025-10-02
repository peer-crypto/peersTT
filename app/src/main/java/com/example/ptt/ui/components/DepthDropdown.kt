package com.example.ptt.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


@Composable
fun <T> DepthDropdown(
    labelBuilder: (T) -> String,
    options: List<T>,
    selected: T,
    onSelected: (T) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }


    Box(modifier = modifier) {
        OutlinedButton(
            onClick = { expanded = true },
            modifier = Modifier.width(200.dp).height(60.dp)
        ) { Text(labelBuilder(selected) + " ▾", fontSize = 25.sp) }


        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            options.forEach { value ->
                DropdownMenuItem(
                    text = { Text(labelBuilder(value)) },
                    onClick = {
                        onSelected(value)
                        expanded = false
                    }
                )
            }
        }
    }
}