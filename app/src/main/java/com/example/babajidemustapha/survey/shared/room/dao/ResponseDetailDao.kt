package com.example.babajidemustapha.survey.shared.room.dao

import androidx.room.Dao
import androidx.room.Query
import com.example.babajidemustapha.survey.shared.models.QuestionAndResponse
import com.example.babajidemustapha.survey.shared.room.db.SurveyDatabase

@Dao
abstract class ResponseDetailDao(db: SurveyDatabase) {
    @Query("SELECT QUESTION.*,RESPONSE_DETAIL.RESPONSE FROM RESPONSE_DETAIL JOIN QUESTION" +
            " ON RESPONSE_DETAIL.OFFLINE_QUESTION_ID = QUESTION.OFFLINE_ID WHERE OFFLINE_RESPONSE_ID = :response_header_id")
    abstract fun getResponseDetailsWithRespectiveQuestions(response_header_id: Int): List<QuestionAndResponse?>?
}