package com.garan.tempo.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.navArgument
import androidx.wear.compose.material.ScalingLazyListState
import androidx.wear.compose.material.TimeText
import androidx.wear.compose.navigation.composable
import com.garan.tempo.data.SavedExercise
import com.garan.tempo.settings.ExerciseSettingsWithScreens
import com.garan.tempo.settings.TempoSettings
import com.garan.tempo.settings.Units
import com.garan.tempo.ui.screens.metricpicker.MetricPicker
import com.garan.tempo.ui.screens.metricpicker.MetricPickerViewModel
import com.garan.tempo.ui.screens.postworkout.PostWorkoutScreen
import com.garan.tempo.ui.screens.postworkout.PostWorkoutViewModel
import com.garan.tempo.ui.screens.preworkout.PreWorkoutPermissionCheck
import com.garan.tempo.ui.screens.preworkout.PreWorkoutViewModel
import com.garan.tempo.ui.screens.screeneditor.ScreenEditor
import com.garan.tempo.ui.screens.screenformat.ScreenFormatScreen
import com.garan.tempo.ui.screens.screenformat.ScreenFormatViewModel
import com.garan.tempo.ui.screens.settings.SettingsScreen
import com.garan.tempo.ui.screens.settings.SettingsViewModel
import com.garan.tempo.ui.screens.startmenu.StartMenuScreen
import com.garan.tempo.ui.screens.startmenu.StartMenuViewModel
import com.garan.tempo.ui.screens.workout.WorkoutScreen
import com.garan.tempo.ui.screens.workoutsettings.WorkoutSettingsScreen
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.android.horologist.compose.navscaffold.WearNavScaffold
import com.google.android.horologist.compose.navscaffold.scalingLazyColumnComposable


@ExperimentalPagerApi
@Composable
fun TempoScaffold(
    startDestination: String,
    navController: NavHostController,
    timeText: @Composable (Modifier) -> Unit = {
        TimeText(modifier = it)
    },
) {
    WearNavScaffold(
        navController = navController,
        startDestination = startDestination,
        timeText = timeText
    ) {
        scalingLazyColumnComposable(
            Screen.START_MENU.route,
            scrollStateBuilder = { ScalingLazyListState() }
        ) {
            val viewModel = hiltViewModel<StartMenuViewModel>()
            val exerciseSettings by viewModel.exerciseSettings.collectAsState(listOf())
            StartMenuScreen(
                exerciseSettings = exerciseSettings,
                onPreWorkoutClick = { id ->
                    navController.navigate(Screen.PRE_WORKOUT.route + "/" + id)
                },
                onSettingsClick = {
                    navController.navigate(Screen.SETTINGS.route)
                },
                scrollState = it.scrollableState
            )
        }
        composable(Screen.PRE_WORKOUT.route + "/{settingsId}",
            arguments = listOf(
                navArgument("settingsId") { type = NavType.IntType }
            )
        ) {
            val viewModel = hiltViewModel<PreWorkoutViewModel>()
            val exerciseSettings by viewModel.exerciseSettings.collectAsState(
                ExerciseSettingsWithScreens()
            )
            val serviceState by viewModel.serviceState
            PreWorkoutPermissionCheck(
                onStartNavigate = {
                    navController.popBackStack(
                        route = Screen.START_MENU.route,
                        inclusive = true,
                        saveState = false
                    )
                    navController.navigate(Screen.WORKOUT.route)
                },
                exerciseSettings = exerciseSettings,
                serviceState = serviceState,
                onStartExercise = {
                    viewModel.startExercise()
                },
                onPrepareExercise = { viewModel.prepare() }
            )
        }
        composable(Screen.WORKOUT.route) {
            WorkoutScreen(
                onFinishNavigate = { exerciseId ->
                    navController.popBackStack()
                    navController.navigate(Screen.POST_WORKOUT.route + "/" + exerciseId)
                }
            )
        }
        scalingLazyColumnComposable(
            route = Screen.POST_WORKOUT.route + "/{exerciseId}",
            scrollStateBuilder = { ScalingLazyListState() }
        ) {
            val viewModel = hiltViewModel<PostWorkoutViewModel>()
            val savedExercise by viewModel.savedExercise.collectAsState(initial = SavedExercise())
            PostWorkoutScreen(
                savedExercise = savedExercise,
                scrollState = it.scrollableState
            )
        }
        scalingLazyColumnComposable(
            route = Screen.SETTINGS.route,
            scrollStateBuilder = { ScalingLazyListState() }
        ) {
            val viewModel = hiltViewModel<SettingsViewModel>()
            val exerciseSettings by viewModel.exerciseSettings.collectAsState(listOf())
            val tempoSettings by viewModel.tempoSettingsManager.tempoSettings.collectAsState(
                TempoSettings(units = Units.METRIC)
            )
            SettingsScreen(
                exerciseSettings = exerciseSettings,
                tempoSettings = tempoSettings,
                onWorkoutSettingsClick = { id ->
                    navController.navigate(
                        Screen.WORKOUT_SETTINGS.route + "/${id}"
                    )
                },
                onSetUnits = { units ->
                    viewModel.tempoSettingsManager.setUnits(units)
                },
                scrollState = it.scrollableState
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
                    navController.navigate(Screen.SCREEN_EDITOR.route + "/" + settingsId)
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
                    navController.navigate(route)
                },
                onScreenFormatClick = { screen ->
                    val route = Screen.SCREEN_FORMAT.route + "/$settingsId/$screen"
                    navController.navigate(route)
                }
            )
        }
        composable(
            Screen.SCREEN_FORMAT.route + "/{settingsId}/{screen}",
            arguments = listOf(
                navArgument("settingsId") { type = NavType.IntType },
                navArgument("screen") { type = NavType.IntType }
            )
        )
        {
            val viewModel = hiltViewModel<ScreenFormatViewModel>()
            ScreenFormatScreen(
                onScreenFormatClick = { screenFormat ->
                    viewModel.setScreenFormat(screenFormat)
                    navController.popBackStack()
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
                    navController.popBackStack()
                }
            )
        }
    }
}
