package com.jide.surveyapp.util.mapper

import com.jide.surveyapp.model.Survey
import com.jide.surveyapp.model.relations.QuestionWithOptions
import com.jide.surveyapp.model.relations.SurveyWithQuestionsAndOptions
import com.jide.surveyapp.repository.local.relations.QuestionEntityWithOptions
import com.jide.surveyapp.repository.local.relations.SurveyEntityWithQuestionsAndOptions
import com.jide.surveyapp.util.Mapper
import comjidesurveyapp.SurveyEntity

class SurveyWithQuestionsMapper(
        private val surveyMapper: Mapper<SurveyEntity, Survey>,
        private val questionMapper: Mapper<QuestionEntityWithOptions, QuestionWithOptions>
) : Mapper<SurveyEntityWithQuestionsAndOptions, SurveyWithQuestionsAndOptions> {
    override suspend fun map(from: SurveyEntityWithQuestionsAndOptions): SurveyWithQuestionsAndOptions {
        return with(from) {
            SurveyWithQuestionsAndOptions(surveyMapper.map(survey), questionMapper.mapList(questions))
        }
    }

    override suspend fun mapInverse(from: SurveyWithQuestionsAndOptions): SurveyEntityWithQuestionsAndOptions {
        return with(from) {
            SurveyEntityWithQuestionsAndOptions(surveyMapper.mapInverse(survey), questionMapper.mapListInverse(questions))
        }
    }
}