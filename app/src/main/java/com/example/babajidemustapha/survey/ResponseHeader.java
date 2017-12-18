package com.example.babajidemustapha.survey;

/**
 * Created by Babajide Mustapha on 9/22/2017.
 */

public class ResponseHeader {
    private int response_id;
    private String respondentName;
    private String date;
    private String username;
    private int survey_id;

    public ResponseHeader(int survey_id, String respondentName, String date){
        this.respondentName = respondentName;
        this.date = date;
        this.survey_id = survey_id;
    }
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
}
