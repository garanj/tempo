package com.garan.tempo.ui.screens.startmenu

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.flowWithLifecycle
import androidx.wear.compose.material.AutoCenteringParams
import androidx.wear.compose.material.Chip
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.ScalingLazyColumn
import androidx.wear.compose.material.ScalingLazyListAnchorType
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.items
import com.garan.tempo.R
import com.garan.tempo.UiState
import com.garan.tempo.data.imageVector
import com.garan.tempo.ui.navigation.Screen
import kotlinx.coroutines.flow.Flow
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

@Composable
fun <T : R, R> Flow<T>.collectAsStateLifecycleAware(
    initial: R,
    context: CoroutineContext = EmptyCoroutineContext
): State<R> {
    val lifecycleAwareFlow = rememberFlow(flow = this)
    return lifecycleAwareFlow.collectAsState(initial = initial, context = context)
}

@Composable
fun <T> rememberFlow(
    flow: Flow<T>,
    lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current
): Flow<T> {
    return remember(
        key1 = flow,
        key2 = lifecycleOwner
    ) { flow.flowWithLifecycle(lifecycleOwner.lifecycle, Lifecycle.State.STARTED) }
}

/**
 * Composable functions used on the Connect screen, for initiating a connection to the fan.
 */
@Composable
fun StartMenuScreen(
    uiState: UiState,
    screenStarted: Boolean = uiState.navHostController
        .getBackStackEntry(Screen.START_MENU.route)
        .lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED),
    viewModel: StartMenuViewModel = hiltViewModel()
) {
    val exerciseSettings by viewModel.exerciseSettings.collectAsStateLifecycleAware(initial = emptyList())

    LaunchedEffect(screenStarted) {
        if (screenStarted) {
            uiState.isShowTime.value = true
        }
    }

    if (exerciseSettings.isNotEmpty()) {
        ScalingLazyColumn(
            anchorType = ScalingLazyListAnchorType.ItemStart,
            autoCentering = AutoCenteringParams()
        ) {
            items(exerciseSettings) { setting ->
                Chip(
                    onClick = {
                        val id = setting.exerciseSettings.exerciseSettingsId
                        uiState.navHostController.navigate(Screen.PRE_WORKOUT.route + "/" + id)
                    },
                    label = { Text(setting.exerciseSettings.name) },
                    icon = {
                        Icon(
                            imageVector = setting.exerciseSettings.exerciseType.imageVector,
                            contentDescription = setting.exerciseSettings.name
                        )
                    }
                )
            }
            item {
                Chip(
                    onClick = {
                        uiState.navHostController.navigate(Screen.SETTINGS.route)
                    },
                    label = { Text(text = stringResource(id = R.string.settings)) },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = stringResource(id = R.string.settings)
                        )
                    }
                )
            }
        }
    }
}