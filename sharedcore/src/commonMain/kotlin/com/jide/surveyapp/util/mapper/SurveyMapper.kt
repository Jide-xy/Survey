package com.jide.surveyapp.util.mapper

import com.jide.surveyapp.model.Survey
import com.jide.surveyapp.repository.local.entity.SurveyEntityWithResponseCount
import com.jide.surveyapp.util.Mapper

object SurveyMapper : Mapper<SurveyEntityWithResponseCount, Survey> {
    override suspend fun map(from: SurveyEntityWithResponseCount): Survey {
        return with(from) {
            Survey(name, description, shared, backedUp, dateCreated, id, responseCount)
        }
    }

    override suspend fun mapInverse(from: Survey): SurveyEntityWithResponseCount {
        return with(from) {
            SurveyEntityWithResponseCount(id, name, description, shared, backedUp, dateCreated, responseCount)
        }
    }
}