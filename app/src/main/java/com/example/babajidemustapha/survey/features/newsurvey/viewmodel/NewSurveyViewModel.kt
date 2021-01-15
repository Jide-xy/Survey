package com.example.babajidemustapha.survey.features.newsurvey.viewmodel

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jide.surveyapp.model.relations.SurveyWithQuestionsAndOptions
import com.jide.surveyapp.repository.Repository
import kotlinx.coroutines.launch

class NewSurveyViewModel @ViewModelInject constructor(private val repository: Repository) : ViewModel() {

    fun createSurvey(survey: SurveyWithQuestionsAndOptions) {
        viewModelScope.launch {
            repository.insertSurveyWithQuestions(listOf(survey))
        }
    }
}