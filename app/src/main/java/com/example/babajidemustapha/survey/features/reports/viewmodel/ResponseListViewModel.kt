package com.example.babajidemustapha.survey.features.reports.viewmodel

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import com.jide.surveyapp.repository.Repository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flatMapLatest

class ResponseListViewModel @ViewModelInject constructor(private val repository: Repository) : ViewModel() {
    private val surveyId = MutableStateFlow<String?>(null)
    val responseHeaderFlow = surveyId.flatMapLatest {
        if (it == null) emptyFlow() else repository.getResponseHeaderWithSurveyId(it)
    }

    private val responseHeaderId = MutableStateFlow<String?>(null)
    val responseDetailFlow = responseHeaderId.flatMapLatest {
        if (it == null) emptyFlow() else repository.getResponseDetailWithHeaderId(it)
    }

    fun getResponseHeaders(surveyId: String?) {
        this.surveyId.value = surveyId
    }

    fun getResponseDetails(id: String) {
        responseHeaderId.value = id
    }
}