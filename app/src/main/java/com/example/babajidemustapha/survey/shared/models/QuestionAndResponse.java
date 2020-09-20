package com.example.babajidemustapha.survey.shared.models;

import androidx.room.ColumnInfo;

import com.example.babajidemustapha.survey.shared.room.entities.Question;

/**
 * Created by Babajide Mustapha on 9/27/2017.
 */

public class QuestionAndResponse extends Question {

    @ColumnInfo(name = "RESPONSE")
    private String response;

    public QuestionAndResponse() {

    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }
}
