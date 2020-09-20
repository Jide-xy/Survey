package com.example.babajidemustapha.survey.features.searchsurvey.fragments;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.babajidemustapha.survey.R;
import com.example.babajidemustapha.survey.features.takesurvey.activities.SurveyAction;
import com.example.babajidemustapha.survey.shared.models.QuestionType;
import com.example.babajidemustapha.survey.shared.room.entities.Question;
import com.example.babajidemustapha.survey.shared.room.entities.Survey;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.example.babajidemustapha.survey.features.dashboard.fragments.SurveyList.OnNavigationMenuSelected;



/**
 * A simple {@link Fragment} subclass.
 */
public class SearchSurvey extends Fragment {


    RequestQueue requestQueue;
    EditText editText;
    SharedPreferences preferences;
    RecyclerView recyclerView;
    ProgressBar progressBar;
    Button btn;
    String token;
    OnNavigationMenuSelected mListener;
    public SearchSurvey() {
        // Required empty public constructor
    }


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnNavigationMenuSelected) {
            mListener = (OnNavigationMenuSelected) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnNavigationMenuSelected");
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mListener.setTitle("Search Online Surveys");
        recyclerView = view.findViewById(R.id.online_surveys);
        progressBar = view.findViewById(R.id.search_progress);
        requestQueue = Volley.newRequestQueue(getContext());
        preferences = getActivity().getSharedPreferences("user_data",Context.MODE_PRIVATE);
        editText = view.findViewById(R.id.search_param);
        btn = view.findViewById(R.id.online_search);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btn.setEnabled(false);
                progressBar.setVisibility(View.VISIBLE);
                searchSurvey();
            }
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        // recyclerView.setAdapter(new CustomAdapter1(db.getMySurveys()));
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), RecyclerView.VERTICAL));
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_search_survey, container, false);
    }

    private List<Survey> buildSurvey(JSONObject object) throws JSONException {
        List<Survey> result = new ArrayList<>();
        for(int i = 0; i< object.getJSONArray("SURVEYS").length();i++){
            JSONObject object1 = object.getJSONArray("SURVEYS").getJSONObject(i);
            Survey survey = new Survey(object1.getInt("ID"), object1.getString("SURVEY_NAME"), object1.getLong("DATE_CREATED"), object1.getString("DESCRIPTION"),
                   object1.getString("USERNAME"),object1.getString("DEVICE_TOKEN"));
            int ques_no = 0;
            for(int j = 0; j< object.getJSONArray("QUESTIONS").length();j++){
                if(object.getJSONArray("QUESTIONS").getJSONObject(j).getInt("SURVEY_ID") == object1.getInt("ID")){
                    ques_no++;
                    JSONObject object2 = object.getJSONArray("QUESTIONS").getJSONObject(j);
                    Question question = new Question(object2.getInt("QUESTION_ID"), object2.getInt("QUESTION_NO"), QuestionType.valueOf(object2.getString("QUESTION_TYPE")), object2.getInt("SURVEY_ID"),
                            new JSONArray(object2.getString("QUESTION_OPTIONS")), object2.getBoolean("MANDATORY"),object2.getString("QUESTION_TEXT"));
                    //survey.addQuestion(question);
                }
            }
            survey.setNoOFQues(ques_no);
            result.add(survey);
        }
        return result;
    }

    private void searchSurvey() {
        String query = editText.getText().toString();
        if(query.isEmpty()){

        }
        else {
            JSONObject object = new JSONObject();
            try {
               // preferences.getInt("USER_ID",0)
                object.put("USER_ID", preferences.getInt("USER_ID",0));
                object.put("SEARCH_PARAM", query);
            } catch (JSONException e) {
                e.printStackTrace();
                return;
            }
          JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, "http://survhey.azurewebsites.net/survey/search",object,new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Log.e("ddd",response.toString());
                    progressBar.setVisibility(View.GONE);
                    btn.setEnabled(true);
                    try {
                        if(response.getString("STATUS").equalsIgnoreCase("success")){
                          recyclerView.setAdapter(new CustomAdapter1(buildSurvey(response)));
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
                    progressBar.setVisibility(View.GONE);
                    btn.setEnabled(true);
                    Toast.makeText(getContext(),"Failed to connect",Toast.LENGTH_SHORT).show();
                    error.printStackTrace();
                }
            });
            requestQueue.add(request);
        }

    }

    private class CustomAdapter1 extends RecyclerView.Adapter<CustomAdapter1.ViewHolder> {
        List<Survey> source;

        private CustomAdapter1(List<Survey> source) {
            this.source = source;
        }

        private void add(Survey survey) {
            source.add(survey);
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ViewHolder(getActivity().getLayoutInflater().inflate(R.layout.survey_item_layout,parent, false));
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.name.setText(source.get(position).getName().length() >30 ? source.get(position).getName().substring(0,30)+"...":source.get(position).getName());
            holder.desc.setText(source.get(position).getDesc().length() > 40 ? source.get(position).getDesc().substring(0,40)+"..." : source.get(position).getDesc());
            holder.date.setText(DateFormat.format("yyyy-MM-dd", source.get(position).getDate()));

//            holder.no_of_ques.setText(source.get(position).getNoOfQues()+" question(s)");
            holder.privacy.setText(source.get(position).isPrivacy() ? "Public" : "Private");
            holder.privacy.setVisibility(View.GONE);
        }

        @Override
        public int getItemCount() {
            return source.size();
        }

        protected class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            TextView name;
            TextView desc;
            TextView date;
  //          TextView no_of_ques;
            TextView privacy;

            private ViewHolder(View itemView) {
                super(itemView);
                name = itemView.findViewById(R.id.name);
                desc = itemView.findViewById(R.id.desc);
                date = itemView.findViewById(R.id.date);
                //     no_of_ques = (TextView) itemView.findViewById(R.id.no_of_questions);
                privacy = itemView.findViewById(R.id.privacy);
                itemView.setOnClickListener(this);
            }

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), SurveyAction.class);
                intent.putExtra("online", true);
                //intent.putExtra("questions", (Serializable) source.get(getAdapterPosition()).getQuestions());
                intent.putExtra("name", source.get(getAdapterPosition()).getName());
                intent.putExtra("ID", source.get(getAdapterPosition()).getId());
                intent.putExtra("Description", source.get(getAdapterPosition()).getDesc());
                intent.putExtra("Username", source.get(getAdapterPosition()).getUsername());
                intent.putExtra("device_token", source.get(getAdapterPosition()).getDeviceToken());
                intent.putExtra("quesNo", source.get(getAdapterPosition()).getNoOfQues());
                startActivity(intent);
            }
        }
    }


}
