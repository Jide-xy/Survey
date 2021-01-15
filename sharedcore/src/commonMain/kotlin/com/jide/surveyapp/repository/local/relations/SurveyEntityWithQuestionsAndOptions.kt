package com.jide.surveyapp.repository.local.relations

import comjidesurveyapp.SurveyEntity

data class SurveyEntityWithQuestionsAndOptions(
        val survey: SurveyEntity,
        val questions: List<QuestionEntityWithOptions>
)
