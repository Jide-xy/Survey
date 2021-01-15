package com.jide.surveyapp.repository.local.relations

import comjidesurveyapp.QuestionEntity
import comjidesurveyapp.SurveyEntity

data class SurveyEntityWithQuestions(
        val survey: SurveyEntity,
        val questions: List<QuestionEntity>
)
