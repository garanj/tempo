package com.garan.tempo.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.navArgument
import androidx.wear.compose.material.ScalingLazyListState
import androidx.wear.compose.material.TimeText
import androidx.wear.compose.navigation.composable
import com.garan.tempo.data.SavedExerciseWithMetrics
import com.garan.tempo.settings.ExerciseSettingsWithScreens
import com.garan.tempo.settings.TempoSettings
import com.garan.tempo.settings.Units
import com.garan.tempo.ui.components.ambient.AmbientState
import com.garan.tempo.ui.screens.metricpicker.MetricPicker
import com.garan.tempo.ui.screens.metricpicker.MetricPickerUiState
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
import com.garan.tempo.ui.screens.workout.WorkoutViewModel
import com.garan.tempo.ui.screens.workoutsettings.WorkoutSettingsScreen
import com.garan.tempo.ui.screens.workoutsettings.WorkoutSettingsUiState
import com.garan.tempo.ui.screens.workoutsettings.WorkoutSettingsViewModel
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.android.horologist.compose.navscaffold.WearNavScaffold
import com.google.android.horologist.compose.navscaffold.scalingLazyColumnComposable


@OptIn(ExperimentalLifecycleComposeApi::class)
@ExperimentalPagerApi
@Composable
fun TempoScaffold(
    startDestination: String,
    navController: NavHostController,
    timeText: @Composable (Modifier) -> Unit = {
        TimeText(modifier = it)
    },
    ambientState: AmbientState
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
            val viewModel = hiltViewModel<WorkoutViewModel>()
            WorkoutScreen(
                serviceState = viewModel.serviceState.value,
                onFinishStateChange = { exerciseId ->
                    navController.popBackStack()
                    navController.navigate(Screen.POST_WORKOUT.route + "/" + exerciseId)
                },
                onFinishTap = { viewModel.endExercise() },
                onPauseResumeTap = { viewModel.pauseResumeExercise() },
                onActiveScreenChange = { viewModel.onPagerChange() },
                ambientState = ambientState
            )
        }
        scalingLazyColumnComposable(
            route = Screen.POST_WORKOUT.route + "/{exerciseId}",
            arguments = listOf(
                navArgument("exerciseId") { type = NavType.StringType }
            ),
            scrollStateBuilder = { ScalingLazyListState() }
        ) {
            val exerciseId = it.backStackEntry.arguments?.getString("exerciseId")!!
            val viewModel = hiltViewModel<PostWorkoutViewModel>()
            val savedExercise by viewModel.savedExercise(exerciseId).collectAsStateWithLifecycle(
                SavedExerciseWithMetrics()
            )
            PostWorkoutScreen(
                savedExerciseWithMetrics = savedExercise,
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
        scalingLazyColumnComposable(
            route = Screen.WORKOUT_SETTINGS.route + "/{settingsId}",
            arguments = listOf(
                navArgument("settingsId") { type = NavType.IntType }
            ),
            scrollStateBuilder = { ScalingLazyListState() }
        ) {
            val settingsId = it.backStackEntry.arguments?.getInt("settingsId")
            val viewModel = hiltViewModel<WorkoutSettingsViewModel>()
            val settings by viewModel.exerciseSettings.collectAsState(WorkoutSettingsUiState())
            WorkoutSettingsScreen(
                onScreenButtonClick = {
                    navController.navigate(Screen.SCREEN_EDITOR.route + "/" + settingsId)
                },
                settings = settings,
                onAutoPauseToggle = { viewModel.setAutoPause(!settings.useAutoPause) },
                scrollState = it.scrollableState
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
                // TODO remove all magic strings
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
        scalingLazyColumnComposable(
            Screen.METRIC_PICKER.route + "/{settingsId}/{screen}/{slot}",
            arguments = listOf(
                navArgument("settingsId") { type = NavType.IntType },
                navArgument("screen") { type = NavType.IntType },
                navArgument("slot") { type = NavType.IntType },
            ),
            scrollStateBuilder = { ScalingLazyListState() }
        ) {
            val settingsId = it.backStackEntry.arguments?.getInt("settingsId")!!
            val screen = it.backStackEntry.arguments?.getInt("screen")!!
            val slot = it.backStackEntry.arguments?.getInt("slot")!!
            val viewModel = hiltViewModel<MetricPickerViewModel>()
            val metrics by viewModel.displayMetrics.collectAsState(initial = MetricPickerUiState())
            MetricPicker(
                onClick = { metric ->
                    viewModel.setMetric(settingsId, screen, slot, metric)
                    navController.popBackStack()
                },
                metrics = metrics,
                scrollState = it.scrollableState
            )
        }
    }
}
