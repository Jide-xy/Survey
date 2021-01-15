package com.jide.surveyapp.model

import com.jide.surveyapp.util.DateUtil
import com.jide.surveyapp.util.UUID

data class Survey(
        val name: String,
        val description: String?,
        val shared: Boolean,
        val backedUp: Boolean = false,
        val dateCreated: String = DateUtil.timeInMilliseconds.toString(),
        val id: String = UUID.randomString(),
        val responseCount: Int = 0
)