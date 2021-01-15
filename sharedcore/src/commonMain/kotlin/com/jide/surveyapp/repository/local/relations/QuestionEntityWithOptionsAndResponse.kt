package com.jide.surveyapp.repository.local.relations

import comjidesurveyapp.OptionEntity
import comjidesurveyapp.QuestionEntity
import comjidesurveyapp.ResponseDetailEntity

data class QuestionEntityWithOptionsAndResponse(
        val question: QuestionEntity,
        val options: List<OptionEntity>,
        val responses: List<ResponseDetailEntity>
)