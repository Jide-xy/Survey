package com.jide.surveyapp.model.relations

import com.jide.surveyapp.model.Question
import com.jide.surveyapp.model.Survey

data class SurveyWithQuestions(
        val survey: Survey,
        val questions: List<Question>
)
