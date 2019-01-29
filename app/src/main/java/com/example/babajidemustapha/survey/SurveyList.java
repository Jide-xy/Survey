package com.example.babajidemustapha.survey;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.SearchView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SurveyList.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SurveyList#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SurveyList extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    Context context;
    SurveyDatabase db;
    CustomAdapter1 adapter1;
    RecyclerView recyclerView;
    TextView placeholder;
    List<Survey> surveys;
    FloatingActionButton fab;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public SurveyList() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SurveyList.
     */
    // TODO: Rename and change types and number of parameters
    public static SurveyList newInstance(String param1, String param2) {
        SurveyList fragment = new SurveyList();
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
        db = new SurveyDatabase(getContext());
        getActivity().setTitle("My Surveys");
        View view = inflater.inflate(R.layout.fragment_survey_list, container, false);
        recyclerView = view.findViewById(R.id.surveyList);
        placeholder = view.findViewById(R.id.emptyPlaceholder);
        fab = view.findViewById(R.id.fab);
        surveys= new ArrayList<>();
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), NewSurveyActivity.class);
                startActivity(intent);
            }
        });
        loadSurveys();
        setHasOptionsMenu(true);
       // recyclerView.addItemDecoration(new DividerItemDecoration(getContext(),R.drawable.divider));
        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    public void loadSurveys(){
        surveys = db.getMySurveys();
        if(surveys.isEmpty()){
           recyclerView.setVisibility(View.GONE);
           placeholder.setVisibility(View.VISIBLE);
           return;
        }
        placeholder.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);
        adapter1 = new CustomAdapter1(db.getMySurveys());
        recyclerView.setAdapter(adapter1);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

//    @Override
//    public void OnViewCreated(View view, @Nullable Bundle savedInstanceState){
//        super.onViewCreated(view,savedInstanceState);
//    }

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
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.main, menu);
//        SearchView searchView =
//                (SearchView) menu.findItem(R.id.search).getActionView();
        MenuItem item = menu.findItem(R.id.search);
        SearchView sv = new SearchView(((MainActivity) getActivity()).getSupportActionBar().getThemedContext());
        item.setShowAsAction(item.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW | item.SHOW_AS_ACTION_IF_ROOM);
        item.setActionView(sv);
        sv.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (!query.isEmpty()) {
                    adapter1.changeSource(db.filterSurveys(query));
                    adapter1.notifyDataSetChanged();
                    return true;
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (!newText.isEmpty()) {
                    adapter1.changeSource(db.filterSurveys(newText));
                    adapter1.notifyDataSetChanged();
                    return true;
                }
                return false;
            }
        });

        super.onCreateOptionsMenu(menu, inflater);
    }

    public class DividerItemDecoration extends RecyclerView.ItemDecoration {

        private Drawable divider;


        /**
         * Custom divider will be used
         */
        public DividerItemDecoration(Context context, int resId) {
            divider = ContextCompat.getDrawable(context, resId);
        }

        @Override
        public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
            int left = parent.getPaddingLeft();
            int right = parent.getWidth() - parent.getPaddingRight();

            int childCount = parent.getChildCount();
            for (int i = 0; i < childCount; i++) {
                View child = parent.getChildAt(i);

                RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();

                int top = child.getBottom() + params.bottomMargin;
                int bottom = top + divider.getIntrinsicHeight();

                divider.setBounds(left, top, right, bottom);
                divider.draw(c);
            }

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

        private void changeSource(List<Survey> source) {
            this.source = source;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ViewHolder(getActivity().getLayoutInflater().inflate(R.layout.survey_item_layout, parent, false));
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.name.setText(source.get(position).getName().length() > 30 ? source.get(position).getName().substring(0, 30) + "..." : source.get(position).getName());
            holder.desc.setText(source.get(position).getDesc().length() > 40 ? source.get(position).getDesc().substring(0, 40) + "..." : source.get(position).getDesc());
            try {
                holder.date.setText(new SimpleDateFormat("yyyy-MM-dd").format(new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(source.get(position).getDate())));
            } catch (ParseException e) {
                e.printStackTrace();
                holder.date.setText(source.get(position).getDate());
            }

//            holder.no_of_ques.setText(source.get(position).getNoOfQues()+" question(s)");
            holder.privacy.setText(source.get(position).isPrivate() ? "Public" : "Private");
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

                PopupMenu popup = new PopupMenu(getContext(), view);
                MenuInflater inflater = popup.getMenuInflater();
                inflater.inflate(R.menu.menu_survey_action, popup.getMenu());
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        Intent intent;
                        switch (item.getItemId()) {
                            case R.id.action_take_survey:
                                intent = new Intent(getActivity(), SurveyAction.class);
                                intent.putExtra("name", source.get(getAdapterPosition()).getName());
                                intent.putExtra("ID", source.get(getAdapterPosition()).getId());
                                intent.putExtra("Description", source.get(getAdapterPosition()).getDesc());
                                intent.putExtra("quesNo", source.get(getAdapterPosition()).getNoOfQues());
                                intent.putExtra("online", false);
                                startActivity(intent);
                                return true;
                            case R.id.action_view_report:
                                intent = new Intent(getActivity(), SurveyReportActivity.class);
                                intent.putExtra("ID", source.get(getAdapterPosition()).getId());
                                intent.putExtra("name", source.get(getAdapterPosition()).getName());
                                startActivity(intent);
                                return true;
                        }
                        return false;
                    }


                });
                popup.show();
            }
        }
    }
}
