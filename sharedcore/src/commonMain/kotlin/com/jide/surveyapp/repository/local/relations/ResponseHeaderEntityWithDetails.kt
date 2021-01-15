package com.jide.surveyapp.repository.local.relations

import comjidesurveyapp.ResponseDetailEntity
import comjidesurveyapp.ResponseHeaderEntity

data class ResponseHeaderEntityWithDetails(
        val responseHeaderEntity: ResponseHeaderEntity,
        val responseDetailEntities: List<ResponseDetailEntity>
)
