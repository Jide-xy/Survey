package com.example.babajidemustapha.survey.shared.room.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

/**
 * Created by Babajide Mustapha on 9/19/2017.
 */
@Entity(tableName = "RESPONSE_DETAIL", foreignKeys = {@ForeignKey(entity = Question.class,
        parentColumns = "OFFLINE_ID",
        childColumns = "OFFLINE_QUESTION_ID"),
        @ForeignKey(entity = ResponseHeader.class,
                parentColumns = "OFFLINE_ID",
                childColumns = "OFFLINE_RESPONSE_ID")
})
public class ResponseDetail {
    //    "CREATE TABLE RESPONSE_DETAIL(OFFLINE_RES_DETAIL_ID INTEGER PRIMARY KEY," +
//            "ONLINE_RESPONSE_ID INTEGER, ONLINE_QUESTION_ID INTEGER, RESPONSE TEXT,"  +
//            "ONLINE_RES_DETAIL_ID INTEGER, OFFLINE_RESPONSE_ID INTEGER, OFFLINE_QUESTION_ID INTEGER,SYNCED INTEGER," +
//            "FOREIGN KEY(OFFLINE_RESPONSE_ID) REFERENCES RESPONSE(OFFLINE_ID)," +
//            "FOREIGN KEY(OFFLINE_QUESTION_ID) REFERENCES QUESTION(OFFLINE_ID))";
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "OFFLINE_RES_DETAIL_ID")
    private int id;

    @ColumnInfo(name = "ONLINE_RES_DETAIL_ID")
    private int online_id;

    @ColumnInfo(name = "OFFLINE_RESPONSE_ID", index = true)
    private int response_id;

    @ColumnInfo(name = "ONLINE_RESPONSE_ID")
    private int online_response_id;

    @ColumnInfo(name = "OFFLINE_QUESTION_ID", index = true)
    private int question_id;

    @ColumnInfo(name = "ONLINE_QUESTION_ID")
    private int online_question_id;

    @ColumnInfo(name = "RESPONSE")
    private String response;

    @ColumnInfo(name = "SYNCED")
    private boolean synced = false;

    public ResponseDetail() {
    }

    @Ignore
    public ResponseDetail(int question_id, String response) {
        this.response = response;
        this.question_id = question_id;
    }

    @Ignore
    public ResponseDetail(int id, int question_id, String response) {
        this.id = id;
        this.response = response;
        this.question_id = question_id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public int getQuestion_id() {
        return question_id;
    }

    public void setQuestion_id(int question_id) {
        this.question_id = question_id;
    }

    public int getOnline_id() {
        return online_id;
    }

    public void setOnline_id(int online_id) {
        this.online_id = online_id;
    }

    public int getResponse_id() {
        return response_id;
    }

    public void setResponse_id(int response_id) {
        this.response_id = response_id;
    }

    public int getOnline_response_id() {
        return online_response_id;
    }

    public void setOnline_response_id(int online_response_id) {
        this.online_response_id = online_response_id;
    }

    public int getOnline_question_id() {
        return online_question_id;
    }

    public void setOnline_question_id(int online_question_id) {
        this.online_question_id = online_question_id;
    }

    public boolean isSynced() {
        return synced;
    }

    public void setSynced(boolean synced) {
        this.synced = synced;
    }
}
