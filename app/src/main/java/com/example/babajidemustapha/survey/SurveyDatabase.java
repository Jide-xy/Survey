package com.example.babajidemustapha.survey;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Babajide Mustapha on 9/18/2017.
 */

public class SurveyDatabase extends SQLiteOpenHelper {
    Context context;
    private static final String name = "SURVEY_DB";
    private static final int version = 1;
    private static final String createSurveyTable = "CREATE TABLE SURVEY(OFFLINE_ID INTEGER PRIMARY KEY," +
            " ONLINE_ID INTEGER, NAME TEXT, PRIVACY INTEGER, USERNAME TEXT, DATE_CREATED TEXT, DESCRIPTION TEXT,SYNCED INTEGER)";
    private static final String createQuestionTable = "CREATE TABLE QUESTION(OFFLINE_SURVEY_ID INTEGER, " +
            "ONLINE_SURVEY_ID INTEGER, Q_NO INTEGER, Q_TYPE TEXT, MANDATORY INTEGER, Q_TEXT TEXT, OPTIONS TEXT," +
            "OFFLINE_ID INTEGER PRIMARY KEY, ONLINE_ID INTEGER,SYNCED INTEGER," +
            "FOREIGN KEY(OFFLINE_SURVEY_ID) REFERENCES SURVEY(OFFLINE_ID))";
    private static final String createResponseTable = "CREATE TABLE RESPONSE(ONLINE_SURVEY_ID INTEGER," +
            " ONLINE_ID INTEGER, RESPONDENT_NAME TEXT, RESPONSE_DATE TEXT,SYNCED INTEGER," +
            "OFFLINE_ID INTEGER PRIMARY KEY, OFFLINE_SURVEY_ID INTEGER, " +
            "FOREIGN KEY(OFFLINE_SURVEY_ID) REFERENCES SURVEY(OFFLINE_ID))";
    private static final String createResDetailTable = "CREATE TABLE RESPONSE_DETAIL(OFFLINE_RES_DETAIL_ID INTEGER PRIMARY KEY," +
            "ONLINE_RESPONSE_ID INTEGER, ONLINE_QUESTION_ID INTEGER, RESPONSE TEXT,"  +
            "ONLINE_RES_DETAIL_ID INTEGER, OFFLINE_RESPONSE_ID INTEGER, OFFLINE_QUESTION_ID INTEGER,SYNCED INTEGER," +
            "FOREIGN KEY(OFFLINE_RESPONSE_ID) REFERENCES RESPONSE(OFFLINE_ID)," +
            "FOREIGN KEY(OFFLINE_QUESTION_ID) REFERENCES QUESTION(OFFLINE_ID))";
    public SurveyDatabase(Context context) {
        super(context, name, null, version);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(createSurveyTable);
        db.execSQL(createQuestionTable);
        db.execSQL(createResponseTable);
        db.execSQL(createResDetailTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS SURVEY");
        db.execSQL("DROP TABLE IF EXISTS QUESTION");
        db.execSQL("DROP TABLE IF EXISTS RESPONSE");
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion){

    }

//    private void emptyTable() {
//        SQLiteDatabase db = this.getWritableDatabase();
//        db.execSQL("DROP TABLE IF EXISTS USER");
//        onCreate(db);
//    }


    public void saveSurvey(JSONArray survey) throws JSONException {
        SQLiteDatabase db = this.getWritableDatabase();

        for(int i=0 ; i<survey.length(); i++){
            ContentValues values = new ContentValues();
            JSONObject object = survey.getJSONObject(i);
            values.put("NAME",object.getString("SURVEY_NAME"));
            values.put("ONLINE_ID",object.getInt("ID"));
            values.put("PRIVACY",object.getBoolean("PRIVACY"));
            values.put("USERNAME",object.getString("USER_ID"));
            values.put("DATE_CREATED",object.getString("DATE_CREATED"));
            values.put("DESCRIPTION",object.getString("DESCRIPTION"));
           Log.e("yup", db.insert("SURVEY",null,values)+"");
        }
        db.close();
    }
    public void saveQuestion(JSONArray question) throws JSONException {
        SQLiteDatabase db = this.getWritableDatabase();

        for(int i=0 ; i<question.length(); i++){
            ContentValues values = new ContentValues();
            JSONObject object = question.getJSONObject(i);
            String[] arg = {object.getInt("SURVEY_ID") +""};
            Cursor cursor = db.rawQuery("SELECT OFFLINE_ID FROM SURVEY WHERE ONLINE_ID = ?",arg);
            cursor.moveToFirst();
            values.put("ONLINE_SURVEY_ID",object.getInt("SURVEY_ID"));
            values.put("ONLINE_ID",object.getString("QUESTION_ID"));
            values.put("OFFLINE_SURVEY_ID",cursor.getInt(0));
            values.put("Q_NO",object.getString("QUESTION_NO"));
            values.put("Q_TYPE",object.getString("QUESTION_TYPE"));
            values.put("MANDATORY",object.getBoolean("MANDATORY"));
            values.put("Q_TEXT",object.getString("QUESTION_TEXT"));
            values.put("OPTIONS",object.getString("QUESTION_OPTIONS"));
            values.put("SYNCED",1);
            db.insert("QUESTION",null,values);
            cursor.close();
        }
        db.close();
    }
    public void saveResponse(JSONArray response) throws JSONException {
        SQLiteDatabase db = this.getWritableDatabase();

        for(int i=0 ; i<response.length(); i++){
            ContentValues values = new ContentValues();
            JSONObject object = response.getJSONObject(i);
            String[] arg = {object.getInt("SURVEY_ID") +""};
            Cursor cursor = db.rawQuery("SELECT OFFLINE_ID FROM SURVEY WHERE ONLINE_ID = ?",arg);
            cursor.moveToFirst();
            values.put("OFFLINE_SURVEY_ID",cursor.getInt(cursor.getColumnIndex("OFFLINE_ID")));
            values.put("ONLINE_SURVEY_ID",object.getInt("SURVEY_ID"));
            values.put("ONLINE_ID",object.getInt("RESPONSE_ID"));
            values.put("RESPONDENT_NAME",object.getString("RESPONDENT_NAME"));
            values.put("RESPONSE_DATE",object.getString("RESPONSE_DATE"));
            values.put("SYNCED",1);
            db.insert("RESPONSE",null,values);
            cursor.close();
        }
        db.close();
    }
    public void saveResponseDetail(JSONArray resDetail) throws JSONException {
        SQLiteDatabase db = this.getWritableDatabase();

        for(int i=0 ; i<resDetail.length(); i++){
            ContentValues values = new ContentValues();
            JSONObject object = resDetail.getJSONObject(i);
            String[] arg = {object.getInt("RESPONSE_ID")+""};
            String[] arg1 = {object.getInt("QUESTION_ID")+""};
            Cursor cursor = db.rawQuery("SELECT OFFLINE_ID FROM RESPONSE WHERE ONLINE_ID = ?",arg);
            Cursor cursor1 = db.rawQuery("SELECT OFFLINE_ID FROM QUESTION WHERE ONLINE_ID = ?",arg1);
            cursor.moveToFirst();
            cursor1.moveToFirst();
            values.put("OFFLINE_RESPONSE_ID",cursor.getInt(cursor.getColumnIndex("OFFLINE_ID")));
            values.put("OFFLINE_QUESTION_ID",cursor1.getInt(cursor.getColumnIndex("OFFLINE_ID")));
            values.put("ONLINE_RESPONSE_ID",object.getInt("RESPONSE_ID"));
            values.put("ONLINE_QUESTION_ID",object.getInt("QUESTION_ID"));
            values.put("ONLINE_RES_DETAIL_ID",object.getInt("RESPONSE_DETAIL_ID"));
            values.put("RESPONSE",object.getString("RESPONSE"));
            values.put("SYNCED",1);
            db.insert("RESPONSE_DETAIL",null,values);
            cursor.close();
            cursor1.close();
        }
        db.close();
    }

    public List<Survey> getMySurveys(){
        SQLiteDatabase db = this.getReadableDatabase();
        List<Survey> surveys = new ArrayList<>();

        Cursor cursor = db.rawQuery("SELECT * FROM SURVEY",null);
        if(cursor != null) {
            while (cursor.moveToNext()) {
                String[] args = {cursor.getInt(cursor.getColumnIndex("OFFLINE_ID")) + ""};
//                Log.e("dbsurvey", cursor.getString(cursor.getColumnIndex("ONLINE_ID")));
                Cursor cursor1 = db.rawQuery("SELECT COUNT(Q_NO) AS COUNT FROM QUESTION WHERE OFFLINE_SURVEY_ID = ?", args);
                cursor1.moveToFirst();
                surveys.add(new Survey(cursor.getInt(cursor.getColumnIndex("OFFLINE_ID")),
                        cursor.getString(cursor.getColumnIndex("NAME")),
                        cursor.getInt(cursor.getColumnIndex("PRIVACY")) != 0,
                        cursor.getString(cursor.getColumnIndex("DATE_CREATED")),
                        cursor1.getInt(0),
                        cursor.getString(cursor.getColumnIndex("DESCRIPTION"))
                ));
                cursor1.close();
            }
        }
        else{
            Log.e("cursor", " cursr is null");
        }
        cursor.close();
        db.close();
        return surveys;
    }
    public List<Question> getQuestions(int id){
        SQLiteDatabase db = this.getReadableDatabase();
        List<Question> questions = new ArrayList<>();
        String[] args = {id + ""};
        Cursor cursor = db.rawQuery("SELECT * FROM QUESTION WHERE OFFLINE_SURVEY_ID = ? ORDER BY Q_NO ASC ",args);

        while(cursor.moveToNext()){
            Log.e("q type", cursor.getString(cursor.getColumnIndex("Q_TYPE")));
            Log.e("q text", cursor.getString(cursor.getColumnIndex("Q_TEXT")));
            Log.e("options",cursor.getString(cursor.getColumnIndex("OPTIONS")));
            try{
            switch (cursor.getString(cursor.getColumnIndex("Q_TYPE"))) {
                case "TEXT":
                questions.add(new Question(cursor.getInt(cursor.getColumnIndex("OFFLINE_ID")),
                        cursor.getInt(cursor.getColumnIndex("Q_NO")),
                        cursor.getString(cursor.getColumnIndex("Q_TYPE")),
                        cursor.getInt(cursor.getColumnIndex("OFFLINE_SURVEY_ID")),
                        null,
                        cursor.getInt(cursor.getColumnIndex("MANDATORY")) != 0,
                        cursor.getString(cursor.getColumnIndex("Q_TEXT"))));
                    break;
                case "SINGLE":
                case "MULTI":
                    questions.add(new Question(cursor.getInt(cursor.getColumnIndex("OFFLINE_ID")),
                            cursor.getInt(cursor.getColumnIndex("Q_NO")),
                            cursor.getString(cursor.getColumnIndex("Q_TYPE")),
                            cursor.getInt(cursor.getColumnIndex("OFFLINE_SURVEY_ID")),
                            new JSONArray(cursor.getString(cursor.getColumnIndex("OPTIONS"))),
                            cursor.getInt(cursor.getColumnIndex("MANDATORY")) != 0,
                            cursor.getString(cursor.getColumnIndex("Q_TEXT"))));
                    break;
            }
        }
            catch (JSONException e){
                e.printStackTrace();
            }
        }
        cursor.close();
        db.close();
        return questions;
    }

    public List<ResponseHeader> getResponseHeader(int id){
        SQLiteDatabase db = this.getReadableDatabase();
        List<ResponseHeader> responseHeaders = new ArrayList<>();
        String[] args = {id + ""};
        Cursor cursor = db.rawQuery("SELECT * FROM RESPONSE WHERE OFFLINE_SURVEY_ID = ? ORDER BY OFFLINE_ID DESC",args);

        while(cursor.moveToNext()){
            Log.e("response ish",cursor.getInt(cursor.getColumnIndex("ONLINE_SURVEY_ID"))+"");
            Log.e("response synced ish",cursor.getInt(cursor.getColumnIndex("SYNCED"))+"");
            responseHeaders.add(new ResponseHeader(cursor.getInt(cursor.getColumnIndex("OFFLINE_ID")),
                    cursor.getInt(cursor.getColumnIndex("OFFLINE_SURVEY_ID")),
                    cursor.getString(cursor.getColumnIndex("RESPONDENT_NAME")),
                    cursor.getString(cursor.getColumnIndex("RESPONSE_DATE"))
                    ));
        }
        cursor.close();
        db.close();
        return responseHeaders;
    }
    public List<QuestionAndResponse> getResponse(int id){
        SQLiteDatabase db = this.getReadableDatabase();
        List<QuestionAndResponse> response = new ArrayList<>();
        String[] args = {id + ""};
        String query = "SELECT QUESTION.OFFLINE_ID,QUESTION.Q_NO,QUESTION.Q_TYPE,QUESTION.OFFLINE_SURVEY_ID," +
                "QUESTION.OPTIONS,QUESTION.MANDATORY,QUESTION.Q_TEXT,RESPONSE_DETAIL.RESPONSE FROM RESPONSE_DETAIL JOIN QUESTION" +
                " ON RESPONSE_DETAIL.OFFLINE_QUESTION_ID = QUESTION.OFFLINE_ID WHERE OFFLINE_RESPONSE_ID = ?";
        Cursor cursor = db.rawQuery(query ,args);
        try {
            while (cursor.moveToNext()) {
                Log.e("response here", cursor.getString(cursor.getColumnIndex("MANDATORY")));
                switch (cursor.getString(cursor.getColumnIndex("Q_TYPE"))) {
                    case "TEXT":
                        response.add(new QuestionAndResponse(cursor.getInt(cursor.getColumnIndex("OFFLINE_ID")),
                                cursor.getInt(cursor.getColumnIndex("Q_NO")),
                                cursor.getString(cursor.getColumnIndex("Q_TYPE")),
                                cursor.getInt(cursor.getColumnIndex("OFFLINE_SURVEY_ID")),
                                null,
                               cursor.getInt(cursor.getColumnIndex("MANDATORY")) != 0,
                                cursor.getString(cursor.getColumnIndex("Q_TEXT")),
                                cursor.getString(cursor.getColumnIndex("RESPONSE"))));
                        break;
                    case "SINGLE":
                    case "MULTI":
                        response.add(new QuestionAndResponse(cursor.getInt(cursor.getColumnIndex("OFFLINE_ID")),
                                cursor.getInt(cursor.getColumnIndex("Q_NO")),
                                cursor.getString(cursor.getColumnIndex("Q_TYPE")),
                                cursor.getInt(cursor.getColumnIndex("OFFLINE_SURVEY_ID")),
                                new JSONArray(cursor.getString(cursor.getColumnIndex("OPTIONS"))),
                                cursor.getInt(cursor.getColumnIndex("MANDATORY")) != 0,
                                cursor.getString(cursor.getColumnIndex("Q_TEXT")),
                                cursor.getString(cursor.getColumnIndex("RESPONSE"))));
                        break;
                }
            }
        }
        catch (JSONException e){
            e.printStackTrace();
        }
        cursor.close();
        db.close();
        return response;
    }

    public void recordResponse(ResponseHeader responseHeader, List<Response> responses){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("RESPONDENT_NAME", responseHeader.getRespondentName());
        values.put("RESPONSE_DATE",responseHeader.getDate());
        values.put("OFFLINE_SURVEY_ID",responseHeader.getSurvey_id());
        values.put("SYNCED",0);
        Cursor cursor = db.rawQuery("SELECT * FROM SURVEY WHERE OFFLINE_ID = ?",new String[]{responseHeader.getSurvey_id()+""});
        cursor.moveToFirst();
        values.put("ONLINE_SURVEY_ID",cursor.getInt(cursor.getColumnIndex("ONLINE_ID")));
        long responseHeaderId = db.insert("RESPONSE",null,values);
        Cursor cursor1 = null;
        Cursor cursor2 = null;
        for(int i=0 ; i<responses.size(); i++){
            ContentValues contentValues = new ContentValues();
            Response response = responses.get(i);
             cursor1 = db.rawQuery("SELECT * FROM RESPONSE WHERE OFFLINE_ID = ?",new String[]{responseHeaderId+""});
             cursor2 = db.rawQuery("SELECT * FROM QUESTION WHERE OFFLINE_ID = ?",new String[]{response.getQuestion_id()+""});
            cursor1.moveToFirst();
            cursor2.moveToFirst();
            contentValues.put("OFFLINE_RESPONSE_ID",responseHeaderId);
            contentValues.put("OFFLINE_QUESTION_ID", response.getQuestion_id());
            contentValues.put("ONLINE_RESPONSE_ID",cursor1.getInt(cursor1.getColumnIndex("ONLINE_ID")));
            contentValues.put("ONLINE_QUESTION_ID", cursor2.getInt(cursor2.getColumnIndex("ONLINE_ID")));
            contentValues.put("RESPONSE",response.getResponse());
            contentValues.put("SYNCED",0);
            db.insert("RESPONSE_DETAIL",null,contentValues);
        }
        if(cursor1!=null&&cursor2!=null) {
            cursor1.close();
            cursor2.close();
        }
        db.close();
    }
    public void createSurvey(Survey survey, List<Question> questions){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("NAME", survey.getName());
        values.put("PRIVACY",survey.isPrivate());
        values.put("DESCRIPTION",survey.getDesc());
        values.put("USERNAME",survey.getUsername());
        values.put("DATE_CREATED",survey.getDate());
        values.put("SYNCED",0);
        long survey_id = db.insert("SURVEY",null,values);
        for (Question question: questions) {
            ContentValues questionValues = new ContentValues();
            questionValues.put("OFFLINE_SURVEY_ID",survey_id);
            questionValues.put("Q_NO",question.getQuestionNo());
            questionValues.put("Q_TYPE",question.getQuestionType());
            questionValues.put("Q_TEXT",question.getQuestionText());
            questionValues.put("OPTIONS",question.getOptions().toString());
            questionValues.put("MANDATORY",question.isMandatory());
            questionValues.put("SYNCED",0);
            db.insert("QUESTION",null, questionValues);
        }
        db.close();
    }

    public JSONObject getSyncData(){
        JSONObject json;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor survey = db.rawQuery("SELECT * FROM SURVEY WHERE SYNCED = 0", null);
        try {
            json = new JSONObject();
            JSONArray array = new JSONArray();
            JSONArray obj2 = new JSONArray();
            while (survey.moveToNext()) {
                JSONObject object = new JSONObject();
                JSONArray obj = new JSONArray();
                JSONArray obj1 = new JSONArray();
               object.put("SURVEY_OFFLINE_ID",survey.getInt(survey.getColumnIndex("OFFLINE_ID"))) ;
                object.put("SURVEY_NAME", survey.getString(survey.getColumnIndex("NAME")));
                object.put("PRIVACY", survey.getInt(survey.getColumnIndex("PRIVACY")));
                object.put("DATE_CREATED", survey.getString(survey.getColumnIndex("DATE_CREATED")));
                object.put("DESCRIPTION", survey.getString(survey.getColumnIndex("DESCRIPTION")));
                String[] args =  {survey.getInt(survey.getColumnIndex("OFFLINE_ID")) +""};
                Cursor questions = db.rawQuery("SELECT * FROM QUESTION WHERE OFFLINE_SURVEY_ID = ? ORDER BY Q_NO ASC ",args);
                while (questions.moveToNext()){
                    JSONObject ques = new JSONObject();
                    ques.put("OFFLINE_ID" ,questions.getInt(questions.getColumnIndex("OFFLINE_ID")));
                    ques.put("Q_NO", questions.getInt(questions.getColumnIndex("Q_NO")));
                    ques.put("Q_TYPE", questions.getString(questions.getColumnIndex("Q_TYPE")));
                    ques.put( "OFFLINE_SURVEY_ID",  questions.getInt(questions.getColumnIndex("OFFLINE_SURVEY_ID")));
                    ques.put("OPTIONS",  questions.getString(questions.getColumnIndex("OPTIONS")));
                    ques.put( "MANDATORY", questions.getInt(questions.getColumnIndex("MANDATORY")));
                    ques.put("Q_TEXT",   questions.getString(questions.getColumnIndex("Q_TEXT")));
                    obj.put(ques);
                }
                Cursor response = db.rawQuery("SELECT * FROM RESPONSE WHERE OFFLINE_SURVEY_ID = ? AND ONLINE_SURVEY_ID IS NULL",args);
                while(response.moveToNext()) {
                    JSONObject object1 = new JSONObject();
                    JSONArray responses = new JSONArray();
                    String[] args1 = {response.getInt(response.getColumnIndex("OFFLINE_ID"))+""};
                    object1.put("OFFLINE_ID", response.getInt(response.getColumnIndex("OFFLINE_ID")));
                    object1.put("OFFLINE_SURVEY_ID",    response.getInt(response.getColumnIndex("OFFLINE_SURVEY_ID")));
                    object1.put("RESPONDENT_NAME",    response.getString(response.getColumnIndex("RESPONDENT_NAME")));
                    object1.put("RESPONSE_DATE",    response.getString(response.getColumnIndex("RESPONSE_DATE")));
                    Cursor res_detail = db.rawQuery("SELECT * FROM RESPONSE_DETAIL WHERE OFFLINE_RESPONSE_ID = ?",args1);
                    while (res_detail.moveToNext()){
                        JSONObject object2 = new JSONObject();
                        object2.put("OFFLINE_RES_DETAIL_ID", res_detail.getInt(res_detail.getColumnIndex("OFFLINE_RES_DETAIL_ID")));
                        object2.put("OFFLINE_QUESTION_ID",  res_detail.getInt(res_detail.getColumnIndex("OFFLINE_QUESTION_ID")));
                        object2.put("OFFLINE_RESPONSE_ID",  res_detail.getString(res_detail.getColumnIndex("OFFLINE_RESPONSE_ID")));
                        object2.put("RESPONSE",  res_detail.getString(res_detail.getColumnIndex("RESPONSE")));
                        responses.put(object2);
                    }
                    object1.put("RESPONSE_DETAIL", responses);
                    obj1.put(object1);
                }
                object.put("QUESTIONS",obj);
                object.put("RESPONSES",obj1);
                array.put(object);
            }
            json.put("SURVEYS", array);
            Cursor online_response = db.rawQuery("SELECT * FROM RESPONSE WHERE SYNCED = 0 AND ONLINE_SURVEY_ID != 0",null);
            Log.e("online survey response", online_response.getCount()+"");
            while(online_response.moveToNext()) {
                JSONObject object3 = new JSONObject();
                JSONArray responses = new JSONArray();
                String[] args1 = {online_response.getInt(online_response.getColumnIndex("OFFLINE_ID"))+""};
                object3.put("OFFLINE_ID", online_response.getInt(online_response.getColumnIndex("OFFLINE_ID")));
                object3.put("OFFLINE_SURVEY_ID", online_response.getInt(online_response.getColumnIndex("OFFLINE_SURVEY_ID")));
                object3.put("ONLINE_SURVEY_ID",    online_response.getInt(online_response.getColumnIndex("ONLINE_SURVEY_ID")));
                object3.put("RESPONDENT_NAME",    online_response.getString(online_response.getColumnIndex("RESPONDENT_NAME")));
                object3.put("RESPONSE_DATE",    online_response.getString(online_response.getColumnIndex("RESPONSE_DATE")));
                Cursor res_detail = db.rawQuery("SELECT * FROM RESPONSE_DETAIL WHERE OFFLINE_RESPONSE_ID = ?",args1);
                while (res_detail.moveToNext()){
                    JSONObject object2 = new JSONObject();
                    object2.put("OFFLINE_RES_DETAIL_ID", res_detail.getInt(res_detail.getColumnIndex("OFFLINE_RES_DETAIL_ID")));
                    object2.put("OFFLINE_QUESTION_ID",  res_detail.getInt(res_detail.getColumnIndex("OFFLINE_QUESTION_ID")));
                    object2.put("ONLINE_QUESTION_ID",  res_detail.getInt(res_detail.getColumnIndex("ONLINE_QUESTION_ID")));
                    object2.put("OFFLINE_RESPONSE_ID",  res_detail.getString(res_detail.getColumnIndex("OFFLINE_RESPONSE_ID")));
                    object2.put("RESPONSE",  res_detail.getString(res_detail.getColumnIndex("RESPONSE")));
                    responses.put(object2);
                }
                object3.put("RESPONSE_DETAIL", responses);
                obj2.put(object3);
                res_detail.close();
            }
            json.put("SYNCED_SURVEY_RESPONSE",obj2);
            online_response.close();
        }
        catch (JSONException e){
            json = null;
        }
        finally {
            db.close();
            survey.close();
        }
        return json;
    }

    public void updateSyncData(JSONObject jsonObject){
        SQLiteDatabase db = this.getWritableDatabase();
            try {
            JSONArray surveys = jsonObject.getJSONArray("SURVEYS");
            JSONArray syncedSurveyresponses = jsonObject.getJSONArray("SYNCED_SURVEY_RESPONSE");
            for(int i = 0; i<surveys.length();i++){
                JSONObject jsonObject1 = surveys.getJSONObject(i);
                ContentValues contentValues = new ContentValues();
                contentValues.put("SYNCED",1);
                contentValues.put("ONLINE_ID",jsonObject1.getInt("SURVEY_ONLINE_ID"));
                db.update("SURVEY",contentValues,"OFFLINE_ID = ?", new String[] {jsonObject1.getInt("SURVEY_OFFLINE_ID")+""});
                JSONArray questions = jsonObject1.getJSONArray("QUESTIONS");
                JSONArray responses = jsonObject1.getJSONArray("RESPONSES");
                for(int j = 0; j<questions.length();j++){
                    JSONObject jsonObject2 = questions.getJSONObject(j);
                    ContentValues contentValues1 = new ContentValues();
                    contentValues1.put("SYNCED",1);
                    contentValues1.put("ONLINE_ID",jsonObject2.getInt("ONLINE_ID"));
                    contentValues1.put("ONLINE_SURVEY_ID",jsonObject1.getInt("SURVEY_ONLINE_ID"));
                    db.update("QUESTION",contentValues1,"OFFLINE_ID = ?", new String[] {jsonObject2.getInt("OFFLINE_ID")+""});
                }
                for(int j = 0; j<responses.length();j++){
                    JSONObject jsonObject2 = responses.getJSONObject(j);
                    ContentValues contentValues1 = new ContentValues();
                    contentValues1.put("SYNCED",1);
                    contentValues1.put("ONLINE_ID",jsonObject2.getInt("ONLINE_ID"));
                    contentValues1.put("ONLINE_SURVEY_ID",jsonObject1.getInt("SURVEY_ONLINE_ID"));
                    db.update("RESPONSE",contentValues1,"OFFLINE_ID = ?", new String[] {jsonObject2.getInt("OFFLINE_ID")+""});
                    JSONArray responseDetails = jsonObject2.getJSONArray("RESPONSE_DETAIL");
                    for(int x = 0; x<responseDetails.length();x++){
                        JSONObject jsonObject3 = responseDetails.getJSONObject(x);
                        ContentValues contentValues2 = new ContentValues();
                        contentValues2.put("SYNCED",1);
                        contentValues2.put("ONLINE_RESPONSE_ID",jsonObject2.getInt("ONLINE_ID"));
                        contentValues2.put("ONLINE_QUESTION_ID",jsonObject3.getInt("ONLINE_QUESTION_ID"));
                        contentValues2.put("ONLINE_RES_DETAIL_ID",jsonObject3.getInt("ONLINE_RES_DETAIL_ID"));
                        db.update("RESPONSE_DETAIL",contentValues2,"OFFLINE_RES_DETAIL_ID = ?", new String[] {jsonObject3.getInt("OFFLINE_RES_DETAIL_ID")+""});
                    }
                }
            }
            for(int i = 0; i<syncedSurveyresponses.length();i++){
                JSONObject jsonObject2 = syncedSurveyresponses.getJSONObject(i);
                ContentValues contentValues1 = new ContentValues();
                contentValues1.put("SYNCED",1);
                contentValues1.put("ONLINE_ID",jsonObject2.getInt("ONLINE_ID"));
                db.update("RESPONSE",contentValues1,"OFFLINE_ID = ?", new String[] {jsonObject2.getInt("OFFLINE_ID")+""});
                JSONArray responseDetails = jsonObject2.getJSONArray("RESPONSE_DETAIL");
                for(int x = 0; x<responseDetails.length();x++){
                    JSONObject jsonObject3 = responseDetails.getJSONObject(x);
                    ContentValues contentValues2 = new ContentValues();
                    contentValues2.put("SYNCED",1);
                    contentValues2.put("ONLINE_RESPONSE_ID",jsonObject2.getInt("ONLINE_ID"));
                    contentValues2.put("ONLINE_RES_DETAIL_ID",jsonObject3.getInt("ONLINE_RES_DETAIL_ID"));
                    db.update("RESPONSE_DETAIL",contentValues2,"OFFLINE_RES_DETAIL_ID = ?", new String[] {jsonObject3.getInt("OFFLINE_RES_DETAIL_ID")+""});
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        finally {
                db.close();
            }
    }

    public int getResponsesCount(int survey_id){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(OFFLINE_ID) AS RESPONSE_COUNT FROM RESPONSE WHERE OFFLINE_SURVEY_ID = ? ", new String[]{survey_id+""});
        cursor.moveToFirst();
        int x = cursor.getInt(cursor.getColumnIndex("RESPONSE_COUNT"));
        cursor.close();
        return x;
    }
    public Survey getSurvey(int online_id){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM SURVEY WHERE ONLINE_ID = ? ", new String[]{online_id+""});
        cursor.moveToFirst();
        Cursor cursor1 = db.rawQuery("SELECT COUNT(Q_NO) AS COUNT FROM QUESTION WHERE OFFLINE_SURVEY_ID = ?", new String[]{cursor.getInt(cursor.getColumnIndex("OFFLINE_ID"))+""});
        cursor1.moveToFirst();
        Survey survey = new Survey(cursor.getInt(cursor.getColumnIndex("OFFLINE_ID")),
                cursor.getString(cursor.getColumnIndex("NAME")),
                cursor.getInt(cursor.getColumnIndex("PRIVACY")) != 0,
                cursor.getString(cursor.getColumnIndex("DATE_CREATED")),
                cursor1.getInt(0),
                cursor.getString(cursor.getColumnIndex("DESCRIPTION"))
        );
        cursor.close();
        cursor1.close();
        db.close();
        return survey;
    }
    public List<Survey> filterSurveys(String query){
        SQLiteDatabase db = this.getReadableDatabase();
        List<Survey> surveys = new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT * FROM SURVEY WHERE NAME LIKE ? OR DESCRIPTION LIKE ? ", new String[]{"%"+query+"%","%"+query+"%"});
        if(cursor != null) {
            while (cursor.moveToNext()) {
                String[] args = {cursor.getInt(cursor.getColumnIndex("OFFLINE_ID")) + ""};
                Cursor cursor1 = db.rawQuery("SELECT COUNT(Q_NO) AS COUNT FROM QUESTION WHERE OFFLINE_SURVEY_ID = ?", args);
                cursor1.moveToFirst();
                surveys.add(new Survey(cursor.getInt(cursor.getColumnIndex("OFFLINE_ID")),
                        cursor.getString(cursor.getColumnIndex("NAME")),
                        cursor.getInt(cursor.getColumnIndex("PRIVACY")) != 0,
                        cursor.getString(cursor.getColumnIndex("DATE_CREATED")),
                        cursor1.getInt(0),
                        cursor.getString(cursor.getColumnIndex("DESCRIPTION"))
                ));
                cursor1.close();
            }
        }
        else{
            Log.e("cursor", " cursor is null");
        }
        cursor.close();
        db.close();
        return surveys;
    }
    public Map<Question,List<Response>> getReport(int id){
        SQLiteDatabase db = this.getReadableDatabase();
        Map<Question,List<Response>> report = new LinkedHashMap<>();

        Cursor cursor = db.rawQuery("SELECT * FROM QUESTION WHERE OFFLINE_SURVEY_ID = ? ORDER BY Q_NO ASC", new String[] {id+""});
        try {
            while (cursor.moveToNext()) {
              //  Log.e("question NO: ", cursor.getInt(cursor.getColumnIndex("Q_NO"))+"");
                Question question = null;
                List<Response> response = new ArrayList<>();
                switch (cursor.getString(cursor.getColumnIndex("Q_TYPE"))) {
                    case "TEXT":
                        question = new Question(cursor.getInt(cursor.getColumnIndex("OFFLINE_ID")),
                                cursor.getInt(cursor.getColumnIndex("Q_NO")),
                                cursor.getString(cursor.getColumnIndex("Q_TYPE")),
                                cursor.getInt(cursor.getColumnIndex("OFFLINE_SURVEY_ID")),
                                null,
                                cursor.getInt(cursor.getColumnIndex("MANDATORY")) != 0,
                                cursor.getString(cursor.getColumnIndex("Q_TEXT")));
                        break;
                    case "SINGLE":
                    case "MULTI":
                       question = new Question(cursor.getInt(cursor.getColumnIndex("OFFLINE_ID")),
                               cursor.getInt(cursor.getColumnIndex("Q_NO")),
                               cursor.getString(cursor.getColumnIndex("Q_TYPE")),
                               cursor.getInt(cursor.getColumnIndex("OFFLINE_SURVEY_ID")),
                               new JSONArray(cursor.getString(cursor.getColumnIndex("OPTIONS"))),
                               cursor.getInt(cursor.getColumnIndex("MANDATORY")) != 0,
                               cursor.getString(cursor.getColumnIndex("Q_TEXT")));
                        break;
                }
                String[] args = {cursor.getInt(cursor.getColumnIndex("OFFLINE_ID")) + ""};
                Cursor cursor1 = db.rawQuery("SELECT * FROM RESPONSE_DETAIL WHERE OFFLINE_QUESTION_ID = ?" ,args);
                Log.e("response count: ", cursor1.getCount()+"");
                while(cursor1.moveToNext()) {
                   response.add(new Response(cursor1.getInt(cursor1.getColumnIndex("OFFLINE_RES_DETAIL_ID")),
                           cursor1.getInt(cursor1.getColumnIndex("OFFLINE_QUESTION_ID")),
                           cursor1.getString(cursor1.getColumnIndex("RESPONSE"))));
                }
                cursor1.close();
                report.put(question,response);
            }
            cursor.close();
        }
        catch (JSONException e){
            e.printStackTrace();
        }
        db.close();
        return report;
    }
//    public List<ResponseHeader> filterResponseList(int id, String query){
//        SQLiteDatabase db = this.getReadableDatabase();
//        List<ResponseHeader> responseHeaders = new ArrayList<>();
//        String[] args = {id + ""};
//        Cursor cursor = db.rawQuery("SELECT * FROM RESPONSE WHERE OFFLINE_SURVEY_ID = ? AND ORDER BY OFFLINE_ID DESC",args);
//
//        while(cursor.moveToNext()){
//            Log.e("response ish",cursor.getInt(cursor.getColumnIndex("ONLINE_SURVEY_ID"))+"");
//            Log.e("response synced ish",cursor.getInt(cursor.getColumnIndex("SYNCED"))+"");
//            responseHeaders.add(new ResponseHeader(cursor.getInt(cursor.getColumnIndex("OFFLINE_ID")),
//                    cursor.getInt(cursor.getColumnIndex("OFFLINE_SURVEY_ID")),
//                    cursor.getString(cursor.getColumnIndex("RESPONDENT_NAME")),
//                    cursor.getString(cursor.getColumnIndex("RESPONSE_DATE"))
//            ));
//        }
//        return responseHeaders;
//    }
    public void deleteDB(){
        context.getApplicationContext().deleteDatabase(name);
    }
}
