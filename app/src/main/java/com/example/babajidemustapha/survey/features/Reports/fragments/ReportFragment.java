package com.example.babajidemustapha.survey.features.Reports.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.appcompat.widget.SearchView;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.babajidemustapha.survey.R;
import com.example.babajidemustapha.survey.features.Reports.activities.SurveyReportActivity;
import com.example.babajidemustapha.survey.features.dashboard.activities.MainActivity;
import com.example.babajidemustapha.survey.shared.room.db.SurveyDatabase;
import com.example.babajidemustapha.survey.shared.room.entities.Survey;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class ReportFragment extends Fragment {

    SurveyDatabase db;
    CustomAdapter1 adapter1;
    RecyclerView recyclerView;
    TextView placeholder;
    List<Survey> surveys;
    public ReportFragment() {
        // Required empty public constructor
    }

@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
    db = SurveyDatabase.getInstance(getContext());
        getActivity().setTitle("Reports");
        View view = inflater.inflate(R.layout.fragment_survey_list, container, false);
    recyclerView = view.findViewById(R.id.surveyList);
    placeholder = view.findViewById(R.id.emptyPlaceholder);
        surveys= new ArrayList<>();
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    loadSurveys();
    setHasOptionsMenu(true);
        return view;
    }
    public void loadSurveys(){
        surveys = db.surveyDao().getAllSurveys();
        if(surveys.isEmpty()){
            recyclerView.setVisibility(View.GONE);
            placeholder.setVisibility(View.VISIBLE);
            return;
        }
        placeholder.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);
        adapter1 = new CustomAdapter1(surveys);
        recyclerView.setAdapter(adapter1);
    }

    private class CustomAdapter1 extends RecyclerView.Adapter<CustomAdapter1.ViewHolder> {
        List<Survey> source;

        private CustomAdapter1(List<Survey> source) {
            this.source = source;
        }

        private void add(Survey survey) {
            source.add(survey);
        }

        private void changeSource(List<Survey> source){
            this.source = source;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ViewHolder(getActivity().getLayoutInflater().inflate(R.layout.survey_item_layout,parent, false));
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.name.setText(source.get(position).getName().length() >30 ? source.get(position).getName().substring(0,30)+"...":source.get(position).getName());
            holder.desc.setText(source.get(position).getDesc().length() > 40 ? source.get(position).getDesc().substring(0,40)+"..." : source.get(position).getDesc());
            try {
                holder.date.setText(new SimpleDateFormat("yyyy-MM-dd").format(new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(source.get(position).getDate())));
            } catch (ParseException e) {
                e.printStackTrace();
                holder.date.setText(source.get(position).getDate());
            }

//            holder.no_of_ques.setText(source.get(position).getNoOfQues()+" question(s)");
            holder.privacy.setText(source.get(position).isPrivacy() ? "Public" : "Private");
        }

        @Override
        public int getItemCount() {
            return source.size();
        }

        protected class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            TextView name;
            TextView desc;
            TextView date;
            TextView no_of_ques;
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
                Intent intent = new Intent(getActivity(), SurveyReportActivity.class);
                intent.putExtra("ID",source.get(getAdapterPosition()).getId());
                intent.putExtra("name",source.get(getAdapterPosition()).getName());
                startActivity(intent);
            }
        }
    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.main, menu);
//        SearchView searchView =
//                (SearchView) menu.findItem(R.id.search).getActionView();
        MenuItem item = menu.findItem(R.id.search);
        SearchView sv = new SearchView(((MainActivity) getActivity()).getSupportActionBar().getThemedContext());
        MenuItemCompat.setShowAsAction(item, MenuItemCompat.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW | MenuItemCompat.SHOW_AS_ACTION_IF_ROOM);
        MenuItemCompat.setActionView(item, sv);
        sv.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                adapter1.changeSource(db.surveyDao().searchSurveys(query));
                adapter1.notifyDataSetChanged();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter1.changeSource(db.surveyDao().searchSurveys(newText));
                adapter1.notifyDataSetChanged();
                return true;
            }
        });

        super.onCreateOptionsMenu(menu,inflater);
    }
}
