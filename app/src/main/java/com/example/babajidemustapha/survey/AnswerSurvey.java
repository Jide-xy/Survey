package com.example.babajidemustapha.survey;


import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class AnswerSurvey extends Fragment {

    SurveyDatabase db;
    LinearLayout rootView;
    List<Question> questions;
    Button btn;
    EditText name;
    int survey_id;
    boolean online;
    RequestQueue requestQueue;
    String device_token;
    public AnswerSurvey() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.content_take_survey,container,false);
        Bundle bundle = getActivity().getIntent().getExtras();
        survey_id = bundle.getInt("ID");
        online = bundle.getBoolean("online");
        requestQueue = Volley.newRequestQueue(getContext());
        Log.e("id",survey_id +"");
        rootView = (LinearLayout) view.findViewById(R.id.rootView);
        name = (EditText) view.findViewById(R.id.respondentName);
        btn= (Button) view.findViewById(R.id.submitResponse);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.setEnabled(false);
//                if(name.getText().toString().isEmpty()){
//                    Toast.makeText(getContext(),"Please enter your name",Toast.LENGTH_SHORT).show();
//                }
//                else {
                    if (validate(questions)) {
                        if(!online) {
                            submit();
                            Toast.makeText(getContext(), "Your response has been recorded", Toast.LENGTH_SHORT).show();
                            getActivity().finish();
                        }
                        else{
                            saveOnlineResponse(questions);
                        }
                    } else {
                        Toast.makeText(getContext(), "1 or more mandatory questions have not been answered", Toast.LENGTH_SHORT).show();
                    }
             //   }
                v.setEnabled(true);
            }
        });
        if(!online) {
            db = new SurveyDatabase(getContext());
            questions = db.getQuestions(survey_id);
        }
        else {
            device_token = bundle.getString("device_token");
            questions = (ArrayList<Question>) bundle.getSerializable("questions");
        }
        Log.e("id",questions.toString());
        buildQuestions(questions,rootView);
        return view;
    }

    public void buildQuestions(final List<Question> questions, ViewGroup view){
        for(int i = 0; i< questions.size(); i++){
            view.addView(buildQuestion(questions.get(i)));
        }
    }
    public void submit(){
            storeResponse(questions);
    }
    public boolean validate(List<Question> questions){

        for(int i = 0; i<questions.size(); i++) {
            if (questions.get(i).isMandatory()) {
                switch (questions.get(i).getQuestionType()) {
                    case "TEXT":
                        EditText ans = (EditText) rootView.findViewWithTag(questions.get(i).getQuestionNo());
                        if (ans.getText().toString().isEmpty()){
                            return false;
                        }
                        break;
                    case "SINGLE":
                        RadioGroup radioGroup = (RadioGroup) rootView.findViewWithTag("rg"+ questions.get(i).getQuestionNo());
                        if(radioGroup.getCheckedRadioButtonId() == -1){
                            return false;
                        }
                        break;
                    case "MULTI":
                        Question question = questions.get(i);
                        for(int j = 0 ; j < question.getOptionCount(); j++ ){
                            CheckBox cb = (CheckBox) rootView.findViewWithTag("qu"+ question.getQuestionNo()+"op"+ j);
                            if(cb.isChecked()){
                                return true;
                            }
                        }
                        return false;
                }
            }
        }
        return true;
    }
    public List<Response> buildResponseDetail(List<Question> questions){
        List<Response> responses = new ArrayList<>();
        for(int i = 0; i<questions.size(); i++) {
            Question question = questions.get(i);
            switch (question.getQuestionType()) {
                case "TEXT":
                    EditText ans = (EditText) rootView.findViewWithTag(question.getQuestionNo());
                    if (!ans.getText().toString().isEmpty()){
                        // question.setAnswer(ans.getText().toString());
                        responses.add(new Response(question.getId(),ans.getText().toString()));
                    }
                    else{
                        responses.add(new Response(question.getId(),""));
                    }
                    break;
                case "SINGLE":
                    RadioGroup radioGroup = (RadioGroup) rootView.findViewWithTag("rg"+question.getQuestionNo());
                    if(radioGroup.getCheckedRadioButtonId() != -1){
                        RadioButton radioButton = (RadioButton) rootView.findViewById(radioGroup.getCheckedRadioButtonId());
                        responses.add(new Response(question.getId(),radioButton.getText().toString()));
                        // question.setAnswer(radioButton.getText().toString());
                    }
                    else{
                        responses.add(new Response(question.getId(),""));
                    }
                    break;
                case "MULTI":
                    List<String> cbList = new ArrayList<>();
                    for(int j = 0 ; j < question.getOptionCount(); j++ ){
                        CheckBox cb = (CheckBox) rootView.findViewWithTag("qu"+ question.getQuestionNo()+"op"+ j);
                        if(cb.isChecked()){
                            cbList.add("\""+cb.getText().toString()+"\"");
                        }
                    }
                    if(!cbList.isEmpty()){
                        responses.add(new Response(question.getId(),cbList.toString()));
                    }
                    else{
                        responses.add(new Response(question.getId(),""));
                    }
                    break;
            }
        }
        return responses;
    }
    public ResponseHeader buildResponseHeader(){
        ResponseHeader header = new ResponseHeader(survey_id , name.getText().toString(), new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date()));
        return header;
    }
    public void storeResponse(List<Question> questions){

        db.recordResponse(buildResponseHeader(),buildResponseDetail(questions));

    }
    public void saveOnlineResponse(List<Question> questions){
        final ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Saving Response. Please wait...");
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.show();
        JSONObject object = new JSONObject();
        try {
            object.put("RESPONSE_HEADER", buildResponseHeaderJSON(buildResponseHeader()));
            object.put("RESPONSE_DETAIL", buildResponseDetailJSON(buildResponseDetail(questions)));
            object.put("DEVICE_TOKEN",device_token);
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, "http://survhey.azurewebsites.net/survey/record_response",object,new com.android.volley.Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Log.e("ddd",response.toString());
                    progressDialog.dismiss();
                    btn.setEnabled(true);
                    try {
                        if(response.getString("STATUS").equalsIgnoreCase("success")){
                            Toast.makeText(getContext(),"Response Successfully Posted",Toast.LENGTH_SHORT).show();
                        }
                        else {
                            Toast.makeText(getContext(),"An error occured",Toast.LENGTH_SHORT).show();
                            Log.e("ddd",response.toString());
                        }
                    } catch (JSONException e) {
                        // Toast.makeText(LoginActivity.this,"An error occured",Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }

                }
            }, new com.android.volley.Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    progressDialog.dismiss();
                    btn.setEnabled(true);
                    Toast.makeText(getContext(),"Failed to connect",Toast.LENGTH_SHORT).show();
                    error.printStackTrace();
                }
            });
            requestQueue.add(request);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public JSONObject buildResponseHeaderJSON(ResponseHeader responseHeader) throws JSONException{
        JSONObject object = new JSONObject();
        object.put("RESPONSE_ID", responseHeader.getResponse_id());
        object.put("SURVEY_ID", responseHeader.getSurvey_id());
        object.put("RESPONDENT_NAME", responseHeader.getRespondentName());
        object.put("RESPONSE_DATE", responseHeader.getDate());
        return object;
    }
    public JSONArray buildResponseDetailJSON(List<Response> responses) throws JSONException{
        JSONArray jsonArray = new JSONArray();
        for(int i = 0; i< responses.size();i++){
            Response response = responses.get(i);
            JSONObject object = new JSONObject();
            object.put("QUESTION_ID", response.getQuestion_id());
            object.put("RESPONSE",  response.getResponse());
            jsonArray.put(object);
        }
        return jsonArray;
    }

    public View buildQuestion(Question question){

        CardView cardView = new CardView(getContext());
        CardView.LayoutParams layoutParams = new CardView.LayoutParams(CardView.LayoutParams.MATCH_PARENT,CardView.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(0,
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2, getResources().getDisplayMetrics()),
                0,
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2, getResources().getDisplayMetrics()));
        cardView.setLayoutParams(layoutParams);
        LinearLayout ll = new LinearLayout(getContext());
        ll.setOrientation(LinearLayout.VERTICAL);
        ll.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        ll.setPadding((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 16, getResources().getDisplayMetrics()),
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 16, getResources().getDisplayMetrics()),
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 16, getResources().getDisplayMetrics()),
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 16, getResources().getDisplayMetrics()));
        TextView q_text = new TextView(getContext());
        TextView q_No = new TextView(getContext());
        if(question.isMandatory()){
            SpannableString star = new SpannableString("*"+question.getQuestionNo()+".");
            star.setSpan(new ForegroundColorSpan(0xFFFF0000),0,1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            q_No.setText(star);
        }
        else{
            q_No.setText(question.getQuestionNo()+".");
        }
        q_No.setTextSize(TypedValue.COMPLEX_UNIT_SP,20);
        q_No.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        q_text.setText(question.getQuestionText());
        q_text.setTextSize(TypedValue.COMPLEX_UNIT_SP,20);
        q_text.setPadding((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, getResources().getDisplayMetrics()),
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, getResources().getDisplayMetrics()),
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, getResources().getDisplayMetrics()),
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, getResources().getDisplayMetrics()));
        q_text.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        LinearLayout subll = new LinearLayout(getContext());
        subll.setOrientation(LinearLayout.HORIZONTAL);
        subll.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        subll.addView(q_No);
        subll.addView(q_text);
        if(question.isMandatory()){

        }
        ll.addView(subll);
        switch (question.getQuestionType()){
            case "TEXT":
                EditText ans = new EditText(getContext());
                ans.setHint("Enter answer here");
                ans.setTag(question.getQuestionNo());
                ll.addView(ans);
                break;
            case "SINGLE":
                RadioGroup radioGroup = new RadioGroup(getContext());
                radioGroup.setTag("rg"+ question.getQuestionNo());
                List<String> options = question.getOptions();
                for(int i = 0;i<question.getOptionCount();i++){
                    RadioButton opt = new RadioButton(getContext());
                    opt.setId(View.generateViewId());
                    opt.setText(options.get(i));
                    radioGroup.addView(opt);
                }
                ll.addView(radioGroup);
                break;
            case "MULTI":
                LinearLayout llcb = new LinearLayout(getContext());
                llcb.setOrientation(LinearLayout.VERTICAL);
                llcb.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                List<String> questionOptions = question.getOptions();
                for(int i = 0;i<question.getOptionCount();i++){
                    CheckBox cb = new CheckBox(getContext());
                    cb.setText(questionOptions.get(i));
                    cb.setTag("qu"+ question.getQuestionNo() + "op" + i);
                    llcb.addView(cb);
                }
                ll.addView(llcb);
                break;
        }
        cardView.addView(ll);
        return cardView;
    }

}
