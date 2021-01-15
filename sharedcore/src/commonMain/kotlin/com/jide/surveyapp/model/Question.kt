package com.jide.surveyapp.model

import com.jide.surveyapp.util.UUID

data class Question(
        val questionNo: Int = 0,
        val questionType: QuestionType? = null,
        val mandatory: Boolean = false,
        val questionText: String? = null,
        val surveyId: String? = null,
        val id: String = UUID.randomString()
)