package com.jide.surveyapp.model

import com.jide.surveyapp.util.UUID

data class ResponseDetail(
        val optionId: String,
        val freeTextResponse: String? = null,
        val responseHeaderId: String = "",
        val id: String = UUID.randomString()
)