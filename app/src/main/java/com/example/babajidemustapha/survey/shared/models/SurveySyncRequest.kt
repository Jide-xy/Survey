package com.example.babajidemustapha.survey.shared.models

import com.example.babajidemustapha.survey.shared.room.entities.Question
import com.example.babajidemustapha.survey.shared.room.entities.ResponseDetail
import com.example.babajidemustapha.survey.shared.room.entities.ResponseHeader
import com.example.babajidemustapha.survey.shared.room.entities.Survey
import java.util.*

class SurveySyncRequest {
    var surveys: List<UnsyncedSurveyWithEmbeddedQuestionsAndResponses> = ArrayList()
    var responses: List<ResponseHeaderWithEmbeddedResponseDetails> = ArrayList()

    class UnsyncedSurveyWithEmbeddedQuestionsAndResponses {
        var survey: Survey? = null
        var questions: List<Question> = emptyList()
        var responses: List<ResponseHeaderWithEmbeddedResponseDetails> = emptyList()
    }

    class ResponseHeaderWithEmbeddedResponseDetails {
        var responseHeader: ResponseHeader? = null
        var responseDetails: List<ResponseDetail> = emptyList()
    }

}