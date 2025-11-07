package com.example.ptt.ui.components

import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun PercentField(
    label: String,
    valuePct: Int,
    onChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
    width: Dp = 120.dp
) {
    OutlinedTextField(
        value = valuePct.toString(),
        onValueChange = { raw ->
            val digits = raw.filter { it.isDigit() }
            val v = digits.toIntOrNull()?.coerceIn(0, 100) ?: 0
            onChange(v)
        },
        label = { Text(label) },
        singleLine = true,
        trailingIcon = { Text("%") },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        modifier = modifier.width(width)
    )
}
