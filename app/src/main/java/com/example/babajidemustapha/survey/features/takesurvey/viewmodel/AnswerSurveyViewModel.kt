package com.example.babajidemustapha.survey.features.takesurvey.viewmodel

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jide.surveyapp.model.relations.QuestionWithOptionsAndResponse
import com.jide.surveyapp.model.relations.ResponseHeaderWithDetails
import com.jide.surveyapp.repository.Repository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class AnswerSurveyViewModel @ViewModelInject constructor(private val repository: Repository) : ViewModel() {


    private val surveyId = MutableStateFlow("")
    val questionsFlow: Flow<List<QuestionWithOptionsAndResponse>> = surveyId.flatMapLatest {
        repository.getQuestionsWithSurveyId(it).map { questionWithOptions ->
            questionWithOptions.map { question -> QuestionWithOptionsAndResponse(question.question, question.options, emptyList()) }
        }
    }

    fun saveResponse(response: ResponseHeaderWithDetails) {
        viewModelScope.launch {
            repository.insertResponseHeaderWithDetails(listOf(response))
        }
    }

    fun getQuestions(surveyId: String) {
        this.surveyId.value = surveyId
    }
}