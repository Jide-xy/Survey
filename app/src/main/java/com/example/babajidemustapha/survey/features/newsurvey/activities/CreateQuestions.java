package com.example.babajidemustapha.survey.features.newsurvey.activities;

import android.graphics.Color;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import com.example.babajidemustapha.survey.R;
import com.example.babajidemustapha.survey.shared.models.QuestionType;
import com.example.babajidemustapha.survey.shared.room.db.SurveyDatabase;
import com.example.babajidemustapha.survey.shared.room.entities.Question;
import com.example.babajidemustapha.survey.shared.room.entities.Survey;
import com.example.babajidemustapha.survey.shared.utils.DbOperationHelper;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class CreateQuestions extends AppCompatActivity {

    Button addQuestion;
    Button addOption;
    Button submit;
    LinearLayout optionConfig;
    EditText quesText;
    EditText optText;
    Spinner quesType;
    Switch mandatory;
    LinearLayout questionsPreview;
    LinearLayout optionsPreview;
    int i = 0;
    Boolean survey_privacy;
    String survey_name;
    String survey_desc;

    SurveyDatabase db;
    List<Question> questions;
    List<String> options;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_questions);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        db = SurveyDatabase.getInstance(this);
        Bundle bundle = getIntent().getExtras();
        survey_name = bundle.getString("survey name");
        survey_desc = bundle.getString("survey desc");
        survey_privacy = bundle.getBoolean("survey privacy");
        addQuestion = findViewById(R.id.addQuestion);
        addOption = findViewById(R.id.addOption);
        //  submit = (Button) findViewById(R.id.submit);
        optionConfig = findViewById(R.id.optionConfig);
        questionsPreview = findViewById(R.id.questionsPreview);
        optionsPreview = findViewById(R.id.optionsPreview);
        quesText = findViewById(R.id.questionText);
        optText = findViewById(R.id.optionText);
        quesType = findViewById(R.id.questionType);
        mandatory = findViewById(R.id.mandatory);
        questions = new ArrayList<>();
        options = new ArrayList<>();
        addQuestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tryAddQuestion();
            }
        });
        addOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(optText.getText().toString().isEmpty()){
                    Toast.makeText(CreateQuestions.this,"Option field cant be empty",Toast.LENGTH_SHORT).show();
                }
                else {
                    options.add(optText.getText().toString());
                    switch (quesType.getSelectedItem().toString()){
                        case "SINGLE OPTION":
                            RadioButton radioButton = new RadioButton(CreateQuestions.this);
                            radioButton.setText(optText.getText().toString());
                            radioButton.setEnabled(false);
                            optionsPreview.addView(radioButton);
                            optText.setText("");
                            break;
                        case "MULTIPLE OPTIONS":
                            CheckBox checkBox = new CheckBox(CreateQuestions.this);
                            checkBox.setText(optText.getText().toString());
                            checkBox.setEnabled(false);
                            optionsPreview.addView(checkBox);
                            optText.setText("");
                            break;
                    }
                }
            }
        });
        quesType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (parent.getItemAtPosition(position).toString()){
                    case "TEXT":
                        options.clear();
                        optionsPreview.removeAllViews();
                        optionConfig.setVisibility(View.GONE);
                        quesText.setEnabled(true);
                        quesText.requestFocus();
                        break;
                    case "SINGLE OPTION":
                        options.clear();
                        optionsPreview.removeAllViews();
                        optionConfig.setVisibility(View.VISIBLE);
                        quesText.setEnabled(true);
                        optText.requestFocus();
                        break;
                    case "MULTIPLE OPTIONS":
                        options.clear();
                        optionsPreview.removeAllViews();
                        optionConfig.setVisibility(View.VISIBLE);
                        quesText.setEnabled(true);
                        optText.requestFocus();
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                quesText.setText("");
                optText.setText("");
                options.clear();
                optionsPreview.removeAllViews();
                optionConfig.setVisibility(View.GONE);
                quesText.setEnabled(false);
            }
        });
//        submit.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if(questions.size() == 0){
//                    Toast.makeText(CreateQuestions.this,"You must add at least one question",Toast.LENGTH_SHORT).show();
//                }
//                else {
//                    Survey survey = new Survey();
//                    survey.setName(survey_name);
//                    survey.setDesc(survey_desc);
//                    survey.setUsername("");
//                    survey.setPrivacy(survey_privacy);
//                    survey.setDate(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
//                    db.createSurvey(survey,questions);
//                    Toast.makeText(CreateQuestions.this,"Your Survey has been recorded",Toast.LENGTH_SHORT).show();
//                    finish();
//                }
//            }
//        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
    public void tryAddQuestion(){
        Question question = new Question();
        switch (QuestionType.valueOf(quesType.getSelectedItem().toString())) {
            case SHORT_TEXT:
                if(quesText.getText().toString().isEmpty()){
                    Toast.makeText(this,"Question field can't be empty",Toast.LENGTH_SHORT).show();
                    quesText.requestFocus();
                    return;
                }
                else {
                    i++;
                    question.setQuestionText(quesText.getText().toString());
                    question.setMandatory(mandatory.isChecked());
                    question.setQuestionType(QuestionType.SHORT_TEXT);
                    question.setQuestionNo(questions.size()+1);
                }
                break;
            case SINGLE_OPTION:
                if(quesText.getText().toString().isEmpty()){
                    Toast.makeText(this,"Question field cant be empty",Toast.LENGTH_SHORT).show();
                    quesText.requestFocus();
                    return;
                }
              else if(options.isEmpty()){
                    Toast.makeText(this,"At least one option must be added",Toast.LENGTH_SHORT).show();
                    return;
                }
                else {
                    i++;
                    question.setQuestionText(quesText.getText().toString());
                    question.setMandatory(mandatory.isChecked());
                    question.setQuestionType(QuestionType.SINGLE_OPTION);
                    question.setQuestionNo(questions.size()+1);
                    question.setOptions(options);
                }
                break;
            case MULTIPLE_OPTION:
                if(quesText.getText().toString().isEmpty()){
                    Toast.makeText(this,"Question field cant be empty",Toast.LENGTH_SHORT).show();
                    quesText.requestFocus();
                    return;
                }
                else if(options.isEmpty()){
                    Toast.makeText(this,"At least one option must be added",Toast.LENGTH_SHORT).show();
                    return;
                }
                else {
                    i++;
                    question.setQuestionText(quesText.getText().toString());
                    question.setMandatory(mandatory.isChecked());
                    question.setQuestionType(QuestionType.MULTIPLE_OPTION);
                    question.setQuestionNo(questions.size()+1);
                    question.setOptions(options);
                }
                break;
            default:
                Toast.makeText(this,"Please select option type",Toast.LENGTH_SHORT).show();
                return;
        }
        questions.add(question);
        questionsPreview.removeAllViews();
        buildQuestions(questions,questionsPreview);
        optionsPreview.removeAllViews();
        quesType.setSelection(0);
        options.clear();
        quesText.setText("");
        optText.setText("");
        quesText.requestFocus();
    }

    public View buildQuestion(Question question){

        CardView cardView = new CardView(this);
        CardView.LayoutParams layoutParams = new CardView.LayoutParams(CardView.LayoutParams.MATCH_PARENT,CardView.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(0,
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2, getResources().getDisplayMetrics()),
                0,
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2, getResources().getDisplayMetrics()));
        cardView.setLayoutParams(layoutParams);
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
            case SHORT_TEXT:
                EditText ans = new EditText(this);
               ans.setEnabled(false);
                ll.addView(ans);
                break;
            case SINGLE_OPTION:
                RadioGroup radioGroup = new RadioGroup(this);
                radioGroup.setTag("rg"+ question.getQuestionNo());
                try {
                    JSONArray options = new JSONArray(question.getOptions().toString());
                    for (int i = 0; i < question.getOptionCount(); i++) {
                        RadioButton opt = new RadioButton(this);
                        opt.setId(View.generateViewId());
                        opt.setText(options.getString(i));
                        opt.setEnabled(false);
                        radioGroup.addView(opt);
                    }
                }
                catch (JSONException e){
                    e.printStackTrace();
                }
                ll.addView(radioGroup);
                break;
            case MULTIPLE_OPTION:
                LinearLayout llcb = new LinearLayout(this);
                llcb.setOrientation(LinearLayout.VERTICAL);
                llcb.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                try {
                    JSONArray questionOptions = new JSONArray(question.getOptions().toString());
                for(int i = 0;i<question.getOptionCount();i++){
                    CheckBox cb = new CheckBox(this);
                    cb.setText(questionOptions.getString(i));
                    cb.setTag("qu"+ question.getQuestionNo() + "op" + i);
                    cb.setEnabled(false);
                    llcb.addView(cb);
                }
                }
                catch (JSONException e){
                    e.printStackTrace();
                }
                ll.addView(llcb);
                break;
        }
        cardView.addView(ll);
        return cardView;
    }
    public void buildQuestions(List<Question> questions, ViewGroup view){
        for(int i = 0; i< questions.size(); i++){
            view.addView(buildQuestion(questions.get(i)));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.question_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.save) {
            if (questions.size() == 0) {
                Toast.makeText(CreateQuestions.this, "You must add at least one question", Toast.LENGTH_SHORT).show();
            } else {
                DbOperationHelper.execute(new DbOperationHelper.IDbOperationHelper<Void>() {
                    @Override
                    public Void run() {
                        Survey survey = new Survey();
                        survey.setName(survey_name);
                        survey.setDesc(survey_desc);
                        survey.setUsername("");
                        survey.setPrivacy(survey_privacy);
                        //survey.setDate(new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date()));
                        db.surveyDao().createSurveyWithQuestions(survey, questions);
                        return null;
                    }

                    @Override
                    public void onCompleted(Void aVoid) {
                        Toast.makeText(CreateQuestions.this, "Your Survey has been recorded", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
