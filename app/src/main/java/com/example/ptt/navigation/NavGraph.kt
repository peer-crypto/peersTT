package com.example.ptt.navigation


import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.NavHostController
import com.example.ptt.ui.screens.DeepstopScreen
import com.example.ptt.ui.screens.HomeScreen


object Routes {
    const val HOME = "home"
    const val DEEPSTOP = "deepstop"
}


@Composable
fun AppNavGraph(nav: NavHostController) {
    NavHost(navController = nav, startDestination = Routes.HOME) {
        composable(Routes.HOME) {
            HomeScreen(onDeepstop = { nav.navigate(Routes.DEEPSTOP) })
        }
        composable(Routes.DEEPSTOP) {
            DeepstopScreen(onBack = { nav.popBackStack() })
        }
    }
}
