package com.example.babajidemustapha.survey.features.reports.viewmodel

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import com.jide.surveyapp.repository.Repository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flatMapLatest

class ReportViewModel @ViewModelInject constructor(private val repository: Repository) : ViewModel() {
    private val surveyId = MutableStateFlow<String?>(null)
    val reportFlow = surveyId.flatMapLatest {
        if (it == null) emptyFlow() else repository.getReport(it)
    }

    fun getReport(surveyId: String?) {
        this.surveyId.value = surveyId
    }
}