package com.jide.surveyapp.util.mapper

import com.jide.surveyapp.model.ResponseHeader
import com.jide.surveyapp.util.Mapper
import comjidesurveyapp.ResponseHeaderEntity

object ResponseHeaderMapper : Mapper<ResponseHeaderEntity, ResponseHeader> {
    override suspend fun map(from: ResponseHeaderEntity): ResponseHeader {
        return with(from) {
            ResponseHeader(respondentName, surveyId, date, backedUp, id)
        }
    }

    override suspend fun mapInverse(from: ResponseHeader): ResponseHeaderEntity {
        return with(from) {
            ResponseHeaderEntity(id, respondentName, backedUp, date, surveyId)
        }
    }
}