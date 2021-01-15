package com.jide.surveyapp.repository.local

import com.jide.surveyapp.repository.local.entity.SurveyEntityWithResponseCount
import com.jide.surveyapp.repository.local.relations.QuestionEntityWithOptions
import com.jide.surveyapp.repository.local.relations.QuestionEntityWithOptionsAndResponse
import com.jide.surveyapp.repository.local.relations.ResponseHeaderEntityWithDetails
import com.jide.surveyapp.repository.local.relations.SurveyEntityWithQuestionsAndOptions
import com.jide.surveyapp.repository.local.utilmodel.ReportEntity
import comjidesurveyapp.*
import kotlinx.coroutines.flow.Flow

interface LocalDb {
    suspend fun getAllSurveys(): Flow<List<SurveyEntity>>
    suspend fun filterSurveys(query: String): Flow<List<SurveyEntityWithResponseCount>>

    //    suspend fun getAllSurveysWithQuestions(): Flow<List<SurveyEntityWithQuestions>>
    suspend fun getAllSurveysWithResponseCount(): Flow<List<SurveyEntityWithResponseCount>>
    suspend fun getQuestionsWithSurveyId(surveyId: String): Flow<List<QuestionEntityWithOptions>>
    suspend fun getResponseHeaderWithSurveyId(surveyId: String): Flow<List<ResponseHeaderEntity>>
    suspend fun getResponseDetailWithHeaderId(responseHeaderId: String): Flow<List<QuestionEntityWithOptionsAndResponse>>

    //    fun getReportWithSurveyId(surveyId: String): Query<GetReportWithSurveyId>
    suspend fun insertSurvey(surveyEntity: SurveyEntity)
    suspend fun insertQuestion(questionEntity: QuestionEntity)
    suspend fun insertResponseHeader(responseHeaderEntity: ResponseHeaderEntity)
    suspend fun insertResponseDetail(responseDetailEntity: ResponseDetailEntity)
    suspend fun insertOption(optionEntity: OptionEntity)
    suspend fun insertSurveyWithQuestions(surveyWithQuestions: List<SurveyEntityWithQuestionsAndOptions>)
    suspend fun insertResponseHeaderWithDetails(responseHeaderEntityWithDetails: List<ResponseHeaderEntityWithDetails>)
    suspend fun getReportWithSurveyId(surveyId: String): Flow<List<ReportEntity>>
}