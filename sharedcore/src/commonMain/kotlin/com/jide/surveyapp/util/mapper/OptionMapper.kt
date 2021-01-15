package com.jide.surveyapp.util.mapper

import com.jide.surveyapp.model.Option
import com.jide.surveyapp.util.Mapper
import comjidesurveyapp.OptionEntity

object OptionMapper : Mapper<OptionEntity, Option> {
    override suspend fun map(from: OptionEntity): Option {
        return with(from) {
            Option(optionNo, optionText, questionId, id)
        }
    }

    override suspend fun mapInverse(from: Option): OptionEntity {
        return with(from) {
            OptionEntity(id, optionNo, optionText, questionId ?: "")
        }
    }
}