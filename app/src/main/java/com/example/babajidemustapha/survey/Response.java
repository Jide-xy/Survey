package com.example.babajidemustapha.survey;

/**
 * Created by Babajide Mustapha on 9/19/2017.
 */

public class Response {
    private int id;
    private int question_id;
    private String response;

    public Response(int question_id, String response){
        this.response = response;
        this.question_id = question_id;
    }
    public Response(int id, int question_id, String response){
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
}
