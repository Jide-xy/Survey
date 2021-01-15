package com.jide.surveyapp.repository.local.entity

data class SurveyEntityWithResponseCount(
        val id: String,
        val name: String,
        val description: String?,
        val shared: Boolean,
        val backedUp: Boolean,
        val dateCreated: String,
        val responseCount: Int
)
