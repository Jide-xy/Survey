package com.jide.surveyapp.model

import com.jide.surveyapp.util.UUID

data class Option(
        val optionNo: Int = -1,
        val optionText: String? = null,
        val questionId: String? = null,
        val id: String = UUID.randomString()
)
