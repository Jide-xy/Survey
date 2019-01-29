package com.example.babajidemustapha.survey;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

/**
 * A simple {@link Fragment} subclass.
 */
public class SurveyDescription extends Fragment {

    int survey_id;
    String name;
    int quesNo;
    String desc;
    SurveyDatabase db;
    boolean online;

    public SurveyDescription() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
       View view =  inflater.inflate(R.layout.content_survey_description,container,false);
        Bundle bundle = getActivity().getIntent().getExtras();
        TextView txtName = view.findViewById(R.id.name);
        TextView txtDesc = view.findViewById(R.id.desc);
        TextView txtQues = view.findViewById(R.id.ques);
        survey_id = bundle.getInt("ID");
        online = bundle.getBoolean("online");
        db = new SurveyDatabase(getContext());
        name = bundle.getString("name");
        quesNo = bundle.getInt("quesNo");
        desc = bundle.getString("Description");
        txtName.setText(name);
        txtDesc.setText(desc);
        txtQues.setText(quesNo + " question(s)");
        if(online){
            TextView username = view.findViewById(R.id.username);
            username.setText("Survey By: @"+bundle.getString("Username"));
            username.setVisibility(View.VISIBLE);
        }
        return view;
    }

}
