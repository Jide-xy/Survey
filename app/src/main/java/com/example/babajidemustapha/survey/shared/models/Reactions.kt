package com.example.babajidemustapha.survey.shared.models

import androidx.annotation.IdRes
import com.example.babajidemustapha.survey.R

enum class Reactions {
    VERY_HAPPY,
    HAPPY,
    INDIFFERENT,
    SAD;

    @IdRes
    fun getReactionViewId(): Int {
        return when (this) {
            VERY_HAPPY -> R.id.reactionVeryHappy
            HAPPY -> R.id.reactionHappy
            INDIFFERENT -> R.id.reactionIndifferent
            SAD -> R.id.reactionSad
        }
    }
}