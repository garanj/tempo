package com.garan.tempo.settings

import com.garan.tempo.R

enum class ScreenFormat {
    ONE_SLOT {
        override val numSlots = 1
        override val labelId = R.string.screen_format_one_slot
    },
    TWO_SLOT {
        override val numSlots = 2
        override val labelId = R.string.screen_format_two_slot
    },
    ONE_PLUS_TWO_SLOT {
        override val numSlots = 3
        override val labelId = R.string.screen_format_one_plus_two_slot
    },
    ONE_PLUS_FOUR_SLOT {
        override val numSlots = 5
        override val labelId = R.string.screen_format_one_plus_four_slot
    },
    SIX_SLOT {
        override val numSlots = 6
        override val labelId = R.string.screen_format_six_slot
    };

    abstract val numSlots: Int
    abstract val labelId: Int
}