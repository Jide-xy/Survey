package com.example.babajidemustapha.survey.shared.models

import androidx.annotation.DrawableRes
import com.example.babajidemustapha.survey.R

enum class QuestionType {
    SHORT_TEXT,
    LONG_TEXT,
    SINGLE_OPTION,
    MULTIPLE_OPTION,
    IMAGES,
    REACTIONS;


    @DrawableRes
    fun getImageResource(): Int {
        return when (this) {
            SHORT_TEXT -> R.drawable.ic_text_fields
            LONG_TEXT -> R.drawable.ic_text_fields
            SINGLE_OPTION -> R.drawable.ic_radio_button_q_type
            MULTIPLE_OPTION -> R.drawable.ic_check_q_type
            IMAGES -> R.drawable.ic_image_q_type
            REACTIONS -> R.drawable.ic_reaction_q_type
        }
    }

    fun getDisplayName(): String {
        return when (this) {
            SHORT_TEXT -> "Simple Text"
            LONG_TEXT -> "Paragraph"
            MULTIPLE_OPTION -> "Multiselect"
            else -> toSentenceCase(this.name.toLowerCase().replace("_", " "))
        }
    }

    fun hasOptions(): Boolean {
        return when (this) {
            SINGLE_OPTION, MULTIPLE_OPTION -> true
            else -> false
        }
    }

    private fun toSentenceCase(text: String): String = text.split(" ").fold("") { acc, s ->
        "$acc ${s.capitalize()}"
    }.trim()
}