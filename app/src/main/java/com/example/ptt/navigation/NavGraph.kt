package com.example.ptt.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.ptt.ui.screens.DeepstopScreen
import com.example.ptt.ui.screens.HomeScreen
import com.example.ptt.ui.screens.RockbottomScreen
import com.example.ptt.ui.screens.RockbottomResultScreen

sealed class Route(val path: String) {
    data object Home : Route("home")
    data object Deepstop : Route("deepstop")
    data object Rockbottom : Route("rockbottom")
    data object RockbottomResult : Route("rockbottom_result")
}



@Composable
fun AppNavGraph(nav: NavHostController) {
    NavHost(navController = nav, startDestination = Route.Home.path) {
        composable(Route.Home.path) {
            HomeScreen(
                onDeepstop = { nav.navigate(Route.Deepstop.path) },
                onRockbottom = { nav.navigate(Route.Rockbottom.path) }
            )
        }
        composable(Route.Deepstop.path) {
            DeepstopScreen(onBack = { nav.popBackStack() })
        }
        composable(Route.Rockbottom.path) {
            RockbottomScreen(
                onBack = { nav.popBackStack() },
                onShowResult = { nav.navigate(Route.RockbottomResult.path) }
            )
        }
        composable(Route.RockbottomResult.path) {
            RockbottomResultScreen(
                onBack = { nav.popBackStack() },
                nav = nav
            )
        }
    }
}
