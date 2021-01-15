package com.jide.surveyapp.util.mapper

import com.jide.surveyapp.model.Survey
import com.jide.surveyapp.util.Mapper
import comjidesurveyapp.SurveyEntity

object SurveyWithoutResponseCountMapper : Mapper<SurveyEntity, Survey> {
    override suspend fun map(from: SurveyEntity): Survey {
        return with(from) {
            Survey(name, description, shared, backedUp, dateCreated, id)
        }
    }

    override suspend fun mapInverse(from: Survey): SurveyEntity {
        return with(from) {
            SurveyEntity(id, name, description, shared, backedUp, dateCreated)
        }
    }
}