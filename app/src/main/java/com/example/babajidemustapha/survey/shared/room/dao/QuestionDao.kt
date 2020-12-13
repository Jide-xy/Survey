package com.example.babajidemustapha.survey.shared.room.dao

import androidx.room.Dao
import androidx.room.Query
import com.example.babajidemustapha.survey.shared.room.db.SurveyDatabase

@Dao
abstract class QuestionDao(db: SurveyDatabase) {
    //    @Insert(onConflict = OnConflictStrategy.REPLACE)
    //    abstract void insertQuestions(List<Question> questions);
    //
    @Query("SELECT COUNT(Q_NO) AS COUNT FROM QUESTION WHERE OFFLINE_SURVEY_ID = :survey_id")
    abstract fun getSurveyQuestionCount(survey_id: Int): Int //
    //    @Query("SELECT * FROM QUESTION WHERE ONLINE_ID = :online_id")
    //    abstract Question getQuestionWithOnlineId(int online_id);
}