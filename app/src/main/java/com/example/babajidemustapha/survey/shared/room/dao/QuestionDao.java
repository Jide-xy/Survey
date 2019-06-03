package com.example.babajidemustapha.survey.shared.room.dao;

import androidx.room.Dao;

@Dao
public abstract class QuestionDao extends SurveyDao {
//    @Insert(onConflict = OnConflictStrategy.REPLACE)
//    abstract void insertQuestions(List<Question> questions);
//
//    @Query("SELECT COUNT(Q_NO) AS COUNT FROM QUESTION WHERE OFFLINE_SURVEY_ID = :survey_id")
//    abstract int getSurveyQuestionCount(int survey_id);
//
//    @Query("SELECT * FROM QUESTION WHERE ONLINE_ID = :online_id")
//    abstract Question getQuestionWithOnlineId(int online_id);
}
