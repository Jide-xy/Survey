package com.jide.surveyapp.util.mapper

import com.jide.surveyapp.model.Option
import com.jide.surveyapp.model.Question
import com.jide.surveyapp.model.relations.Report
import com.jide.surveyapp.repository.local.utilmodel.ReportEntity
import com.jide.surveyapp.util.Mapper
import comjidesurveyapp.OptionEntity
import comjidesurveyapp.QuestionEntity

class ReportMapper(
        private val questionWithoutOptionsMapper: Mapper<QuestionEntity, Question>,
        private val optionsMapper: Mapper<OptionEntity, Option>
) : Mapper<ReportEntity, Report> {
    override suspend fun map(from: ReportEntity): Report {
        return with(from) {
            Report(questionWithoutOptionsMapper.map(question), report.map { Pair(optionsMapper.map(it.first), it.second) })
        }
    }

    override suspend fun mapInverse(from: Report): ReportEntity {
        return with(from) {
            ReportEntity(questionWithoutOptionsMapper.mapInverse(question), report.map { Pair(optionsMapper.mapInverse(it.first), it.second) })
        }
    }
}