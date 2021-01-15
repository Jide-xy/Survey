package com.jide.surveyapp.model

import com.jide.surveyapp.util.DateUtil
import com.jide.surveyapp.util.UUID

data class ResponseHeader(
        val respondentName: String?,
        val surveyId: String,
        val date: String = DateUtil.timeInMilliseconds.toString(),
        val backedUp: Boolean = false,
        val id: String = UUID.randomString()
)