package com.jide.surveyapp.model.relations

import com.jide.surveyapp.model.ResponseDetail
import com.jide.surveyapp.model.ResponseHeader

data class ResponseHeaderWithDetails(
        val responseHeaderEntity: ResponseHeader,
        val responseDetailEntities: List<ResponseDetail>
)
