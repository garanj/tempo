package com.garan.tempo

import android.Manifest
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.net.Uri
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsRun
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.fragment.app.FragmentActivity
import androidx.health.services.client.data.ExerciseState
import androidx.navigation.NavHostController
import androidx.navigation.navDeepLink
import androidx.wear.ambient.AmbientModeSupport
import androidx.wear.compose.material.ExperimentalWearMaterialApi
import androidx.wear.compose.material.Scaffold
import androidx.wear.compose.material.TimeText
import androidx.wear.compose.material.Vignette
import androidx.wear.compose.material.VignettePosition
import androidx.wear.compose.navigation.SwipeDismissableNavHost
import androidx.wear.compose.navigation.composable
import androidx.wear.compose.navigation.rememberSwipeDismissableNavController
import com.garan.tempo.settings.ExerciseSettings
import com.garan.tempo.settings.TempoSettingsManager
import com.garan.tempo.settings.Units
import com.garan.tempo.ui.components.MenuItem
import com.garan.tempo.ui.format.LocalDisplayUnitFormatter
import com.garan.tempo.ui.format.imperialUnitFormatter
import com.garan.tempo.ui.format.metricUnitFormatter
import com.garan.tempo.ui.screens.PostWorkoutScreen
import com.garan.tempo.ui.screens.PreWorkoutScreen
import com.garan.tempo.ui.screens.SettingsScreen
import com.garan.tempo.ui.screens.StartMenuScreen
import com.garan.tempo.ui.screens.TempoLoadingMessage
import com.garan.tempo.ui.screens.WorkoutScreen
import com.garan.tempo.ui.theme.TempoTheme
import com.google.accompanist.pager.ExperimentalPagerApi
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.serialization.ExperimentalSerializationApi
import javax.inject.Inject
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.garan.tempo.ui.model.WorkoutSettingsViewModel
import com.garan.tempo.ui.screens.WorkoutSettingsScreen
import androidx.hilt.navigation.compose.hiltViewModel
import com.garan.tempo.ui.screens.ScreenEditor


const val TAG = "Tempo"

enum class Screen(val route: String) {
    LOADING("loading"),
    START_MENU("start_menu"),
    PRE_WORKOUT("pre_workout"),
    WORKOUT("workout"),
    POST_WORKOUT("post_workout"),
    SETTINGS("settings"),
    WORKOUT_SETTINGS("workout_settings"),
    SCREEN_EDITOR("screen_editor")
}

class UiState(
    var isShowTime: MutableState<Boolean>,
    var isShowVignette: MutableState<Boolean>,
    val navHostController: NavHostController
)

@OptIn(ExperimentalWearMaterialApi::class)
@Composable
fun rememberUiState(
    isShowTime: MutableState<Boolean> = mutableStateOf(true),
    isShowVignette: MutableState<Boolean> = mutableStateOf(false),
    navHostController: NavHostController = rememberSwipeDismissableNavController()
) = remember(isShowTime, isShowVignette, navHostController) {
    UiState(isShowTime, isShowVignette, navHostController)
}

/**
 * Activity for controlling searching for the Headwind fan, and launching the fan control activity
 * on successfully locating and connecting to it.
 */
@AndroidEntryPoint
class TempoActivity : FragmentActivity(), AmbientModeSupport.AmbientCallbackProvider {
    private val requiredPermissions = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACTIVITY_RECOGNITION,
        Manifest.permission.BODY_SENSORS
    )

    @Inject
    lateinit var tempoSettingsManager: TempoSettingsManager

    private var tempoService: MutableState<TempoService?> = mutableStateOf(null)
    private var bound: Boolean = false

    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { result ->
        if (result.all { it.value }) {
            Log.i(TAG, "All required permissions granted")
            createService()
        } else {
            Log.i(TAG, "Not all required permissions granted")
            // TODO permissions Composable
        }
    }

    private val connection = object : ServiceConnection {
        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            val binder = service as TempoService.LocalBinder
            binder.getService().let {
                tempoService.value = it
            }
            Log.i(TAG, "onServiceConnected")
            bound = true
        }

        override fun onServiceDisconnected(arg0: ComponentName) {
            bound = false
            tempoService.value = null
            Log.i(TAG, "onServiceDisconnected")
        }
    }

    private fun createService() {
        Intent(this, TempoService::class.java).also { intent ->
            startForegroundService(intent)
            bindService(intent, connection, Context.BIND_AUTO_CREATE)
        }
    }

    @OptIn(ExperimentalWearMaterialApi::class)
    @ExperimentalPagerApi
    @Composable
    fun TempoScreen() {
        // TODO move this so settings take effect
        val appState = rememberUiState()
        val units by tempoSettingsManager.units.collectAsState(Units.IMPERIAL)
        val formatter = when (units) {
            Units.IMPERIAL -> imperialUnitFormatter()
            Units.METRIC -> metricUnitFormatter()
        }

        CompositionLocalProvider(LocalDisplayUnitFormatter provides formatter) {
            TempoTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    timeText = { if (appState.isShowTime.value) TimeText() },
                    vignette = {
                        if (appState.isShowVignette.value) Vignette(vignettePosition = VignettePosition.TopAndBottom)
                    }
                ) {
                    WearwindNavigation(appState)
                }
            }
        }
    }

    @OptIn(ExperimentalWearMaterialApi::class)
    @ExperimentalPagerApi
    @Composable
    fun WearwindNavigation(uiState: UiState) {
        Log.i(TAG, "Navigation")
        val service by tempoService
        val exercises by tempoSettingsManager.exercises.collectAsState(listOf())

        SwipeDismissableNavHost(
            navController = uiState.navHostController,
            startDestination = Screen.LOADING.route
        ) {
            composable(Screen.LOADING.route) {
                TempoLoadingMessage(uiState = uiState, service = service)
            }
            composable(Screen.START_MENU.route) {
                StartMenuScreen(
                    uiState = uiState,
                    service = service!!,
                    startMenuItems = exercises.mapIndexed { index, exerciseSettings ->
                        MenuItem.MenuButton(
                            exerciseSettings.name,
                            imageVector = Icons.Default.DirectionsRun,
                            onClick = {
                                service!!.prepare(index)
                            }
                        )
                    }
                            + MenuItem.MenuButton(
                        stringResource(id = R.string.settings),
                        imageVector = Icons.Default.Settings,
                        onClick = {
                            uiState.navHostController.navigate(Screen.SETTINGS.route)
                        }
                    )

                )
            }
            composable(Screen.PRE_WORKOUT.route) {
                PreWorkoutScreen(service = service!!, uiState = uiState,
                    onSwipeBack = {
                        Log.i(TAG, "Cancel prepare")
                        service!!.cancelPrepare()
                    },
                    onClick = {
                        service!!.startWorkout()
                    }
                )
            }
            composable(
                Screen.WORKOUT.route,
                deepLinks = listOf(navDeepLink { uriPattern = "app://tempo/settings" })
            ) {
                val currentExerciseSettings by service!!.currentExerciseSettings
                val metricsUpdate = service!!.metrics
                val exerciseState by service!!.exerciseState
                WorkoutScreen(
                    screenList = currentExerciseSettings.screens,
                    metricsUpdate = metricsUpdate,
                    exerciseState = exerciseState,
                    uiState = uiState,
                    onPauseTap = {
                        if (exerciseState.isUserPaused) {
                            service!!.resumeWorkout()
                        } else if (exerciseState == ExerciseState.ACTIVE) {
                            service!!.pauseWorkout()
                        }
                    },
                    onFinishTap = {
                        service!!.endWorkout()
                    }
                )
            }
            composable(Screen.POST_WORKOUT.route) {
                PostWorkoutScreen(uiState = uiState, service = service!!)
            }
            composable(Screen.SETTINGS.route) {

                SettingsScreen(
                    uiState = uiState,
                    settings = tempoSettingsManager
                )
            }
            composable(Screen.WORKOUT_SETTINGS.route + "/{settingsId}") { backStackEntry ->
                val settingsId = backStackEntry.arguments?.getString("settingsId")
                WorkoutSettingsScreen(
                    onScreenButtonClick = {
                        uiState.navHostController.navigate(Screen.SCREEN_EDITOR.route + "/" + settingsId)
                    }
                )
            }
            composable(Screen.SCREEN_EDITOR.route + "/{settingsId}") { backStackEntry ->
                ScreenEditor()
            }
        }
    }


    @OptIn(ExperimentalWearMaterialApi::class,
        com.google.accompanist.pager.ExperimentalPagerApi::class
    )
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AmbientModeSupport.attach(this)

        // TODO move the permission to when the activity type is selected
        permissionLauncher.launch(requiredPermissions)

        setContent {
            TempoScreen()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.i(TAG, "Finishing....")
        if (bound) {
            unbindService(connection)
        }
    }

    override fun getAmbientCallback() = object : AmbientModeSupport.AmbientCallback() {}

}

fun workoutButtonlist(settingsList: List<ExerciseSettings>) = {
//    settingsList.map { settings ->
//        MenuItem.MenuButton(
//            settings.name,
//            imageVector = Icons.Default.DirectionsRun,
//            onClick = {
//                service!!.prepare()
//            }
//        )
//    }
}