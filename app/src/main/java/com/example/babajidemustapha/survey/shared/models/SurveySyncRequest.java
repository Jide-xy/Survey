package com.example.babajidemustapha.survey.shared.models;

import com.example.babajidemustapha.survey.shared.room.entities.Question;
import com.example.babajidemustapha.survey.shared.room.entities.ResponseDetail;
import com.example.babajidemustapha.survey.shared.room.entities.ResponseHeader;
import com.example.babajidemustapha.survey.shared.room.entities.Survey;

import java.util.ArrayList;
import java.util.List;

public class SurveySyncRequest {

    private List<UnsyncedSurveyWithEmbeddedQuestionsAndResponses> surveys;
    private List<ResponseHeaderWithEmbeddedResponseDetails> responses;

    public SurveySyncRequest() {
        surveys = new ArrayList<>();
        responses = new ArrayList<>();
    }

    public List<UnsyncedSurveyWithEmbeddedQuestionsAndResponses> getSurveys() {
        return surveys;
    }

    public void setSurveys(List<UnsyncedSurveyWithEmbeddedQuestionsAndResponses> surveys) {
        this.surveys = surveys;
    }

    public List<ResponseHeaderWithEmbeddedResponseDetails> getResponses() {
        return responses;
    }

    public void setResponses(List<ResponseHeaderWithEmbeddedResponseDetails> responses) {
        this.responses = responses;
    }


    public static class UnsyncedSurveyWithEmbeddedQuestionsAndResponses {
        private Survey survey;
        private List<Question> questions;
        private List<ResponseHeaderWithEmbeddedResponseDetails> responses;

        public Survey getSurvey() {
            return survey;
        }

        public void setSurvey(Survey survey) {
            this.survey = survey;
        }

        public List<Question> getQuestions() {
            return questions;
        }

        public void setQuestions(List<Question> questions) {
            this.questions = questions;
        }

        public List<ResponseHeaderWithEmbeddedResponseDetails> getResponses() {
            return responses;
        }

        public void setResponses(List<ResponseHeaderWithEmbeddedResponseDetails> responses) {
            this.responses = responses;
        }
    }

    public static class ResponseHeaderWithEmbeddedResponseDetails {
        private ResponseHeader responseHeader;
        private List<ResponseDetail> responseDetails;

        public ResponseHeader getResponseHeader() {
            return responseHeader;
        }

        public void setResponseHeader(ResponseHeader responseHeader) {
            this.responseHeader = responseHeader;
        }

        public List<ResponseDetail> getResponseDetails() {
            return responseDetails;
        }

        public void setResponseDetails(List<ResponseDetail> responseDetails) {
            this.responseDetails = responseDetails;
        }
    }
}
