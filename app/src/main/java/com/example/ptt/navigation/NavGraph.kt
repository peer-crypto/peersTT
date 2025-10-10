package com.example.ptt.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.ptt.ui.screens.DeepstopScreen
import com.example.ptt.ui.screens.HomeScreen
import com.example.ptt.ui.screens.RockbottomScreen
import com.example.ptt.ui.screens.RockbottomResultScreen
import com.example.ptt.ui.screens.RockbottomDetailsScreen
import com.example.ptt.ui.screens.SettingsScreen

sealed class Route(val path: String) {
    data object Home : Route("home")
    data object Deepstop : Route("deepstop")
    data object Rockbottom : Route("rockbottom")
    data object RockbottomResult : Route("rockbottom_result")
    data object RockbottomDetails : Route("rockbottom_details")
    data object Settings : Route("settings")
}



@Composable
fun AppNavGraph(nav: NavHostController) {
    NavHost(navController = nav, startDestination = Route.Home.path) {
        composable(Route.Home.path) {
            HomeScreen(
                onDeepstop = { nav.navigate(Route.Deepstop.path) },
                onRockbottom = { nav.navigate(Route.Rockbottom.path) },
                onSettings = { nav.navigate(Route.Settings.path) }
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

        composable(Route.RockbottomDetails.path) {
            RockbottomDetailsScreen(
                onBack = { nav.popBackStack() },
                nav = nav
            )
        }

        composable(Route.Settings.path) {
            SettingsScreen(
                onBack = { nav.popBackStack() },
                nav = nav
            )
        }

    }
}