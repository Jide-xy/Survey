package com.jide.surveyapp.repository

import com.jide.surveyapp.model.ResponseHeader
import com.jide.surveyapp.model.Survey
import com.jide.surveyapp.model.relations.*
import kotlinx.coroutines.flow.Flow

interface Repository {
    suspend fun getAllSurveysWithResponseCount(query: String? = null): Flow<List<Survey>>
    suspend fun getQuestionsWithSurveyId(surveyId: String): Flow<List<QuestionWithOptions>>
    suspend fun getResponseHeaderWithSurveyId(surveyId: String): Flow<List<ResponseHeader>>
    suspend fun getResponseDetailWithHeaderId(responseHeaderId: String): Flow<List<QuestionWithOptionsAndResponse>>
    suspend fun getReport(surveyId: String): Flow<List<Report>>
    suspend fun insertSurveyWithQuestions(surveyWithQuestions: List<SurveyWithQuestionsAndOptions>)
    suspend fun insertResponseHeaderWithDetails(responseHeaderEntityWithDetails: List<ResponseHeaderWithDetails>)
}