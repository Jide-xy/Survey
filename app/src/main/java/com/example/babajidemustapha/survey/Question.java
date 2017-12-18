package com.example.babajidemustapha.survey;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Babajide Mustapha on 9/19/2017.
 */

public class Question implements Serializable {
    private int id;
    private int questionNo;
    private String questionType;
    private int surveyID;
    private List<String> options;
    private boolean mandatory;
    private String questionText;


    public Question(){
        options = new ArrayList<>();
    }
    public Question(int id, int questionNo, String questionType, int surveyID, JSONArray options, boolean mandatory, String questionText){
        //options = new ArrayList<>();
        this.id = id;
        this.questionNo = questionNo;
        this.questionType = questionType;
        this.surveyID = surveyID;
        this.mandatory = mandatory;
        this.questionText = questionText;
        try {
            if(options != null) {
                this.options = new ArrayList<>();
                for (int i = 0; i < options.length(); i++) {
                    Log.e("djnjdn",options.getString(i));
                    this.options.add(options.getString(i));
                }
            }
            else{
               this.options = null;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public int getQuestionNo() {
        return questionNo;
    }

    public void setQuestionNo(int questionNo) {
        this.questionNo = questionNo;
    }

    public String getQuestionType() {
        return questionType;
    }

    public void setQuestionType(String questionType) {
        this.questionType = questionType;
    }

    public int getSurveyID() {
        return surveyID;
    }

    public void setSurveyID(int surveyID) {
        this.surveyID = surveyID;
    }

    public List<String> getOptions() {
        return options;
    }

    public void addOption(String option) {
        this.options.add(option);
    }

    public void setOptions(List<String> options){
        for(String option: options){
            this.options.add("\""+option+"\"");
        }
    }

    public int getOptionCount(){
        return this.options.size();
    }

    public boolean isMandatory() {
        return mandatory;
    }

    public void setMandatory(boolean mandatory) {
        this.mandatory = mandatory;
    }

    public String getQuestionText() {
        return questionText;
    }

    public void setQuestionText(String questionText) {
        this.questionText = questionText;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


}
