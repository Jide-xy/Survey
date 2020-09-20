package com.example.babajidemustapha.survey.features.dashboard.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.babajidemustapha.survey.R;
import com.example.babajidemustapha.survey.features.Reports.activities.SurveyReportActivity;
import com.example.babajidemustapha.survey.features.dashboard.activities.DashboardActivity;
import com.example.babajidemustapha.survey.features.dashboard.adapters.SurveyAdapter;
import com.example.babajidemustapha.survey.features.newsurvey.activities.QuestionsSetupActivity;
import com.example.babajidemustapha.survey.features.takesurvey.activities.SurveyAction;
import com.example.babajidemustapha.survey.shared.room.db.SurveyDatabase;
import com.example.babajidemustapha.survey.shared.room.entities.Survey;
import com.example.babajidemustapha.survey.shared.utils.DbOperationHelper;
import com.example.babajidemustapha.survey.shared.utils.DbOperationHelper.IDbOperationHelper;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import static com.example.babajidemustapha.survey.shared.room.entities.Survey.SurveyQueryResult;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnNavigationMenuSelected} interface
 * to handle interaction events.
 * Use the {@link SurveyList#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SurveyList extends Fragment implements SurveyAdapter.SurveyActionListener {

    public static final String TAG = SurveyList.class.getSimpleName();

    private SurveyDatabase db;
    private SurveyAdapter adapter1;
    private RecyclerView recyclerView;
    private CardView placeholder;
    private List<? extends Survey> surveys;
    private FloatingActionButton fab;

    private OnNavigationMenuSelected mListener;

    public SurveyList() {
        // Required empty public constructor
    }

    public static SurveyList newInstance() {
        return new SurveyList();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_survey_list, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();
        loadSurveys();

    }

    public void loadSurveys() {
        DbOperationHelper.execute(new IDbOperationHelper<List<SurveyQueryResult>>() {
            @Override
            public List<SurveyQueryResult> run() {
                return db.surveyDao().getAllSurveysWithResponseCount();
            }

            @Override
            public void onCompleted(List<SurveyQueryResult> surveys) {
                SurveyList.this.surveys = surveys;
                if (SurveyList.this.surveys.isEmpty()) {
                    recyclerView.setVisibility(View.GONE);
                    placeholder.setVisibility(View.VISIBLE);
                    return;
                }
                placeholder.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
                adapter1 = new SurveyAdapter(SurveyList.this.surveys, SurveyList.this);
                recyclerView.setAdapter(adapter1);
            }
        });

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
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void takeSurvey(Survey survey, boolean isOnline) {
        DbOperationHelper.execute(new IDbOperationHelper<Integer>() {
            @Override
            public Integer run() {
                return db.questionDao().getSurveyQuestionCount(survey.getId());
            }

            @Override
            public void onCompleted(Integer id) {
                Intent intent = new Intent(getActivity(), SurveyAction.class);
                intent.putExtra("name", survey.getName());
                intent.putExtra("ID", survey.getId());
                intent.putExtra("Description", survey.getDesc());
                intent.putExtra("quesNo", id);
                intent.putExtra("online", isOnline);
                startActivity(intent);
            }
        });

    }

    @Override
    public void viewReport(Survey survey) {
        Intent intent = new Intent(getActivity(), SurveyReportActivity.class);
        intent.putExtra("ID", survey.getId());
        intent.putExtra("name", survey.getName());
        startActivity(intent);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        db = SurveyDatabase.getInstance(getContext());
        mListener.setTitle("My Surveys");
        recyclerView = view.findViewById(R.id.surveyList);
        placeholder = view.findViewById(R.id.emptyPlaceholder);
        fab = view.findViewById(R.id.fab);
        surveys = new ArrayList<>();
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        fab.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), QuestionsSetupActivity.class);
            startActivity(intent);
        });
        placeholder.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), QuestionsSetupActivity.class);
            startActivity(intent);
        });
        //loadSurveys();
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.main, menu);
//        SearchView searchView =
//                (SearchView) menu.findItem(R.id.search).getActionView();
        MenuItem item = menu.findItem(R.id.search);
        SearchView sv = new SearchView(((DashboardActivity) getActivity()).getSupportActionBar().getThemedContext());
        item.setShowAsAction(item.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW | item.SHOW_AS_ACTION_IF_ROOM);
        item.setActionView(sv);
        sv.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (!query.isEmpty()) {
                    DbOperationHelper.execute(new IDbOperationHelper<List<Survey>>() {
                        @Override
                        public List<Survey> run() {
                            return db.surveyDao().searchSurveys(query);
                        }

                        @Override
                        public void onCompleted(List<Survey> surveys) {
                            adapter1.changeSource(surveys);
                        }
                    });
                    return true;
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (!newText.isEmpty()) {
                    DbOperationHelper.execute(new IDbOperationHelper<List<Survey>>() {
                        @Override
                        public List<Survey> run() {
                            return db.surveyDao().searchSurveys(newText);
                        }

                        @Override
                        public void onCompleted(List<Survey> surveys) {
                            adapter1.changeSource(surveys);
                        }
                    });
                    return true;
                }
                return false;
            }
        });

        super.onCreateOptionsMenu(menu, inflater);
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnNavigationMenuSelected {
        void setTitle(String title);
    }

}
