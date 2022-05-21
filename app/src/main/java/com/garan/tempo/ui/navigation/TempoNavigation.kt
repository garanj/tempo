package com.garan.tempo.ui.navigation

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.navArgument
import androidx.wear.compose.navigation.SwipeDismissableNavHost
import androidx.wear.compose.navigation.composable
import com.garan.tempo.TAG
import com.garan.tempo.TempoService
import com.garan.tempo.UiState
import com.garan.tempo.ui.model.MetricPickerViewModel
import com.garan.tempo.ui.screens.MetricPicker
import com.garan.tempo.ui.screens.PostWorkoutScreen
import com.garan.tempo.ui.screens.ScreenEditor
import com.garan.tempo.ui.screens.SettingsScreen
import com.garan.tempo.ui.screens.StartMenuScreen
import com.garan.tempo.ui.screens.WorkoutScreen
import com.garan.tempo.ui.screens.WorkoutSettingsScreen
import com.garan.tempo.ui.screens.preworkout.PreWorkoutScreen
import com.google.accompanist.pager.ExperimentalPagerApi

@ExperimentalPagerApi
@Composable
fun TempoNavigation(
    uiState: UiState,
    startDestination: String
) {
    SwipeDismissableNavHost(
        navController = uiState.navHostController,
        startDestination = startDestination
    ) {
        composable(Screen.START_MENU.route) {
            StartMenuScreen(
                uiState = uiState
            )
        }
        composable(Screen.PRE_WORKOUT.route + "/{settingsId}",
            arguments = listOf(
                navArgument("settingsId") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            PreWorkoutScreen(
                onStartNavigate = {
                    uiState.navHostController.popBackStack(Screen.START_MENU.route, true, false)
                    uiState.navHostController.navigate(Screen.WORKOUT.route)
                }
            )
        }
        composable(Screen.WORKOUT.route) { backStackEntry ->
            WorkoutScreen(
                onFinishNavigate = {
                    uiState.navHostController.popBackStack()
                    uiState.navHostController.navigate(Screen.POST_WORKOUT.route)
                }
            )
        }
        composable(Screen.POST_WORKOUT.route) {
            PostWorkoutScreen()
        }
        composable(Screen.SETTINGS.route) {
            SettingsScreen(
                uiState = uiState
            )
        }
        composable(
            Screen.WORKOUT_SETTINGS.route + "/{settingsId}",
            arguments = listOf(
                navArgument("settingsId") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val settingsId = backStackEntry.arguments?.getInt("settingsId")
            WorkoutSettingsScreen(
                onScreenButtonClick = {
                    uiState.navHostController.navigate(Screen.SCREEN_EDITOR.route + "/" + settingsId)
                }
            )
        }
        composable(
            Screen.SCREEN_EDITOR.route + "/{settingsId}",
            arguments = listOf(
                navArgument("settingsId") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val settingsId = backStackEntry.arguments?.getInt("settingsId")
            ScreenEditor(
                onConfigClick = { screen, slot ->
                    val route = Screen.METRIC_PICKER.route + "/$settingsId/$screen/$slot"
                    uiState.navHostController.navigate(route)
                }
            )
        }
        composable(
            Screen.METRIC_PICKER.route + "/{settingsId}/{screen}/{slot}",
            arguments = listOf(
                navArgument("settingsId") { type = NavType.IntType },
                navArgument("screen") { type = NavType.IntType },
                navArgument("slot") { type = NavType.IntType },
            )
        )
        { backStackEntry ->
            val settingsId = backStackEntry.arguments?.getInt("settingsId")!!
            val screen = backStackEntry.arguments?.getInt("screen")!!
            val slot = backStackEntry.arguments?.getInt("slot")!!
            val viewModel = hiltViewModel<MetricPickerViewModel>()
            MetricPicker(
                onClick = { metric ->
                    viewModel.setMetric(settingsId, screen, slot, metric)
                    uiState.navHostController.popBackStack()
                }
            )
        }
    }
}
