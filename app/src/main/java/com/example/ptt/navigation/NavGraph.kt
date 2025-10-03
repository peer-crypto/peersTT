package com.example.ptt.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.ptt.ui.screens.DeepstopScreen
import com.example.ptt.ui.screens.HomeScreen
import com.example.ptt.ui.screens.RockBottomScreen
import com.example.ptt.ui.screens.RockBottomResultScreen

object Routes {
    const val HOME = "home"
    const val DEEPSTOP = "deepstop"
    const val ROCKBOTTOM = "rockbottom"

    const val ROCKBOTTOM_RESULT = "rockbottom_result"

}

@Composable
fun AppNavGraph(nav: NavHostController) {
    NavHost(navController = nav, startDestination = Routes.HOME) {
        composable(Routes.HOME) {
            HomeScreen(
                onDeepstop = { nav.navigate(Routes.DEEPSTOP) },
                onRockBottom = { nav.navigate(Routes.ROCKBOTTOM) }
            )
        }
        composable(Routes.DEEPSTOP) { DeepstopScreen(onBack = { nav.popBackStack() }) }

        composable(Routes.ROCKBOTTOM) {
            RockBottomScreen(
                onBack = { nav.popBackStack() },
                onShowResult = { nav.navigate(Routes.ROCKBOTTOM_RESULT) }
            )
        }
        composable(Routes.ROCKBOTTOM_RESULT) {
            RockBottomResultScreen(
                onBack = { nav.popBackStack() },
                nav = nav        )
        }
    }
}
