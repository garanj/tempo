package com.garan.tempo

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.fragment.app.FragmentActivity
import androidx.health.services.client.data.ExerciseTrackedStatus
import androidx.lifecycle.lifecycleScope
import androidx.wear.ambient.AmbientModeSupport
import androidx.wear.compose.material.TimeText
import androidx.wear.compose.navigation.rememberSwipeDismissableNavController
import com.garan.tempo.settings.TempoSettings
import com.garan.tempo.settings.TempoSettingsManager
import com.garan.tempo.settings.Units
import com.garan.tempo.ui.format.LocalDisplayUnitFormatter
import com.garan.tempo.ui.format.imperialUnitFormatter
import com.garan.tempo.ui.format.metricUnitFormatter
import com.garan.tempo.ui.navigation.Screen
import com.garan.tempo.ui.navigation.TempoScaffold
import com.garan.tempo.ui.theme.TempoTheme
import com.google.accompanist.pager.ExperimentalPagerApi
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import javax.inject.Inject

const val TAG = "Tempo"

/**
 * Activity for controlling searching for the Headwind fan, and launching the fan control activity
 * on successfully locating and connecting to it.
 */
@AndroidEntryPoint
class TempoActivity : FragmentActivity(), AmbientModeSupport.AmbientCallbackProvider {
    @Inject
    lateinit var healthServicesManager: HealthServicesManager

    @Inject
    lateinit var tempoSettingsManager: TempoSettingsManager

    val timeText: @Composable (Modifier) -> Unit = { modifier ->
        TimeText(modifier = modifier)
    }

    @ExperimentalPagerApi
    @Composable
    fun TempoScreen(startDestination: String = Screen.START_MENU.route) {
        val navController = rememberSwipeDismissableNavController()
        val tempoSettings by tempoSettingsManager.tempoSettings
            .collectAsState(initial = TempoSettings(units = Units.METRIC))
        val formatter = if (tempoSettings.units == Units.METRIC) {
            metricUnitFormatter()
        } else {
            imperialUnitFormatter()
        }

        CompositionLocalProvider(LocalDisplayUnitFormatter provides formatter) {
            TempoTheme {
                TempoScaffold(
                    navController = navController,
                    startDestination = startDestination,
                    timeText = timeText
                )
            }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class, ExperimentalPagerApi::class)
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
        // TODO
    }

    override fun getAmbientCallback() = object : AmbientModeSupport.AmbientCallback() {}
}
