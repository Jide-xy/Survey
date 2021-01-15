package com.jide.surveyapp.util.mapper

import com.jide.surveyapp.model.Question
import com.jide.surveyapp.util.Mapper
import comjidesurveyapp.QuestionEntity

object QuestionWithoutOptionsMapper : Mapper<QuestionEntity, Question> {
    override suspend fun map(from: QuestionEntity): Question {
        return with(from) {
            Question(questionNo, questionType, mandatory, questionText, surveyId, id)
        }
    }

    override suspend fun mapInverse(from: Question): QuestionEntity {
        return with(from) {
            QuestionEntity(id, questionNo, questionType!!, mandatory, questionText!!, surveyId
                    ?: "")
        }
    }
}