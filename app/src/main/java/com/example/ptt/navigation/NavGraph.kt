package com.example.ptt.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.ptt.ui.screens.*
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ptt.viewmodel.HeJumpViewModel


sealed class Route(val path: String) {
    data object Home : Route("home")
    data object Deepstop : Route("deepstop")
    data object Rockbottom : Route("rockbottom")
    data object RockbottomResult : Route("rockbottom_result")
    data object RockbottomDetails : Route("rockbottom_details")

    data object Consumption : Route("consumption")
    data object ConsumptionResult : Route("consumption_result")

    data object ConsumptionDetails : Route("consumption_details")

    data object HeJump: Route("hejump")

    data object HeJumpDetails : Route("hejump_details")

    data object Settings : Route("settings")
}

@Composable
fun AppNavGraph(nav: NavHostController) {

    NavHost(navController = nav, startDestination = Route.Home.path) {

        composable(Route.Home.path) {
            HomeScreen(
                onDeepstop = { nav.navigate(Route.Deepstop.path) },
                onConsumption = { nav.navigate(Route.Consumption.path) },
                onRockbottom = { nav.navigate(Route.Rockbottom.path) },
                onHeJump = { nav.navigate(Route.HeJump.path) },
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

            )
        }

        composable(Route.Settings.path) {
            SettingsScreen(
                onBack = { nav.popBackStack() },

            )
        }
        composable(Route.Consumption.path) { backStackEntry ->
            val vm: com.example.ptt.viewmodel.ConsumptionViewModel =
                viewModel(backStackEntry)
            ConsumptionScreen(
                vm = vm,
                onBack = { nav.popBackStack() },
                onShowResult = { nav.navigate(Route.ConsumptionResult.path) }
            )
        }

        composable(Route.ConsumptionResult.path) {
            val owner = nav.getBackStackEntry(Route.Consumption.path)
            val vm: com.example.ptt.viewmodel.ConsumptionViewModel =
                viewModel(owner)
            ConsumptionResultScreen(
                vm = vm,
                onBack = { nav.popBackStack() },
                onShowDetails = { nav.navigate(Route.ConsumptionDetails.path) }
            )
        }

        composable(Route.ConsumptionDetails.path) {
            val owner = nav.getBackStackEntry(Route.Consumption.path)
            val vm: com.example.ptt.viewmodel.ConsumptionViewModel =
                viewModel(owner)
            ConsumptionDetailsScreen(
                vm = vm,
                onBack = { nav.popBackStack() }
            )
        }

        composable(Route.HeJump.path) { backStackEntry ->
            val vm: com.example.ptt.viewmodel.HeJumpViewModel =
                viewModel(backStackEntry)
                HeJumpScreen(
                vm = vm,
                onBack = { nav.popBackStack() },
                onShowDetails = { nav.navigate(Route.HeJumpDetails.path) },
            )
        }

        // Details sieht dieselbe VM-Instanz: über das HeJump-BackStackEntry scopen
        composable(Route.HeJumpDetails.path) { backStackEntry ->
            val parentEntry = remember(backStackEntry) { nav.getBackStackEntry(Route.HeJump.path) }
            val vm: HeJumpViewModel = viewModel(parentEntry)

            HeJumpDetailsScreen(
                onBack = { nav.popBackStack() },
                // wenn du buildDetailsPayload() hast:
                result = vm.result,            // oder: result = vm.buildDetailsPayload()
                altRec = vm.altRec,            // 👈 NEU: Alternative reinschieben
                onApplyRecommendation = { o2Pct, hePct ->
                    vm.applyToMixAndCalculate(o2Pct, hePct)
                    nav.popBackStack() // zurück in den HeJumpScreen
                }
            )
        }


    }
}
