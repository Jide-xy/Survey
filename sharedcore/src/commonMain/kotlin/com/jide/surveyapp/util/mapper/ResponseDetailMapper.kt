package com.jide.surveyapp.util.mapper

import com.jide.surveyapp.model.ResponseDetail
import com.jide.surveyapp.util.Mapper
import comjidesurveyapp.ResponseDetailEntity

object ResponseDetailMapper : Mapper<ResponseDetailEntity, ResponseDetail> {
    override suspend fun map(from: ResponseDetailEntity): ResponseDetail {
        return with(from) {
            ResponseDetail(optionId, freeTextResponse, responseHeaderId, id)
        }
    }

    override suspend fun mapInverse(from: ResponseDetail): ResponseDetailEntity {
        return with(from) {
            ResponseDetailEntity(id, responseHeaderId, optionId, freeTextResponse)
        }
    }
}