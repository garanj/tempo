package com.garan.tempo.settings

enum class Units {
    METRIC {
        override val speedFactor = 3.6
        override val distanceFactor = 1000.0
        override val heightFactor = 1.0
    },
    IMPERIAL {
        override val speedFactor = 2.23694
        override val distanceFactor = 1609.344
        override val heightFactor = 3.28084
    };

    abstract val speedFactor: Double
    abstract val distanceFactor: Double
    abstract val heightFactor: Double
}