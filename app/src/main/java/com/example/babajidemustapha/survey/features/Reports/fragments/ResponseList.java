package com.example.babajidemustapha.survey.features.Reports.fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.babajidemustapha.survey.R;
import com.example.babajidemustapha.survey.features.Reports.activities.ResponseDetail;
import com.example.babajidemustapha.survey.features.Reports.adapters.ResponseAdapter;
import com.example.babajidemustapha.survey.features.dashboard.fragments.SurveyList;
import com.example.babajidemustapha.survey.shared.room.db.SurveyDatabase;
import com.example.babajidemustapha.survey.shared.room.entities.ResponseHeader;
import com.example.babajidemustapha.survey.shared.utils.DbOperationHelper;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SurveyList.OnNavigationMenuSelected} interface
 * to handle interaction events.
 * Use the {@link SurveyList#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ResponseList extends Fragment implements ResponseAdapter.OnResponseSelectedListener {
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
    ResponseAdapter adapter1;

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
        return inflater.inflate(R.layout.content_response_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        db = SurveyDatabase.getInstance(getContext());
        Bundle bundle = getActivity().getIntent().getExtras();
        survey_id = bundle.getInt("ID");
        //Log.e("survey_id", survey_id+"");
        recyclerView = view.findViewById(R.id.responseList);
        placeholder = view.findViewById(R.id.emptyPlaceholder);
        responses= new ArrayList<>();
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        loadResponses();
    }

    public void loadResponses(){
        DbOperationHelper.execute(new DbOperationHelper.IDbOperationHelper<List<ResponseHeader>>() {
            @Override
            public List<ResponseHeader> run() {
                return db.responseHeaderDao().getResponseHeaders(survey_id);
            }

            @Override
            public void onCompleted(List<ResponseHeader> responseHeaders) {
                responses = responseHeaders;
                if (responses.isEmpty()) {
                    recyclerView.setVisibility(View.GONE);
                    placeholder.setVisibility(View.VISIBLE);
                    return;
                }
                placeholder.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
                adapter1 = new ResponseAdapter(responses, ResponseList.this);
                recyclerView.setAdapter(adapter1);
            }
        });

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

    @Override
    public void onSelected(ResponseHeader responseHeader) {
        Intent intent = new Intent(getContext(), ResponseDetail.class);
        intent.putExtra("survey_id", survey_id);
        intent.putExtra("ID", responseHeader.getResponse_id());
        startActivity(intent);
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

