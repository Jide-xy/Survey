package com.example.babajidemustapha.survey;

import android.graphics.Color;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

public class ResponseDetail extends AppCompatActivity {

    int id;
    LinearLayout rootView;
    SurveyDatabase db;
    List<QuestionAndResponse> questions;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_response_detail);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Bundle bundle = getIntent().getExtras();
        id = bundle.getInt("ID");
        Log.e("id",id +"");
        rootView = findViewById(R.id.responseRootView);
        db = new SurveyDatabase(this);
        questions = db.getResponse(id);
        buildQuestions(questions,rootView);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
    public View buildQuestion(QuestionAndResponse question){
         CardView cardview = new CardView(this);
        CardView.LayoutParams layoutParams = new CardView.LayoutParams(CardView.LayoutParams.MATCH_PARENT,CardView.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(0,
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2, getResources().getDisplayMetrics()),
                0,
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2, getResources().getDisplayMetrics()));
        cardview.setLayoutParams(layoutParams);
        LinearLayout ll = new LinearLayout(this);
        ll.setOrientation(LinearLayout.VERTICAL);
        ll.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        ll.setPadding((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 16, getResources().getDisplayMetrics()),
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 16, getResources().getDisplayMetrics()),
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 16, getResources().getDisplayMetrics()),
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 16, getResources().getDisplayMetrics()));
        TextView q_text = new TextView(this);
        TextView q_No = new TextView(this);
        if(question.isMandatory()){
            SpannableString star = new SpannableString("*"+question.getQuestionNo()+".");
            star.setSpan(new ForegroundColorSpan(Color.RED),0,1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            q_No.setText(star);
        }
        else {
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
        LinearLayout subll = new LinearLayout(this);
        subll.setOrientation(LinearLayout.HORIZONTAL);
        subll.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        subll.addView(q_No);
        subll.addView(q_text);

        ll.addView(subll);
        switch (question.getQuestionType()){
            case "TEXT":
                TextView ans = new TextView(this);
                ans.setTag(question.getQuestionNo());
                ans.setText("Answer: "+ question.getResponse());
                ll.addView(ans);
                break;
            case "SINGLE":
                RadioGroup radioGroup = new RadioGroup(this);
                radioGroup.setTag("rg"+ question.getQuestionNo());
                List<String> options = question.getOptions();
                for(int i = 0;i<question.getOptionCount();i++){
                    RadioButton opt = new RadioButton(this);
                    opt.setId(View.generateViewId());
                    opt.setText(options.get(i));
                    if(options.get(i).equalsIgnoreCase(question.getResponse())){
                        opt.setChecked(true);
                    }
                    opt.setEnabled(false);
                    radioGroup.addView(opt);
                }
                ll.addView(radioGroup);
                break;
            case "MULTI":
                LinearLayout llcb = new LinearLayout(this);
                llcb.setOrientation(LinearLayout.VERTICAL);
                llcb.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                List<String> questionOptions = question.getOptions();
                JSONArray multiResponse = null;
               // Log.e("my response",question.getResponse());
                try {
                    multiResponse = new JSONArray(question.getResponse());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                for(int i = 0;i<question.getOptionCount();i++){
                    CheckBox cb = new CheckBox(this);
                    cb.setText(questionOptions.get(i));
                    cb.setTag("qu"+ question.getQuestionNo() + "op" + i);
                    if(multiResponse != null) {
                        for (int j = 0; j < multiResponse.length(); j++) {
                            try {
                                if(multiResponse.getString(j).equalsIgnoreCase(questionOptions.get(i))){
                                    cb.setChecked(true);
                                    break;
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    cb.setEnabled(false);
                    llcb.addView(cb);
                }
                ll.addView(llcb);
                break;
        }
        cardview.addView(ll);
        return cardview;
    }
    public void buildQuestions(List<QuestionAndResponse> questions, ViewGroup view){
        for(int i = 0; i< questions.size(); i++){
            view.addView(buildQuestion(questions.get(i)));
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
