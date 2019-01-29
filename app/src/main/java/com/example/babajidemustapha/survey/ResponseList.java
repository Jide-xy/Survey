package com.example.babajidemustapha.survey;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

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
public class ResponseList extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    RecyclerView recyclerView;
    SurveyDatabase db;
    int survey_id;
    Context context;
    TextView placeholder;
    List<ResponseHeader> responses;
    CustomAdapter1 adapter1;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public ResponseList() {
        // Required empty public constructor
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
        View view = inflater.inflate(R.layout.content_response_list, container, false);
        Bundle bundle = getActivity().getIntent().getExtras();
        survey_id = bundle.getInt("ID");
        Log.e("survey_id", survey_id+"");
        recyclerView = view.findViewById(R.id.responseList);
        placeholder = view.findViewById(R.id.emptyPlaceholder);
        responses= new ArrayList<>();
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        loadResponses();
//        if(bundle.getBoolean("from_notification")){
//            long response_id = bundle.getLong("response_id");
//           recyclerView.getLayoutManager().scrollToPosition(3);
//          // adapter1.
//        }
        return view;
    }

    public void loadResponses(){
        responses = db.getResponseHeader(survey_id);
        if(responses.isEmpty()){
            recyclerView.setVisibility(View.GONE);
            placeholder.setVisibility(View.VISIBLE);
            return;
        }
        placeholder.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);
        adapter1 = new CustomAdapter1(responses);
        recyclerView.setAdapter(adapter1);
    }
    private class CustomAdapter1 extends RecyclerView.Adapter<CustomAdapter1.ViewHolder> {
        List<ResponseHeader> source;

        private CustomAdapter1(List<ResponseHeader> source) {
            this.source = source;
        }

        private void add(ResponseHeader responseHeader) {
            source.add(responseHeader);
        }

        @Override
        public CustomAdapter1.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new CustomAdapter1.ViewHolder(getActivity().getLayoutInflater().inflate(R.layout.response_list_item_layout,parent, false));
        }

        @Override
        public void onBindViewHolder(CustomAdapter1.ViewHolder holder, int position) {
            Log.e("id",source.get(position).getResponse_id()+"");
            holder.name.setText("Response by: " + (!source.get(position).getRespondentName().isEmpty() ? source.get(position).getRespondentName(): "Anonymous"));
            try {
                holder.date.setText(new SimpleDateFormat("yyyy-MM-dd").format(new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(source.get(position).getDate())));
                holder.time.setText(new SimpleDateFormat("HH:mm").format(new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(source.get(position).getDate())));

            } catch (Exception e) {
                holder.date.setText(source.get(position).getDate());
                holder.time.setText("00:00");
                e.printStackTrace();
            }
        }

        @Override
        public int getItemCount() {
            return source.size();
        }

        protected class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            TextView name;
            TextView date;
            TextView time;

            private ViewHolder(View itemView) {
                super(itemView);
                name = itemView.findViewById(R.id.response_name);
                date = itemView.findViewById(R.id.response_date);
                time = itemView.findViewById(R.id.response_time);
                itemView.setOnClickListener(this);
            }

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), ResponseDetail.class);
                intent.putExtra("survey_id",survey_id);
                intent.putExtra("ID",source.get(getAdapterPosition()).getResponse_id());
                startActivity(intent);
            }
        }
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

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
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


}

