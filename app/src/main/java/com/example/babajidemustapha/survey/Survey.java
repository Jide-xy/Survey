package com.example.babajidemustapha.survey;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Babajide Mustapha on 9/19/2017.
 */

public class Survey {
    private int id;
    private String name;
    private boolean privacy;
    private String username;
    private String date;
    private int no_of_ques;
    private String desc;
    private List<Question> questions;
    private String device_token;

    public Survey(){

    }

    public Survey(int id, String name, boolean privacy, String date, int no_of_ques, String desc){
        this.id = id;
        this.name = name;
        this.privacy = privacy;
        this.date = date;
        this.no_of_ques = no_of_ques;
        this.desc = desc;
        questions = new ArrayList<>();
    }
    public Survey(int id, String name, String date,String desc,String username, String device_token){
        this.id = id;
        this.name = name;
        this.username = username;
        this.device_token = device_token;
        this.date = date;
        this.desc = desc;
        questions = new ArrayList<>();
    }

    public void addQuestion(Question question){
        questions.add(question);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isPrivate() {
        return privacy;
    }

    public void setPrivacy(boolean privacy) {
        this.privacy = privacy;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getNoOfQues() {
        return no_of_ques;
    }

    public void setNoOFQues(int ques) {
        this.no_of_ques = ques;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public List<Question> getQuestions() {
        return questions;
    }

    public void setQuestions(List<Question> questions) {
        this.questions = questions;
    }

    public String getDevice_token() {
        return device_token;
    }

    public void setDevice_token(String device_token) {
        this.device_token = device_token;
    }
}
