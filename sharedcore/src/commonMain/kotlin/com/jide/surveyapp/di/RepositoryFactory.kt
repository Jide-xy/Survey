package com.jide.surveyapp.di

import com.jide.surveyapp.AppDatabase
import com.jide.surveyapp.repository.Repository
import com.jide.surveyapp.repository.RepositoryImpl
import com.jide.surveyapp.repository.local.DatabaseDriverFactory
import com.jide.surveyapp.repository.local.LocalDbImpl
import com.jide.surveyapp.util.DefaultDispatcherProvider
import com.jide.surveyapp.util.mapper.*
import com.squareup.sqldelight.EnumColumnAdapter
import com.squareup.sqldelight.logs.LogSqliteDriver
import comjidesurveyapp.QuestionEntity

object RepositoryFactory {


    fun create(databaseDriverFactory: DatabaseDriverFactory): Repository {
        val appDatabase = AppDatabase(LogSqliteDriver(databaseDriverFactory.createDriver()) { println("SqlDelight: ${it.replace("\n", "")}") }, QuestionEntity.Adapter(EnumColumnAdapter()))
        val dispatcherProvider = DefaultDispatcherProvider()
        val localDb = LocalDbImpl(appDatabase, dispatcherProvider)
        val surveyMapper = SurveyMapper
        val questionWithoutOptionsMapper = QuestionWithoutOptionsMapper
        val optionsMapper = OptionMapper
        val questionMapper = QuestionMapper(questionWithoutOptionsMapper, optionsMapper)
        val responseHeaderMapper = ResponseHeaderMapper
        val responseDetailMapper = ResponseDetailMapper
        val questionWithResponseMapper = QuestionWithResponseMapper(questionWithoutOptionsMapper, optionsMapper, responseDetailMapper)
        val surveyWithoutResponseCountMapper = SurveyWithoutResponseCountMapper
        val surveyWithQuestionsMapper = SurveyWithQuestionsMapper(surveyWithoutResponseCountMapper, questionMapper)
        val responseHeaderWithDetailsMapper = ResponseHeaderWithDetailsMapper(responseHeaderMapper, responseDetailMapper)
        val reportMapper = ReportMapper(questionWithoutOptionsMapper, optionsMapper)

        return RepositoryImpl(localDb, surveyMapper, questionMapper, responseHeaderMapper, questionWithResponseMapper, surveyWithQuestionsMapper, responseHeaderWithDetailsMapper, reportMapper)
    }
}