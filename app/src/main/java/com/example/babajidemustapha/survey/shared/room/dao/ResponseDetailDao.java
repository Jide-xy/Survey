package com.example.babajidemustapha.survey.shared.room.dao;

import androidx.room.Dao;
import androidx.room.Query;
import androidx.room.RoomWarnings;

import com.example.babajidemustapha.survey.shared.models.QuestionAndResponse;

import java.util.List;

@Dao
public abstract class ResponseDetailDao extends SurveyDao {

    @SuppressWarnings(RoomWarnings.CURSOR_MISMATCH)
    @Query("SELECT QUESTION.*,RESPONSE_DETAIL.RESPONSE FROM RESPONSE_DETAIL JOIN QUESTION" +
            " ON RESPONSE_DETAIL.OFFLINE_QUESTION_ID = QUESTION.OFFLINE_ID WHERE OFFLINE_RESPONSE_ID = :response_header_id")
    public abstract List<QuestionAndResponse> getResponseDetailsWithRespectiveQuestions(int response_header_id);
}
