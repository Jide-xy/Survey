package com.example.babajidemustapha.survey.features.dashboard.viewmodel

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import com.jide.surveyapp.repository.Repository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest

class SurveyListViewModel @ViewModelInject constructor(private val repository: Repository) : ViewModel() {
    private val surveyQuery = MutableStateFlow<String?>(null)
    val surveyFlow = surveyQuery.flatMapLatest {
        repository.getAllSurveysWithResponseCount(it)
    }

    fun searchSurvey(query: String?) {
        surveyQuery.value = query
    }
}