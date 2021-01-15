package com.jide.surveyapp.repository.local.utilmodel

import comjidesurveyapp.OptionEntity
import comjidesurveyapp.QuestionEntity

data class ReportEntity(
        val question: QuestionEntity,
        val report: List<Pair<OptionEntity, Int>>
)