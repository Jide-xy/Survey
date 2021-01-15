package com.jide.surveyapp.model.relations

import com.jide.surveyapp.model.Survey

data class SurveyWithQuestionsAndOptions(
        val survey: Survey,
        val questions: List<QuestionWithOptions>
)
