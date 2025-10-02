package com.example.ptt

import androidx.navigation.compose.rememberNavController
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.fillMaxSize
import com.example.ptt.navigation.AppNavGraph


@Composable
fun AppRoot() {
    val nav = rememberNavController()
    Surface(color = MaterialTheme.colorScheme.background, modifier = Modifier.fillMaxSize()) {
        AppNavGraph(nav)
    }
}
