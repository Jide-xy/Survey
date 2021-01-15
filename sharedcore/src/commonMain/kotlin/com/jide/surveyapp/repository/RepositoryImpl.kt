package com.jide.surveyapp.repository

import com.jide.surveyapp.model.ResponseHeader
import com.jide.surveyapp.model.Survey
import com.jide.surveyapp.model.relations.*
import com.jide.surveyapp.repository.local.LocalDb
import com.jide.surveyapp.repository.local.entity.SurveyEntityWithResponseCount
import com.jide.surveyapp.repository.local.relations.QuestionEntityWithOptions
import com.jide.surveyapp.repository.local.relations.QuestionEntityWithOptionsAndResponse
import com.jide.surveyapp.repository.local.relations.ResponseHeaderEntityWithDetails
import com.jide.surveyapp.repository.local.relations.SurveyEntityWithQuestionsAndOptions
import com.jide.surveyapp.repository.local.utilmodel.ReportEntity
import com.jide.surveyapp.util.Mapper
import comjidesurveyapp.ResponseHeaderEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class RepositoryImpl(
        private val localDb: LocalDb,
        private val surveyMapper: Mapper<SurveyEntityWithResponseCount, Survey>,
        private val questionMapper: Mapper<QuestionEntityWithOptions, QuestionWithOptions>,
        private val responseHeaderMapper: Mapper<ResponseHeaderEntity, ResponseHeader>,
        private val questionWithResponseMapper: Mapper<QuestionEntityWithOptionsAndResponse, QuestionWithOptionsAndResponse>,
        private val surveyWithQuestionsMapper: Mapper<SurveyEntityWithQuestionsAndOptions, SurveyWithQuestionsAndOptions>,
        private val responseHeaderWithDetailsMapper: Mapper<ResponseHeaderEntityWithDetails, ResponseHeaderWithDetails>,
        private val reportMapper: Mapper<ReportEntity, Report>
) : Repository {
    override suspend fun getAllSurveysWithResponseCount(query: String?): Flow<List<Survey>> {
        return if (query.isNullOrBlank()) localDb.getAllSurveysWithResponseCount().map { surveyMapper.mapList(it) } else localDb.filterSurveys(query).map { surveyMapper.mapList(it) }
    }

    override suspend fun getQuestionsWithSurveyId(surveyId: String): Flow<List<QuestionWithOptions>> {
        return localDb.getQuestionsWithSurveyId(surveyId).map { questionMapper.mapList(it) }
    }

    override suspend fun getResponseHeaderWithSurveyId(surveyId: String): Flow<List<ResponseHeader>> {
        return localDb.getResponseHeaderWithSurveyId(surveyId).map { responseHeaderMapper.mapList(it) }
    }

    override suspend fun getResponseDetailWithHeaderId(responseHeaderId: String): Flow<List<QuestionWithOptionsAndResponse>> {
        return localDb.getResponseDetailWithHeaderId(responseHeaderId).map { questionWithResponseMapper.mapList(it) }
    }

    override suspend fun getReport(surveyId: String): Flow<List<Report>> {
        return localDb.getReportWithSurveyId(surveyId).map { reportMapper.mapList(it) }
    }

    override suspend fun insertSurveyWithQuestions(surveyWithQuestions: List<SurveyWithQuestionsAndOptions>) {
        localDb.insertSurveyWithQuestions(surveyWithQuestionsMapper.mapListInverse(surveyWithQuestions))
    }

    override suspend fun insertResponseHeaderWithDetails(responseHeaderEntityWithDetails: List<ResponseHeaderWithDetails>) {
        localDb.insertResponseHeaderWithDetails(responseHeaderWithDetailsMapper.mapListInverse(responseHeaderEntityWithDetails))
    }
}