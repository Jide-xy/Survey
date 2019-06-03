package com.example.babajidemustapha.survey.features.newsurvey.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.babajidemustapha.survey.R;
import com.example.babajidemustapha.survey.features.newsurvey.activities.CreateQuestions;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CreateSurveyFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CreateSurveyFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    EditText name;
    EditText desc;
    Switch privacy;
    Button btn;

    public CreateSurveyFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CreateSurveyFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CreateSurveyFragment newInstance(String param1, String param2) {
        CreateSurveyFragment fragment = new CreateSurveyFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View layout = inflater.inflate(R.layout.fragment_create_survey, container, false);
        getActivity().setTitle("Survey Configuration");
        name = layout.findViewById(R.id.name);
        desc = layout.findViewById(R.id.desc);
        privacy = layout.findViewById(R.id.privacy);
        btn = layout.findViewById(R.id.go);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!name.getText().toString().isEmpty() || !desc.getText().toString().isEmpty()){
                    Intent intent = new Intent(getActivity(), CreateQuestions.class);
                    intent.putExtra("survey name",name.getText().toString());
                    intent.putExtra("survey desc",desc.getText().toString());
                    intent.putExtra("survey privacy",privacy.isChecked());
                    startActivity(intent);
                }
                else{
                    Toast.makeText(getContext(),"1 or more fields empty",Toast.LENGTH_SHORT).show();
                }
            }
        });
        return layout;
    }


}
