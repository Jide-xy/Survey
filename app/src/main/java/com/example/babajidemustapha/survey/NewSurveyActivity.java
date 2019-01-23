package com.example.babajidemustapha.survey;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

public class NewSurveyActivity extends AppCompatActivity {

    EditText name;
    EditText desc;
    Switch privacy;
    Button btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_survey);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        name = findViewById(R.id.name);
        desc = findViewById(R.id.desc);
        privacy = findViewById(R.id.privacy);
        btn = findViewById(R.id.go);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!name.getText().toString().isEmpty() || !desc.getText().toString().isEmpty()) {
                    Intent intent = new Intent(NewSurveyActivity.this, CreateQuestions.class);
                    intent.putExtra("survey name", name.getText().toString());
                    intent.putExtra("survey desc", desc.getText().toString());
                    intent.putExtra("survey privacy", privacy.isChecked());
                    startActivity(intent);
                } else {
                    Toast.makeText(NewSurveyActivity.this, "1 or more fields empty", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

}
