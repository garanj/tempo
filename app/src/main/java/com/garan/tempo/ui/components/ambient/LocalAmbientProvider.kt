package com.garan.tempo.ui.components.ambient

import androidx.compose.runtime.compositionLocalOf

val LocalAmbientProvider = compositionLocalOf<AmbientState> { AmbientState.Interactive }