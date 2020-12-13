package com.example.babajidemustapha.survey.shared.models

import androidx.room.ColumnInfo
import com.example.babajidemustapha.survey.shared.room.entities.Question

/**
 * Created by Babajide Mustapha on 9/27/2017.
 */
class QuestionAndResponse : Question() {
    @ColumnInfo(name = "RESPONSE")
    var response: String? = null
}