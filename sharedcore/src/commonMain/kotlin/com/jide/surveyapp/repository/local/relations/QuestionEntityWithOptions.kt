package com.jide.surveyapp.repository.local.relations

import comjidesurveyapp.OptionEntity
import comjidesurveyapp.QuestionEntity

data class QuestionEntityWithOptions(
        val question: QuestionEntity,
        val options: List<OptionEntity>
)
