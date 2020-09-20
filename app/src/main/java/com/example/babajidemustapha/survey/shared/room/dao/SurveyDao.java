package com.example.babajidemustapha.survey.shared.room.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;

import com.example.babajidemustapha.survey.shared.models.SurveySyncRequest;
import com.example.babajidemustapha.survey.shared.room.entities.Question;
import com.example.babajidemustapha.survey.shared.room.entities.ResponseDetail;
import com.example.babajidemustapha.survey.shared.room.entities.ResponseHeader;
import com.example.babajidemustapha.survey.shared.room.entities.Survey;
import com.example.babajidemustapha.survey.shared.room.entities.SurveyWithResponseHeader;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.example.babajidemustapha.survey.shared.models.SurveySyncRequest.ResponseHeaderWithEmbeddedResponseDetails;
import static com.example.babajidemustapha.survey.shared.models.SurveySyncRequest.UnsyncedSurveyWithEmbeddedQuestionsAndResponses;

@Dao
public abstract class SurveyDao {

    @Transaction
    public void createSurveyWithQuestions(Survey survey, List<Question> questions) {
        survey.setSynced(false);
        long offlineSurveyId = saveOfflineSurvey(survey);
        for (Question question :
                questions) {
            question.setSurveyID((int) offlineSurveyId);
        }
        insertQuestions(questions);
    }

    @Transaction
    public SurveySyncRequest getDataToSync() {

        List<UnsyncedSurveyWithEmbeddedQuestionsAndResponses> unsyncedSurveyWithEmbeddedQuestionsAndResponsesList =
                new ArrayList<>();
        List<ResponseHeaderWithEmbeddedResponseDetails> responseHeaderWithEmbeddedResponseDetailsList =
                new ArrayList<>();
        SurveySyncRequest surveySyncRequest = new SurveySyncRequest();

        List<Survey> surveys = getUnsyncedSurveys();
        for (Survey survey :
                surveys) {
            UnsyncedSurveyWithEmbeddedQuestionsAndResponses unsyncedSurveyWithEmbeddedQuestionsAndResponses =
                    new UnsyncedSurveyWithEmbeddedQuestionsAndResponses();
            unsyncedSurveyWithEmbeddedQuestionsAndResponses.setSurvey(survey);
            unsyncedSurveyWithEmbeddedQuestionsAndResponses.setQuestions(getQuestionsForSurveyWithOfflineId(survey.getId()));

            List<ResponseHeaderWithEmbeddedResponseDetails> responseHeaderWithEmbeddedResponseDetailsInnerList =
                    new ArrayList<>();
            List<ResponseHeader> responseHeaders = getResponseHeadersForSurveyWithOfflineId(survey.getId());
            for (ResponseHeader responseHeader :
                    responseHeaders) {
                ResponseHeaderWithEmbeddedResponseDetails responseHeaderWithEmbeddedResponseDetails =
                        new ResponseHeaderWithEmbeddedResponseDetails();
                responseHeaderWithEmbeddedResponseDetails.setResponseHeader(responseHeader);
                responseHeaderWithEmbeddedResponseDetails.setResponseDetails(getResponseDetailsForResponseHeaderWithOfflineId(
                        responseHeader.getResponseId()
                ));
                responseHeaderWithEmbeddedResponseDetailsInnerList.add(responseHeaderWithEmbeddedResponseDetails);
            }
            unsyncedSurveyWithEmbeddedQuestionsAndResponses.setResponses(responseHeaderWithEmbeddedResponseDetailsInnerList);
            unsyncedSurveyWithEmbeddedQuestionsAndResponsesList.add(unsyncedSurveyWithEmbeddedQuestionsAndResponses);
        }
        surveySyncRequest.setSurveys(unsyncedSurveyWithEmbeddedQuestionsAndResponsesList);

        List<ResponseHeaderWithEmbeddedResponseDetails> responseHeaderWithEmbeddedResponseDetailsInnerList =
                new ArrayList<>();
        List<ResponseHeader> responseHeaders = getUnsyncedResponseHeaders();
        for (ResponseHeader responseHeader :
                responseHeaders) {
            ResponseHeaderWithEmbeddedResponseDetails responseHeaderWithEmbeddedResponseDetails =
                    new ResponseHeaderWithEmbeddedResponseDetails();
            responseHeaderWithEmbeddedResponseDetails.setResponseHeader(responseHeader);
            responseHeaderWithEmbeddedResponseDetails.setResponseDetails(getResponseDetailsForResponseHeaderWithOfflineId(
                    responseHeader.getResponseId()
            ));
            responseHeaderWithEmbeddedResponseDetailsInnerList.add(responseHeaderWithEmbeddedResponseDetails);
        }
        surveySyncRequest.setResponses(responseHeaderWithEmbeddedResponseDetailsInnerList);
        return surveySyncRequest;
    }

    @Transaction
    public void updateSyncedDataWithOnlineIds(SurveySyncRequest surveySyncRequest) {
        for (UnsyncedSurveyWithEmbeddedQuestionsAndResponses survey : surveySyncRequest.getSurveys()) {
            Survey mSurvey = survey.getSurvey();
            mSurvey.setSynced(true);
            saveOfflineSurvey(mSurvey);
            for (Question question : survey.getQuestions()) {
                question.setSynced(true);
                question.setOnlineSurveyID(mSurvey.getOnlineId());
            }
            insertQuestions(survey.getQuestions());
            for (ResponseHeaderWithEmbeddedResponseDetails responseHeaderWithEmbeddedResponseDetails
                    : survey.getResponses()) {
                ResponseHeader responseHeader = responseHeaderWithEmbeddedResponseDetails.getResponseHeader();
                responseHeader.setSynced(true);
                responseHeader.setOnlineSurveyId(mSurvey.getOnlineId());

                for (ResponseDetail responseDetail : responseHeaderWithEmbeddedResponseDetails.getResponseDetails()) {
                    responseDetail.setSynced(true);
                    responseDetail.setOnlineResponseId(responseHeader.getOnlineResponseId());
                }
                saveResponse(responseHeader, responseHeaderWithEmbeddedResponseDetails.getResponseDetails());
            }
        }
    }

    @Transaction
    public void saveOnlineQuestions(List<Question> questions) {
        for (Question question :
                questions) {
            long offlineSurveyId = getSurveyWithOnlineId(question.getOnlineSurveyID()).getId();
            question.setSurveyID((int) offlineSurveyId);
            question.setSynced(true);
        }
        insertQuestions(questions);
    }

    @Transaction
    public long[] saveOnlineResponseHeaders(List<ResponseHeader> responseHeaders) {
        for (ResponseHeader responseHeader :
                responseHeaders) {
            long offlineSurveyId = getSurveyWithOnlineId(responseHeader.getOnlineSurveyId()).getId();
            responseHeader.setSurveyId((int) offlineSurveyId);
            responseHeader.setSynced(true);
        }
        return insertResponseHeaders(responseHeaders);
    }

    @Transaction
    public void saveOnlineResponseDetails(List<ResponseDetail> responseDetails) {
        for (ResponseDetail responseDetail :
                responseDetails) {
            long offlineQuestionId = getQuestionWithOnlineId(responseDetail.getOnlineQuestionId()).getId();
            long offlineResponseHeaderId = getResponseHeaderWithOnlineId(responseDetail.getOnlineResponseId()).getResponseId();
            responseDetail.setQuestionId((int) offlineQuestionId);
            responseDetail.setResponseId((int) offlineResponseHeaderId);
            responseDetail.setSynced(true);
        }
        insertResponseDetails(responseDetails);
    }

    @Transaction
    public void saveResponse(ResponseHeader responseHeader, List<ResponseDetail> responseDetails) {
        long responseHeaderId = saveResponseHeader(responseHeader);
        for (ResponseDetail responseDetail :
                responseDetails) {
            responseDetail.setResponseId((int) responseHeaderId);
            responseDetail.setOnlineQuestionId(
                    getQuestionFromOfflineId(responseDetail.getQuestionId()).getOnlineId());
            responseDetail.setOnlineResponseId(
                    getResponseHeaderFromOfflineId(responseHeaderId).getOnlineResponseId()
            );
        }
        insertResponseDetails(responseDetails);

    }

    @Transaction
    public Map<Question, List<ResponseDetail>> generateReport(int survey_id) {
        Map<Question, List<ResponseDetail>> report = new LinkedHashMap<>();
        List<Question> questions = getQuestionsForSurveyWithOfflineId(survey_id);
        for (Question question :
                questions) {
            List<ResponseDetail> responseDetails = getResponseDetailsWithQuestionId(question.getId());
            report.put(question, responseDetails);
        }
        return report;
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void saveOnlineSurvey(List<Survey> surveys);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract long saveOfflineSurvey(Survey survey);

    @Query("SELECT * FROM SURVEY")
    public abstract List<Survey> getAllSurveys();

    @Transaction
    @Query("SELECT * FROM SURVEY")
    public abstract List<SurveyWithResponseHeader> getAllSurveysWithResponse();

    @Query("SELECT * FROM SURVEY WHERE ONLINE_ID = :online_id")
    public abstract Survey getSurveyWithOnlineId(int online_id);

    @Query("SELECT * FROM QUESTION WHERE ONLINE_ID = :online_id")
    abstract Question getQuestionWithOnlineId(int online_id);

    @Query("SELECT * FROM RESPONSE WHERE ONLINE_ID = :online_id")
    abstract ResponseHeader getResponseHeaderWithOnlineId(long online_id);

    @Query("SELECT * FROM SURVEY WHERE NAME LIKE '%' || :query || '%' OR DESCRIPTION LIKE '%' || :query || '%' ")
    public abstract List<Survey> searchSurveys(String query);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void insertQuestions(List<Question> questions);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract long[] insertResponseHeaders(List<ResponseHeader> responseHeaders);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void insertResponseDetails(List<ResponseDetail> responseDetails);

    //================================================SYNC ASPECT
    @Query("SELECT * FROM SURVEY WHERE SYNCED = 0")
    public abstract List<Survey> getUnsyncedSurveys();

    @Query("SELECT * FROM QUESTION WHERE OFFLINE_SURVEY_ID = :id ORDER BY Q_NO ASC ")
    public abstract List<Question> getQuestionsForSurveyWithOfflineId(long id);

    @Query("SELECT * FROM RESPONSE WHERE OFFLINE_SURVEY_ID = :id AND ONLINE_SURVEY_ID IS NULL")
    public abstract List<ResponseHeader> getResponseHeadersForSurveyWithOfflineId(long id);

    @Query("SELECT * FROM RESPONSE_DETAIL WHERE OFFLINE_RESPONSE_ID = :id")
    public abstract List<ResponseDetail> getResponseDetailsForResponseHeaderWithOfflineId(long id);

    @Query("SELECT * FROM RESPONSE WHERE SYNCED = 0 AND ONLINE_SURVEY_ID != 0")
    public abstract List<ResponseHeader> getUnsyncedResponseHeaders();

    //================================================REPORT ASPECT
    @Query("SELECT * FROM RESPONSE_DETAIL WHERE OFFLINE_QUESTION_ID = :question_id")
    public abstract List<ResponseDetail> getResponseDetailsWithQuestionId(int question_id);

    //================================================ON LOGIN CACHE DATA ASPECT


    //REGION START=====================RESPONSE HEADER
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract long saveResponseHeader(ResponseHeader responseHeader);

    @Query("SELECT * FROM RESPONSE WHERE OFFLINE_ID = :id")
    abstract ResponseHeader getResponseHeaderFromOfflineId(long id);

    @Query("SELECT * FROM QUESTION WHERE OFFLINE_ID = :id")
    abstract Question getQuestionFromOfflineId(long id);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract long[] saveResponseDetail(List<ResponseDetail> responseDetails);
    //ENDREGION
}
