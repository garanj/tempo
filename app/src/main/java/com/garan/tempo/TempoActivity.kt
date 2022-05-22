package com.garan.tempo

import android.Manifest
import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.fragment.app.FragmentActivity
import androidx.health.services.client.data.ExerciseTrackedStatus
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavHostController
import androidx.wear.ambient.AmbientModeSupport
import androidx.wear.compose.material.Scaffold
import androidx.wear.compose.material.TimeText
import androidx.wear.compose.material.Vignette
import androidx.wear.compose.material.VignettePosition
import androidx.wear.compose.navigation.rememberSwipeDismissableNavController
import com.garan.tempo.settings.TempoSettingsManager
import com.garan.tempo.ui.format.LocalDisplayUnitFormatter
import com.garan.tempo.ui.format.imperialUnitFormatter
import com.garan.tempo.ui.navigation.Screen
import com.garan.tempo.ui.navigation.TempoNavigation
import com.garan.tempo.ui.theme.TempoTheme
import com.google.accompanist.pager.ExperimentalPagerApi
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.async
import javax.inject.Inject

const val TAG = "Tempo"

class UiState(
    var isShowTime: MutableState<Boolean>,
    var isShowVignette: MutableState<Boolean>,
    val navHostController: NavHostController
)

// TODO sort
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
    // TODO move
    private val requiredPermissions = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACTIVITY_RECOGNITION,
        Manifest.permission.BODY_SENSORS
    )

    @Inject
    lateinit var healthServicesManager: HealthServicesManager

    @Inject
    lateinit var tempoSettingsManager: TempoSettingsManager

    // TODO Move to remember...
    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { result ->
        if (result.all { it.value }) {
            Log.i(TAG, "All required permissions granted")
        } else {
            Log.i(TAG, "Not all required permissions granted")
            // TODO permissions Composable
        }
    }

    @ExperimentalPagerApi
    @Composable
    fun TempoScreen(startDestination: String = Screen.START_MENU.route) {
        // TODO move this so settings take effect
        val appState = rememberUiState()
        // TODO fix
        val formatter = imperialUnitFormatter()

        CompositionLocalProvider(LocalDisplayUnitFormatter provides formatter) {
            TempoTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    timeText = { if (appState.isShowTime.value) TimeText() },
                    vignette = {
                        if (appState.isShowVignette.value) Vignette(vignettePosition = VignettePosition.TopAndBottom)
                    }
                ) {
                    TempoNavigation(
                        uiState = appState,
                        startDestination = startDestination
                    )
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AmbientModeSupport.attach(this)

        val exerciseInfo = lifecycleScope.async {
            healthServicesManager.isExerciseInProgress()
        }
        exerciseInfo.invokeOnCompletion {
            when (exerciseInfo.getCompleted().exerciseTrackedStatus) {
                ExerciseTrackedStatus.NO_EXERCISE_IN_PROGRESS -> freshStart()
                ExerciseTrackedStatus.OWNED_EXERCISE_IN_PROGRESS -> continueExercise()
                ExerciseTrackedStatus.OTHER_APP_IN_PROGRESS -> otherExerciseWarning()
            }
        }
    }

    @OptIn(ExperimentalPagerApi::class)
    private fun freshStart() {
        // TODO move the permission to when the activity type is selected
        permissionLauncher.launch(requiredPermissions)

        setContent {
            TempoScreen()
        }
    }

    @OptIn(ExperimentalPagerApi::class)
    private fun continueExercise() {
        setContent {
            TempoScreen(startDestination = Screen.WORKOUT.route)
        }
    }

    private fun otherExerciseWarning() {

    }

    override fun getAmbientCallback() = object : AmbientModeSupport.AmbientCallback() {}
}
