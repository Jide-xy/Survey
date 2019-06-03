package com.example.babajidemustapha.survey.shared.room.dao;

import androidx.room.Dao;

@Dao
public abstract class ResponseHeaderDao extends SurveyDao {

//    @Transaction
//    public void saveResponse(ResponseHeader responseHeader, List<ResponseDetail> responseDetails){
//        long responseHeaderId = saveResponseHeader(responseHeader);
//        for (ResponseDetail responseDetail :
//                responseDetails) {
//            responseDetail.setResponse_id((int) responseHeaderId);
//            responseDetail.setOnline_question_id(
//                    getQuestionFromOfflineId(responseDetail.getQuestion_id()).getOnline_id());
//            responseDetail.setOnline_response_id(
//                    getResponseHeaderFromOfflineId(responseHeaderId).getOnline_response_id()
//            );
//        }
//        saveResponseDetail(responseDetails);
//
//    }
//
//    @Query("SELECT COUNT(OFFLINE_ID) AS RESPONSE_COUNT FROM RESPONSE WHERE OFFLINE_SURVEY_ID = :survey_id ")
//    abstract int getResponseHeaderCount(int survey_id);
//
//    @Insert(onConflict = OnConflictStrategy.REPLACE)
//    abstract long[] saveResponseHeaders(List<ResponseHeader> responseHeaders);
//
//    @Insert(onConflict = OnConflictStrategy.REPLACE)
//    abstract long saveResponseHeader(ResponseHeader responseHeader);
//
//    @Query("SELECT * FROM RESPONSE WHERE ONLINE_ID = :online_id")
//    abstract ResponseHeader getResponseHeaderWithOnlineId(long online_id);
//
//    @Query("SELECT * FROM RESPONSE WHERE OFFLINE_SURVEY_ID = :survey_id ORDER BY OFFLINE_ID DESC")
//    abstract List<ResponseHeader> getResponseHeaders(int survey_id);
//
//    @Query("SELECT * FROM SURVEY WHERE OFFLINE_ID = :id")
//    abstract Survey getSurveyFromOfflineId(long id);
//
//    @Query("SELECT * FROM RESPONSE WHERE OFFLINE_ID = :id")
//    abstract ResponseHeader getResponseHeaderFromOfflineId(long id);

//    @Query("SELECT * FROM RESPONSE WHERE OFFLINE_SURVEY_ID = :survey_id ORDER BY OFFLINE_ID DESC")
//    abstract List<ResponseHeader> getResponseDetailFromOfflineId(long id);

//    @Query("SELECT * FROM QUESTION WHERE OFFLINE_ID = :id")
//    abstract Question getQuestionFromOfflineId(long id);
//
//    @Insert(onConflict = OnConflictStrategy.REPLACE)
//    abstract long[] saveResponseDetail(List<ResponseDetail> responseDetails);
}
