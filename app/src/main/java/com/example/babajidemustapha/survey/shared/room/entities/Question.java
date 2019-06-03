package com.example.babajidemustapha.survey.shared.room.entities;

import android.util.Log;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.example.babajidemustapha.survey.shared.room.StringListConverter;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Babajide Mustapha on 9/19/2017.
 */
@Entity(tableName = "QUESTION", foreignKeys = @ForeignKey(entity = Survey.class,
        parentColumns = "OFFLINE_ID",
        childColumns = "OFFLINE_SURVEY_ID"))
public class Question implements Serializable {
    //    "CREATE TABLE QUESTION(OFFLINE_SURVEY_ID INTEGER, " +
//            "ONLINE_SURVEY_ID INTEGER, Q_NO INTEGER, Q_TYPE TEXT, MANDATORY INTEGER, Q_TEXT TEXT, OPTIONS TEXT," +
//            "OFFLINE_ID INTEGER PRIMARY KEY, ONLINE_ID INTEGER,SYNCED INTEGER," +
//            "FOREIGN KEY(OFFLINE_SURVEY_ID) REFERENCES SURVEY(OFFLINE_ID))";
    @ColumnInfo(name = "OFFLINE_ID")
    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "ONLINE_ID")
    private int online_id;

    @ColumnInfo(name = "Q_NO")
    private int questionNo;

    @ColumnInfo(name = "Q_TYPE")
    private String questionType;

    @ColumnInfo(name = "OFFLINE_SURVEY_ID")
    private int surveyID;

    @ColumnInfo(name = "ONLINE_SURVEY_ID")
    private int onlineSurveyID;

    @TypeConverters(StringListConverter.class)
    @ColumnInfo(name = "OPTIONS")
    private List<String> options;

    @ColumnInfo(name = "MANDATORY")
    private boolean mandatory;

    @ColumnInfo(name = "Q_TEXT")
    private String questionText;

    @ColumnInfo(name = "SYNCED")
    private boolean synced = false;


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


    public int getOnline_id() {
        return online_id;
    }

    public void setOnline_id(int online_id) {
        this.online_id = online_id;
    }

    public int getOnlineSurveyID() {
        return onlineSurveyID;
    }

    public void setOnlineSurveyID(int onlineSurveyID) {
        this.onlineSurveyID = onlineSurveyID;
    }

    public boolean isSynced() {
        return synced;
    }

    public void setSynced(boolean synced) {
        this.synced = synced;
    }
}
