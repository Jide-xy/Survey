package com.jide.surveyapp.util.mapper

import com.jide.surveyapp.model.ResponseDetail
import com.jide.surveyapp.model.ResponseHeader
import com.jide.surveyapp.model.relations.ResponseHeaderWithDetails
import com.jide.surveyapp.repository.local.relations.ResponseHeaderEntityWithDetails
import com.jide.surveyapp.util.Mapper
import comjidesurveyapp.ResponseDetailEntity
import comjidesurveyapp.ResponseHeaderEntity

class ResponseHeaderWithDetailsMapper(
        private val responseHeaderMapper: Mapper<ResponseHeaderEntity, ResponseHeader>,
        private val responseDetailMapper: Mapper<ResponseDetailEntity, ResponseDetail>
) : Mapper<ResponseHeaderEntityWithDetails, ResponseHeaderWithDetails> {
    override suspend fun map(from: ResponseHeaderEntityWithDetails): ResponseHeaderWithDetails {
        return with(from) {
            ResponseHeaderWithDetails(responseHeaderMapper.map(responseHeaderEntity), responseDetailMapper.mapList(responseDetailEntities))
        }
    }

    override suspend fun mapInverse(from: ResponseHeaderWithDetails): ResponseHeaderEntityWithDetails {
        return with(from) {
            ResponseHeaderEntityWithDetails(responseHeaderMapper.mapInverse(responseHeaderEntity), responseDetailMapper.mapListInverse(responseDetailEntities))
        }
    }
}