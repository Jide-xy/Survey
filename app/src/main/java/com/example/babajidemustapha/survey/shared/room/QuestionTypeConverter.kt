package com.example.babajidemustapha.survey.shared.room

import androidx.room.TypeConverter
import com.example.babajidemustapha.survey.shared.models.QuestionType

class QuestionTypeConverter {
    @TypeConverter
    fun fromQuestionType(questionType: String): QuestionType {
        return QuestionType.valueOf(questionType)
    }

    @TypeConverter
    fun toQuestionType(questionType: QuestionType): String {
        return questionType.name
    }
}