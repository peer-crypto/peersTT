package com.example.ptt.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp


@Composable

fun PercentDropdownColumn(
    label: String,
    valuePct: Int,
    onChange: (Int) -> Unit,
    options: List<Int>,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.width(200.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(label, style = MaterialTheme.typography.labelSmall)
        Spacer(Modifier.height(4.dp))
        DepthDropdown(
            labelBuilder = { v: Int -> "$v %" },
            options = options,
            selected = valuePct,
            onSelected = onChange,
            modifier = Modifier.height(60.dp)
        )
    }
}
