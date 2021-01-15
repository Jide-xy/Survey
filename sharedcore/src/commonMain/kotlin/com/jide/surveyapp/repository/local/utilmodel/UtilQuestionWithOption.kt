package com.jide.surveyapp.repository.local.utilmodel

import comjidesurveyapp.QuestionEntity

data class UtilQuestionWithOption(
        val question: QuestionEntity,
        val options: UtilOptionAndResponse
)