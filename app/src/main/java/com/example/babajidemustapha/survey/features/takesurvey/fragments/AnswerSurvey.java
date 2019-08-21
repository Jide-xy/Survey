package com.example.babajidemustapha.survey.features.takesurvey.fragments;


import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.babajidemustapha.survey.R;
import com.example.babajidemustapha.survey.shared.room.db.SurveyDatabase;
import com.example.babajidemustapha.survey.shared.room.entities.Question;
import com.example.babajidemustapha.survey.shared.room.entities.ResponseDetail;
import com.example.babajidemustapha.survey.shared.room.entities.ResponseHeader;
import com.example.babajidemustapha.survey.shared.utils.DbOperationHelper;
import com.example.babajidemustapha.survey.shared.utils.DbOperationHelper.IDbOperationHelper;
import com.example.babajidemustapha.survey.shared.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

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
    String survey_name;
    boolean online;
    RequestQueue requestQueue;
    String device_token;
    public AnswerSurvey() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.content_take_survey, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        Bundle bundle = getActivity().getIntent().getExtras();
        survey_id = bundle.getInt("ID");
        survey_name = bundle.getString("name");
        online = bundle.getBoolean("online");
        requestQueue = Volley.newRequestQueue(getContext());
        Log.e("id",survey_id +"");
        rootView = view.findViewById(R.id.rootView);
        name = view.findViewById(R.id.respondentName);
        // btn= (Button) view.findViewById(R.id.submitResponse);
//        btn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                v.setEnabled(false);
////                if(name.getText().toString().isEmpty()){
////                    Toast.makeText(getContext(),"Please enter your name",Toast.LENGTH_SHORT).show();
////                }
////                else {
//                    if (validate(questions)) {
//                        if(!online) {
//                            submit();
//                            Toast.makeText(getContext(), "Your response has been recorded", Toast.LENGTH_SHORT).show();
//                            getActivity().finish();
//                        }
//                        else{
//                            saveOnlineResponse(questions);
//                        }
//                    } else {
//                        Toast.makeText(getContext(), "1 or more mandatory questions have not been answered", Toast.LENGTH_SHORT).show();
//                    }
//             //   }
//                v.setEnabled(true);
//            }
//        });
        if(!online) {
            db = SurveyDatabase.getInstance(getContext());
            DbOperationHelper.execute(new IDbOperationHelper<List<Question>>() {
                @Override
                public List<Question> run() {
                    return db.surveyDao().getQuestionsForSurveyWithOfflineId(survey_id);
                }

                @Override
                public void onCompleted(List<Question> questions) {
                    AnswerSurvey.this.questions = questions;
                    buildQuestions(AnswerSurvey.this.questions, rootView);
                }
            });
            getActivity().setTitle(survey_name.length() <= 10 ? survey_name : survey_name.substring(0, 7) + "...");
        }
        else {
            device_token = bundle.getString("device_token");
            questions = (ArrayList<Question>) bundle.getSerializable("questions");
            getActivity().setTitle(survey_name.length() <= 10 ? survey_name : survey_name.substring(0, 7) + "...");
            buildQuestions(questions, rootView);
        }
        super.onViewCreated(view, savedInstanceState);
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
                    case SHORT_TEXT:
                        EditText ans = rootView.findViewWithTag(questions.get(i).getQuestionNo());
                        if (ans.getText().toString().isEmpty()){
                            return false;
                        }
                        break;
                    case SINGLE_OPTION:
                        RadioGroup radioGroup = rootView.findViewWithTag("rg" + questions.get(i).getQuestionNo());
                        if(radioGroup.getCheckedRadioButtonId() == -1){
                            return false;
                        }
                        break;
                    case MULTIPLE_OPTION:
                        Question question = questions.get(i);
                        for(int j = 0 ; j < question.getOptionCount(); j++ ){
                            CheckBox cb = rootView.findViewWithTag("qu" + question.getQuestionNo() + "op" + j);
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

    public List<ResponseDetail> buildResponseDetail(List<Question> questions) {
        List<ResponseDetail> responseDetails = new ArrayList<>();
        for(int i = 0; i<questions.size(); i++) {
            Question question = questions.get(i);
            switch (question.getQuestionType()) {
                case SHORT_TEXT:
                    EditText ans = rootView.findViewWithTag(question.getQuestionNo());
                    if (!ans.getText().toString().isEmpty()){
                        // question.setAnswer(ans.getText().toString());
                        responseDetails.add(new ResponseDetail(question.getId(), ans.getText().toString()));
                    }
                    else{
                        responseDetails.add(new ResponseDetail(question.getId(), ""));
                    }
                    break;
                case SINGLE_OPTION:
                    RadioGroup radioGroup = rootView.findViewWithTag("rg" + question.getQuestionNo());
                    if(radioGroup.getCheckedRadioButtonId() != -1){
                        RadioButton radioButton = rootView.findViewById(radioGroup.getCheckedRadioButtonId());
                        responseDetails.add(new ResponseDetail(question.getId(), radioButton.getText().toString()));
                        // question.setAnswer(radioButton.getText().toString());
                    }
                    else{
                        responseDetails.add(new ResponseDetail(question.getId(), ""));
                    }
                    break;
                case MULTIPLE_OPTION:
                    List<String> cbList = new ArrayList<>();
                    for(int j = 0 ; j < question.getOptionCount(); j++ ){
                        CheckBox cb = rootView.findViewWithTag("qu" + question.getQuestionNo() + "op" + j);
                        if(cb.isChecked()){
                            cbList.add(cb.getText().toString());
                        }
                    }
                    if(!cbList.isEmpty()){
                        responseDetails.add(new ResponseDetail(question.getId(), cbList.toString()));
                    }
                    else{
                        responseDetails.add(new ResponseDetail(question.getId(), ""));
                    }
                    break;
            }
        }
        return responseDetails;
    }
    public ResponseHeader buildResponseHeader(){
        ResponseHeader header = new ResponseHeader(survey_id, name.getText().toString(), Calendar.getInstance().getTimeInMillis());
        return header;
    }
    public void storeResponse(List<Question> questions){

        db.responseHeaderDao().saveResponse(buildResponseHeader(), buildResponseDetail(questions));

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
                            Toast.makeText(getContext(), "ResponseDetail Successfully Posted", Toast.LENGTH_SHORT).show();
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

    public JSONArray buildResponseDetailJSON(List<ResponseDetail> responseDetails) throws JSONException {
        JSONArray jsonArray = new JSONArray();
        for (int i = 0; i < responseDetails.size(); i++) {
            ResponseDetail responseDetail = responseDetails.get(i);
            JSONObject object = new JSONObject();
            object.put("QUESTION_ID", responseDetail.getQuestion_id());
            object.put("RESPONSE", responseDetail.getResponse());
            jsonArray.put(object);
        }
        return jsonArray;
    }

    public View buildQuestion(Question question){

        CardView cardView = new CardView(getContext());
        CardView.LayoutParams layoutParams = new CardView.LayoutParams(CardView.LayoutParams.MATCH_PARENT,CardView.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(0,
                Utils.pxToDp(getResources(), 2),
                0,
                Utils.pxToDp(getResources(), 2));
        cardView.setLayoutParams(layoutParams);
        LinearLayout ll = new LinearLayout(getContext());
        ll.setOrientation(LinearLayout.VERTICAL);
        ll.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        ll.setPadding(Utils.pxToDp(getResources(), 16),
                Utils.pxToDp(getResources(), 16),
                Utils.pxToDp(getResources(), 16),
                Utils.pxToDp(getResources(), 16));
        TextView q_text = new TextView(getContext());
        TextView q_No = new TextView(getContext());
        if(question.isMandatory()){
            SpannableString star = new SpannableString("*"+question.getQuestionNo()+".");
            star.setSpan(new ForegroundColorSpan(0xFFFF0000),0,1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            q_No.setText(star);
        }
        else{
            q_No.setText(String.format(Locale.getDefault(), "%d.", question.getQuestionNo()));
        }
        q_No.setTextSize(TypedValue.COMPLEX_UNIT_SP,20);
        q_No.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        q_text.setText(question.getQuestionText());
        q_text.setTextSize(TypedValue.COMPLEX_UNIT_SP,20);
        q_text.setPadding(Utils.pxToDp(getResources(), 4),
                Utils.pxToDp(getResources(), 4),
                Utils.pxToDp(getResources(), 4),
                Utils.pxToDp(getResources(), 4));
        q_text.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        LinearLayout subll = new LinearLayout(getContext());
        subll.setOrientation(LinearLayout.HORIZONTAL);
        subll.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        subll.addView(q_No);
        subll.addView(q_text);
//        if(question.isMandatory()){
//
//        }
        ll.addView(subll);
        switch (question.getQuestionType()){
            case SHORT_TEXT:
                EditText ans = new EditText(getContext());
                ans.setHint("Enter answer here");
                ans.setTag(question.getQuestionNo());
                ll.addView(ans);
                break;
            case SINGLE_OPTION:
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
            case MULTIPLE_OPTION:
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.save_answers) {
            if (validate(questions)) {
                if (!online) {
                    DbOperationHelper.execute(new IDbOperationHelper<Void>() {
                        @Override
                        public Void run() {
                            submit();
                            return null;
                        }

                        @Override
                        public void onCompleted(Void aVoid) {
                            Toast.makeText(getContext(), "Your response has been recorded", Toast.LENGTH_SHORT).show();
                            getActivity().finish();
                        }
                    });
                } else {
                    saveOnlineResponse(questions);
                }
            } else {
                Toast.makeText(getContext(), "1 or more mandatory questions have not been answered", Toast.LENGTH_SHORT).show();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.take_survey_menu, menu);

        super.onCreateOptionsMenu(menu, inflater);
    }
}
