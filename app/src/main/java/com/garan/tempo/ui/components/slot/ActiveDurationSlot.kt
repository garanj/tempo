package com.garan.tempo.ui.components.slot

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.style.TextAlign
import androidx.health.services.client.data.ExerciseState
import androidx.health.services.client.data.ExerciseUpdate
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.wear.compose.material.MaterialTheme
import com.garan.tempo.ui.components.AutoSizeText
import com.garan.tempo.ui.components.ambient.AmbientState
import kotlinx.coroutines.delay
import java.time.Duration

/**
 * The ActiveDurationSlot is a separate implementation to how other metrics are represented, as the
 * active duration needs to tick up, every second, independently of the frequency at which new
 * ActiveDuration readings are sent from Health Services.
 *
 * It is expected that ActiveDuration will be sent from Health Services _less_ frequently than every
 * second, so should not be relied on to tick the duration up.
 *
 * Therefore it is necessary to run a ticking loop locally, but also ensure that this loop does not
 * run when either the screen is not on or screen is in ambient mode.
 */
@Composable
fun ActiveDurationSlot(
    checkpoint: ExerciseUpdate.ActiveDurationCheckpoint?,
    state: ExerciseState,
    textAlign: TextAlign,
    onConfigClick: () -> Unit,
    isForConfig: Boolean = false,
    ambientState: AmbientState,
    lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current
) {
    var durationStart by remember { mutableStateOf(calculateDurationMillis(checkpoint)) }
    var duration by remember { mutableStateOf(0L) }
    var tickStart by remember { mutableStateOf(System.currentTimeMillis()) }
    val seconds by remember { derivedStateOf { duration / 1000 } }
    var lifecycleEnabled by remember { mutableStateOf(true) }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                lifecycleEnabled = true
            } else if (event == Lifecycle.Event.ON_PAUSE) {
                lifecycleEnabled = false
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    LaunchedEffect(state, lifecycleEnabled, ambientState) {
        durationStart = calculateDurationMillis(checkpoint)
        tickStart = System.currentTimeMillis()

        if (state == ExerciseState.ACTIVE && lifecycleEnabled && ambientState == AmbientState.Interactive) {
            while (true) {
                delay(200)
                val delta = System.currentTimeMillis() - tickStart
                duration = durationStart + delta
            }
        }
    }

    if (ambientState == AmbientState.Interactive) {
        val formattedValue = "%01d:%02d:%02d".format(
            seconds / 3600, (seconds % 3600) / 60, seconds % 60
        )
        AutoSizeText(
            text = formattedValue,
            textAlign = textAlign,
            mainColor = if (!state.isPaused) {
                MaterialTheme.colors.onSurface
            } else {
                MaterialTheme.colors.secondary
            },
            sizingPlaceholder = "8:88:88",
            onClick = onConfigClick,
            isForConfig = isForConfig
        )
    }
}

private fun calculateDurationMillis(checkpoint: ExerciseUpdate.ActiveDurationCheckpoint?) =
    checkpoint?.activeDuration
        ?.plus(
            Duration.ofMillis(System.currentTimeMillis() - checkpoint.time.toEpochMilli())
        )?.toMillis() ?: 0L


