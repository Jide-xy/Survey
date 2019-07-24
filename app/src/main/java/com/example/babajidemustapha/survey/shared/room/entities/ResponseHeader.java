package com.example.babajidemustapha.survey.shared.room.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

/**
 * Created by Babajide Mustapha on 9/22/2017.
 */
@Entity(tableName = "RESPONSE", foreignKeys = {
        @ForeignKey(entity = Survey.class,
                parentColumns = "OFFLINE_ID",
                childColumns = "OFFLINE_SURVEY_ID")
})
public class ResponseHeader {
    //    "CREATE TABLE RESPONSE(ONLINE_SURVEY_ID INTEGER," +
//            " ONLINE_ID INTEGER, RESPONDENT_NAME TEXT, RESPONSE_DATE TEXT,SYNCED INTEGER," +
//            "OFFLINE_ID INTEGER PRIMARY KEY, OFFLINE_SURVEY_ID INTEGER, " +
//            "FOREIGN KEY(OFFLINE_SURVEY_ID) REFERENCES SURVEY(OFFLINE_ID))";
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "OFFLINE_ID")
    private int response_id;

    @ColumnInfo(name = "ONLINE_ID")
    private int online_response_id;

    @ColumnInfo(name = "RESPONDENT_NAME")
    private String respondentName;

    @ColumnInfo(name = "RESPONSE_DATE")
    private String date;

    @Ignore
    private String username;

    @ColumnInfo(name = "SYNCED")
    private boolean synced = false;

    @ColumnInfo(name = "OFFLINE_SURVEY_ID", index = true)
    private int survey_id;

    @ColumnInfo(name = "ONLINE_SURVEY_ID")
    private int online_survey_id;

    public ResponseHeader() {
    }

    @Ignore
    public ResponseHeader(int survey_id, String respondentName, String date){
        this.respondentName = respondentName;
        this.date = date;
        this.survey_id = survey_id;
    }

    @Ignore
    public ResponseHeader(int response_id, int survey_id, String respondentName, String date){
        this.response_id = response_id;
        this.respondentName = respondentName;
        this.date = date;
        this.survey_id = survey_id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getRespondentName() {
        return respondentName;
    }

    public void setRespondentName(String respondentName) {
        this.respondentName = respondentName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getSurvey_id() {
        return survey_id;
    }

    public void setSurvey_id(int survey_id) {
        this.survey_id = survey_id;
    }


    public int getResponse_id() {
        return response_id;
    }

    public void setResponse_id(int response_id) {
        this.response_id = response_id;
    }

    public boolean isSynced() {
        return synced;
    }

    public void setSynced(boolean synced) {
        this.synced = synced;
    }

    public int getOnline_response_id() {
        return online_response_id;
    }

    public void setOnline_response_id(int online_response_id) {
        this.online_response_id = online_response_id;
    }

    public int getOnline_survey_id() {
        return online_survey_id;
    }

    public void setOnline_survey_id(int online_survey_id) {
        this.online_survey_id = online_survey_id;
    }
}
