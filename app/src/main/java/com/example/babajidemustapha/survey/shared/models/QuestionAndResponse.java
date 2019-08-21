package com.example.babajidemustapha.survey.shared.models;

import androidx.room.ColumnInfo;

import com.example.babajidemustapha.survey.shared.room.entities.Question;

/**
 * Created by Babajide Mustapha on 9/27/2017.
 */

public class QuestionAndResponse extends Question {
//    @ColumnInfo(name = "OFFLINE_ID")
//    private int id;
//
//    @ColumnInfo(name = "Q_NO")
//    private int questionNo;
//
//    @ColumnInfo(name = "Q_TYPE")
//    private String questionType;
//
//    @ColumnInfo(name = "OFFLINE_SURVEY_ID")
//    private int surveyID;
//
//    @TypeConverters(StringListConverter.class)
//    @ColumnInfo(name = "OPTIONS")
//    private List<String> options;
//
//    @ColumnInfo(name = "MANDATORY")
//    private boolean mandatory;
//
//    @ColumnInfo(name = "Q_TEXT")
//    private String questionText;

    @ColumnInfo(name = "RESPONSE")
    private String response;


    public QuestionAndResponse() {

    }

//    @Ignore
//    public QuestionAndResponse(int id, int questionNo, String questionType, int surveyID, JSONArray options, boolean mandatory, String questionText, String response){
//        //options = new ArrayList<>();
//        this.id = id;
//        this.questionNo = questionNo;
//        this.questionType = questionType;
//        this.surveyID = surveyID;
//        this.mandatory = mandatory;
//        this.questionText = questionText;
//        this.response = response;
//        try {
//            if(options != null) {
//                this.options = new ArrayList<>();
//                for (int i = 0; i < options.length(); i++) {
//                    this.options.add(options.getString(i));
//                }
//            } else{
//                this.options = null;
//            }
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//    }
//
//    public int getQuestionNo() {
//        return questionNo;
//    }
//
//    public void setQuestionNo(int questionNo) {
//        this.questionNo = questionNo;
//    }
//
//    public String getQuestionType() {
//        return questionType;
//    }
//
//    public void setQuestionType(String questionType) {
//        this.questionType = questionType;
//    }
//
//    public int getSurveyID() {
//        return surveyID;
//    }
//
//    public void setSurveyID(int surveyID) {
//        this.surveyID = surveyID;
//    }
//
//    public List<String> getOptions() {
//        return options;
//    }
//
//    public void setOptions(List<String> options) {
//        this.options = options;
//    }
//
//    public void addOption(String option) {
//        this.options.add(option);
//    }
//
//    public int getOptionCount(){
//        return this.options.size();
//    }
//
//    public boolean isMandatory() {
//        return mandatory;
//    }
//
//    public void setMandatory(boolean mandatory) {
//        this.mandatory = mandatory;
//    }
//
//    public String getQuestionText() {
//        return questionText;
//    }
//
//    public void setQuestionText(String questionText) {
//        this.questionText = questionText;
//    }
//
//    public int getId() {
//        return id;
//    }
//
//    public void setId(int id) {
//        this.id = id;
//    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }
}
