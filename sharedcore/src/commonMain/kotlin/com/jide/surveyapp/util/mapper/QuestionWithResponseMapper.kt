package com.jide.surveyapp.util.mapper

import com.jide.surveyapp.model.Option
import com.jide.surveyapp.model.Question
import com.jide.surveyapp.model.ResponseDetail
import com.jide.surveyapp.model.relations.QuestionWithOptionsAndResponse
import com.jide.surveyapp.repository.local.relations.QuestionEntityWithOptionsAndResponse
import com.jide.surveyapp.util.Mapper
import comjidesurveyapp.OptionEntity
import comjidesurveyapp.QuestionEntity
import comjidesurveyapp.ResponseDetailEntity

class QuestionWithResponseMapper(
        private val questionWithoutOptionsMapper: Mapper<QuestionEntity, Question>,
        private val optionsMapper: Mapper<OptionEntity, Option>,
        private val responseDetailMapper: Mapper<ResponseDetailEntity, ResponseDetail>
) : Mapper<QuestionEntityWithOptionsAndResponse, QuestionWithOptionsAndResponse> {
    override suspend fun map(from: QuestionEntityWithOptionsAndResponse): QuestionWithOptionsAndResponse {
        return with(from) {
            QuestionWithOptionsAndResponse(questionWithoutOptionsMapper.map(question), optionsMapper.mapList(options), responseDetailMapper.mapList(responses))
        }
    }

    override suspend fun mapInverse(from: QuestionWithOptionsAndResponse): QuestionEntityWithOptionsAndResponse {
        return with(from) {
            QuestionEntityWithOptionsAndResponse(questionWithoutOptionsMapper.mapInverse(question), optionsMapper.mapListInverse(options), responseDetailMapper.mapListInverse(responses))
        }
    }
}