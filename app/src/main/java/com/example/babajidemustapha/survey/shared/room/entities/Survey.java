package com.example.babajidemustapha.survey.shared.room.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Babajide Mustapha on 9/19/2017.
 */
@Entity(tableName = "SURVEY")
public class Survey {
//     "CREATE TABLE SURVEY(OFFLINE_ID INTEGER PRIMARY KEY," +
//             " ONLINE_ID INTEGER, NAME TEXT, PRIVACY INTEGER, USERNAME TEXT, DATE_CREATED TEXT, DESCRIPTION TEXT,SYNCED INTEGER)";

    @ColumnInfo(name = "OFFLINE_ID")
    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "ONLINE_ID")
    private int onlineId;

    @ColumnInfo(name = "NAME")
    private String name;

    @ColumnInfo(name = "PRIVACY")
    private boolean privacy;

    @ColumnInfo(name = "SYNCED")
    private boolean synced = true;

    @ColumnInfo(name = "USERNAME")
    private String username;

    @ColumnInfo(name = "DATE_CREATED")
    private long date;

    @Ignore
    private int no_of_ques;

//    @Ignore
//    private int responseCount;

    @ColumnInfo(name = "DESCRIPTION")
    private String desc;

    @Ignore
    private List<Question> questions;

    @Ignore
    private String device_token;

    public Survey(){

    }

    public Survey(int id, String name, boolean privacy, long date, int no_of_ques, String desc) {
        this.id = id;
        this.name = name;
        this.privacy = privacy;
        this.date = date;
        this.no_of_ques = no_of_ques;
        this.desc = desc;
        questions = new ArrayList<>();
    }

    public Survey(int id, String name, long date, String desc, String username, String device_token) {
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

    public boolean isPrivacy() {
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

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
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

    public int getOnlineId() {
        return onlineId;
    }

    public void setOnlineId(int onlineId) {
        this.onlineId = onlineId;
    }

    public boolean isSynced() {
        return synced;
    }

    public void setSynced(boolean synced) {
        this.synced = synced;
    }

//    public int getResponseCount() {
//        return responseCount;
//    }
//
//    public void setResponseCount(int responseCount) {
//        this.responseCount = responseCount;
//    }

    public static class SurveyQueryResult extends Survey {

        private int responseCount;

        public int getResponseCount() {
            return responseCount;
        }

        public void setResponseCount(int responseCount) {
            this.responseCount = responseCount;
        }
    }
}
