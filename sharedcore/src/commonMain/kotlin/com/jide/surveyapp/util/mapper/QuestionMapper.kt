package com.jide.surveyapp.util.mapper

import com.jide.surveyapp.model.Option
import com.jide.surveyapp.model.Question
import com.jide.surveyapp.model.relations.QuestionWithOptions
import com.jide.surveyapp.repository.local.relations.QuestionEntityWithOptions
import com.jide.surveyapp.util.Mapper
import comjidesurveyapp.OptionEntity
import comjidesurveyapp.QuestionEntity

class QuestionMapper(
        private val questionWithoutOptionsMapper: Mapper<QuestionEntity, Question>,
        private val optionsMapper: Mapper<OptionEntity, Option>
) : Mapper<QuestionEntityWithOptions, QuestionWithOptions> {
    override suspend fun map(from: QuestionEntityWithOptions): QuestionWithOptions {
        return with(from) {
            QuestionWithOptions(questionWithoutOptionsMapper.map(question), optionsMapper.mapList(options).toMutableList())
        }
    }

    override suspend fun mapInverse(from: QuestionWithOptions): QuestionEntityWithOptions {
        return with(from) {
            QuestionEntityWithOptions(questionWithoutOptionsMapper.mapInverse(question), optionsMapper.mapListInverse(options))
        }
    }
}