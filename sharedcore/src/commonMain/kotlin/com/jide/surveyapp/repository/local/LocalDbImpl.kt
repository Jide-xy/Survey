package com.jide.surveyapp.repository.local

import com.jide.surveyapp.AppDatabase
import com.jide.surveyapp.model.QuestionType
import com.jide.surveyapp.repository.local.entity.SurveyEntityWithResponseCount
import com.jide.surveyapp.repository.local.relations.QuestionEntityWithOptions
import com.jide.surveyapp.repository.local.relations.QuestionEntityWithOptionsAndResponse
import com.jide.surveyapp.repository.local.relations.ResponseHeaderEntityWithDetails
import com.jide.surveyapp.repository.local.relations.SurveyEntityWithQuestionsAndOptions
import com.jide.surveyapp.repository.local.utilmodel.ReportEntity
import com.jide.surveyapp.repository.local.utilmodel.UtilOptionAndResponse
import com.jide.surveyapp.repository.local.utilmodel.UtilQuestionWithOption
import com.jide.surveyapp.util.DispatcherProvider
import com.squareup.sqldelight.runtime.coroutines.asFlow
import com.squareup.sqldelight.runtime.coroutines.mapToList
import comjidesurveyapp.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class LocalDbImpl(
        private val appDatabase: AppDatabase,
        private val dispatcherProvider: DispatcherProvider
) : LocalDb {

    override suspend fun getAllSurveys(): Flow<List<SurveyEntity>> {
        return withContext((dispatcherProvider.io())) {
            appDatabase.appDatabaseQueries.getAllSurveys().asFlow().mapToList()
        }
    }

    override suspend fun filterSurveys(query: String): Flow<List<SurveyEntityWithResponseCount>> {
        return withContext((dispatcherProvider.io())) {
            appDatabase.appDatabaseQueries.filterSurveys(query) { id, name, description, shared, backedUp, dateCreated, responseCount ->
                SurveyEntityWithResponseCount(id, name, description, shared, backedUp, dateCreated, responseCount.toInt())
            }.asFlow().mapToList()
        }
    }

    override suspend fun getAllSurveysWithResponseCount(): Flow<List<SurveyEntityWithResponseCount>> {
        return withContext((dispatcherProvider.io())) {
            appDatabase.appDatabaseQueries.getAllSurveysWithResponseCount { id, name, description, shared, backedUp, dateCreated, responseCount ->
                SurveyEntityWithResponseCount(id, name, description, shared, backedUp, dateCreated, responseCount.toInt())
            }.asFlow().mapToList()
        }
    }

    override suspend fun getQuestionsWithSurveyId(surveyId: String): Flow<List<QuestionEntityWithOptions>> {
        return withContext((dispatcherProvider.io())) {
            appDatabase.appDatabaseQueries.getQuestionsWithSurveyId(surveyId).asFlow().mapToList().map { questions ->
                questions.groupBy({ QuestionEntity(it.id, it.questionNo, it.questionType, it.mandatory, it.questionText, it.surveyId) },
                        { OptionEntity(it.id_, it.optionNo, it.optionText, it.questionId) })
                        .map { QuestionEntityWithOptions(it.key, it.value) }
            }
        }
    }

    override suspend fun getResponseHeaderWithSurveyId(surveyId: String): Flow<List<ResponseHeaderEntity>> {
        return withContext((dispatcherProvider.io())) {
            appDatabase.appDatabaseQueries.getResponseHeaderWithSurveyId(surveyId).asFlow().mapToList()
        }
    }

    override suspend fun getResponseDetailWithHeaderId(responseHeaderId: String): Flow<List<QuestionEntityWithOptionsAndResponse>> {
        return withContext((dispatcherProvider.io())) {
            appDatabase.appDatabaseQueries.getResponseDetailWithHeaderId(responseHeaderId).asFlow().mapToList().map { response ->
                response.groupBy {
                    QuestionEntity(it.questionMainId, it.questionNo, it.questionType, it.mandatory, it.questionText, it.surveyId)
                }.map {
                    QuestionEntityWithOptionsAndResponse(it.key,
                            it.value.distinctBy { option -> option.optionMainId }
                                    .map { option -> OptionEntity(option.optionMainId, option.optionNo, option.optionText, option.questionId) },
                            it.value.distinctBy { responseDetail -> responseDetail.responseDetaildId }
                                    .mapNotNull { rd -> if (rd.responseDetaildId == null) null else ResponseDetailEntity(rd.responseDetaildId, rd.responseHeaderId!!, rd.optionId!!, rd.freeTextResponse) }
                    )
                }
            }.flowOn(dispatcherProvider.io())
        }
    }

    override suspend fun getReportWithSurveyId(surveyId: String): Flow<List<ReportEntity>> {
        return withContext((dispatcherProvider.io())) {
            appDatabase.appDatabaseQueries.getReportWithSurveyId(surveyId, mapper = { id, questionNo, questionType, mandatory, questionText, surveyId, id_, optionNo, optionText, questionId, id__, responseHeaderId, optionId, freeTextResponse, optionMainId, questionMainId, responseDetaildId ->
                    UtilQuestionWithOption(
                            QuestionEntity(questionMainId, questionNo, questionType, mandatory, questionText, surveyId),
                            UtilOptionAndResponse(OptionEntity(optionMainId!!, optionNo!!,
                                    if (questionType == QuestionType.SINGLE_OPTION || questionType == QuestionType.MULTIPLE_OPTION) optionText else freeTextResponse, questionId!!),
                                    responseDetaildId)
                    )
            }).asFlow().mapToList().map { report ->
                report.groupBy({ it.question }, { it.options }).map { map ->
                    ReportEntity(map.key,
                            map.value.groupBy { it.option }
                                    .map {
                                            Pair(it.key,
                                                    it.value.filterNot { option -> option.responseDetailId.isNullOrBlank() }.size)
                                    })
                }
            }.flowOn(dispatcherProvider.io())
        }
    }


    override suspend fun insertSurvey(surveyEntity: SurveyEntity) {
        return withContext((dispatcherProvider.io())) {
            appDatabase.appDatabaseQueries.insertSurvey(surveyEntity)
        }
    }

    override suspend fun insertQuestion(questionEntity: QuestionEntity) {
        return withContext((dispatcherProvider.io())) {
            appDatabase.appDatabaseQueries.insertQuestion(questionEntity)
        }
    }

    override suspend fun insertResponseHeader(responseHeaderEntity: ResponseHeaderEntity) {
        return withContext((dispatcherProvider.io())) {
            appDatabase.appDatabaseQueries.insertResponseHeader(responseHeaderEntity)
        }
    }

    override suspend fun insertResponseDetail(responseDetailEntity: ResponseDetailEntity) {
        return withContext((dispatcherProvider.io())) {
            appDatabase.appDatabaseQueries.insertResponseDetail(responseDetailEntity)
        }
    }

    override suspend fun insertOption(optionEntity: OptionEntity) {
        return withContext((dispatcherProvider.io())) {
            appDatabase.appDatabaseQueries.insertOption(optionEntity)
        }
    }

    override suspend fun insertSurveyWithQuestions(surveyWithQuestions: List<SurveyEntityWithQuestionsAndOptions>) {
        withContext(dispatcherProvider.io()) {
            appDatabase.appDatabaseQueries.transaction(true) {
                surveyWithQuestions.forEach { surveyEntityWithQuestionsAndOptions ->
                    appDatabase.appDatabaseQueries.insertSurvey(surveyEntityWithQuestionsAndOptions.survey)
                    surveyEntityWithQuestionsAndOptions.questions.forEach { questionEntityWithOptions ->
                        appDatabase.appDatabaseQueries.insertQuestion(questionEntityWithOptions.question.copy(surveyId = surveyEntityWithQuestionsAndOptions.survey.id))
                        questionEntityWithOptions.options.forEach { optionEntity ->
                            appDatabase.appDatabaseQueries.insertOption(optionEntity.copy(questionId = questionEntityWithOptions.question.id))
                        }
                    }
                }
            }
        }
    }

    override suspend fun insertResponseHeaderWithDetails(responseHeaderEntityWithDetails: List<ResponseHeaderEntityWithDetails>) {
        withContext(dispatcherProvider.io()) {
            appDatabase.appDatabaseQueries.transaction {
                responseHeaderEntityWithDetails.forEach { response ->
                    appDatabase.appDatabaseQueries.insertResponseHeader(response.responseHeaderEntity)
                    response.responseDetailEntities.forEach { responseDetailEntity ->
                        appDatabase.appDatabaseQueries.insertResponseDetail(responseDetailEntity.copy(responseHeaderId = response.responseHeaderEntity.id))
                    }
                }
            }
        }
    }

}