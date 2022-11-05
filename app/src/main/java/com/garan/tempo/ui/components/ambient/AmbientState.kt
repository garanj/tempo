package com.garan.tempo.ui.components.ambient

sealed class AmbientState {
    object Interactive : AmbientState()
    object Ambient : AmbientState()
}