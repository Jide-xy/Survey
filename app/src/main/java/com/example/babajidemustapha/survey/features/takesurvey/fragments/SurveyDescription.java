package com.example.babajidemustapha.survey.features.takesurvey.fragments;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.babajidemustapha.survey.R;

import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 */
public class SurveyDescription extends Fragment {

    int survey_id;
    String name;
    int quesNo;
    String desc;
    boolean online;

    public SurveyDescription() {
        // Required empty public constructor
    }

//    public static SurveyDescription newInstance(int survey_id,
//                                                String name,
//                                                int quesNo,
//                                                String desc,
//                                                boolean online){
//        SurveyDescription surveyDescription = new SurveyDescription();
//    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.content_survey_description, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Bundle bundle = getActivity().getIntent().getExtras();
        TextView txtName = view.findViewById(R.id.name);
        TextView txtDesc = view.findViewById(R.id.desc);
        TextView txtQues = view.findViewById(R.id.ques);
        survey_id = bundle.getInt("ID");
        online = bundle.getBoolean("online");
        name = bundle.getString("name");
        quesNo = bundle.getInt("quesNo");
        desc = bundle.getString("Description");
        txtName.setText(name);
        txtDesc.setText(desc);
        txtQues.setText(String.format(Locale.ENGLISH, "%d %s", quesNo, quesNo > 1 ? "questions" : "question"));
        if(online){
            TextView username = view.findViewById(R.id.username);
            username.setText("Survey By: @"+bundle.getString("Username"));
            username.setVisibility(View.VISIBLE);
        }
    }

}
