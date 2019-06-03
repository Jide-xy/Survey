package com.example.babajidemustapha.survey.shared.room.dao;

import androidx.room.Dao;
import androidx.room.Query;

import com.example.babajidemustapha.survey.QuestionAndResponse;

import java.util.List;

@Dao
public abstract class ResponseDetailDao extends SurveyDao {


    @Query("SELECT QUESTION.OFFLINE_ID,QUESTION.Q_NO,QUESTION.Q_TYPE,QUESTION.OFFLINE_SURVEY_ID," +
            "QUESTION.OPTIONS,QUESTION.MANDATORY,QUESTION.Q_TEXT,RESPONSE_DETAIL.RESPONSE FROM RESPONSE_DETAIL JOIN QUESTION" +
            " ON RESPONSE_DETAIL.OFFLINE_QUESTION_ID = QUESTION.OFFLINE_ID WHERE OFFLINE_RESPONSE_ID = :response_header_id")
    public abstract List<QuestionAndResponse> getResponseDetailsWithRespectiveQuestions(int response_header_id);
}
