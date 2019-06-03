package com.example.babajidemustapha.survey.features.Reports.fragments;


import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.babajidemustapha.survey.R;
import com.example.babajidemustapha.survey.shared.room.db.SurveyDatabase;
import com.example.babajidemustapha.survey.shared.room.entities.Question;
import com.example.babajidemustapha.survey.shared.room.entities.ResponseDetail;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;

import org.json.JSONArray;
import org.json.JSONException;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;


/**
 * A simple {@link Fragment} subclass.
 */
public class PieChartFragment extends Fragment {

    int survey_id;
    SurveyDatabase db;
    RecyclerView recyclerView;
    List<Question> questions;
    Map<Question,Map<String,Integer>> xyCoord;
    private final int REQUEST_PERMISSIONS_CODE_WRITE_STORAGE = 18880;
    PieChart pieChart1;
    public PieChartFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_pie_chart,container,false);
        pieChart1 = null;
        survey_id = getActivity().getIntent().getExtras().getInt("ID");
        db = SurveyDatabase.getInstance(getContext());
        questions = new ArrayList<>();
        xyCoord = new LinkedHashMap<>();
        Map<Question, List<ResponseDetail>> reports = db.surveyDao().generateReport(survey_id);
        buildXYcoord(reports);
        recyclerView = view.findViewById(R.id.pieList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(new CustomAdapter1(questions));
        return view;
    }

    public void buildXYcoord(Map<Question, List<ResponseDetail>> report) {

        for (Map.Entry<Question, List<ResponseDetail>> x : report.entrySet()) {
            Log.e("question NO: ", x.getKey().getQuestionNo()+"");
            switch (x.getKey().getQuestionType()){
                case "TEXT":
                    xyCoord.put(x.getKey(),null);
                    questions.add(x.getKey());
                    break;
                case "SINGLE":
                    Map<String,Integer> xy = new LinkedHashMap<>();
                    for(int i = 0;i<x.getKey().getOptions().size();i++){
                        xy.put(x.getKey().getOptions().get(i),0);
                    }
                    for (ResponseDetail responseDetail : x.getValue()) {
                        if (!responseDetail.getResponse().trim().isEmpty()) {
                            if (xy.containsKey(responseDetail.getResponse())) {
                                int i = xy.get(responseDetail.getResponse()) + 1;
                                xy.put(responseDetail.getResponse(), i);
                            } else {
                                xy.put(responseDetail.getResponse(), 1);
                            }
                        }
                    }
                    xyCoord.put(x.getKey(),xy);
                    questions.add(x.getKey());
                    break;
                case "MULTI":
                    Map<String,Integer> xy1 = new LinkedHashMap<>();
                    for(int i = 0;i<x.getKey().getOptions().size();i++){
                        xy1.put(x.getKey().getOptions().get(i),0);
                    }
                    for (ResponseDetail responseDetail : x.getValue()) {
                        try {
                            JSONArray res = new JSONArray(responseDetail.getResponse());
                            Log.e("multi ans", res.toString());
                            for(int i = 0; i <res.length(); i++){
                                if(xy1.containsKey(res.getString(i))){
                                    int j =  xy1.get(res.getString(i)) + 1;
                                    xy1.put(res.getString(i),j);
                                }
                                else{
                                    xy1.put(res.getString(i),1);
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                    xyCoord.put(x.getKey(),xy1);
                    questions.add(x.getKey());
                    break;
            }

        }
    }

    private class CustomAdapter1 extends RecyclerView.Adapter<CustomAdapter1.ViewHolder> {
        List<Question> source;

        private CustomAdapter1(List<Question> source) {
            this.source = source;
        }

        private void add(Question responseHeader) {
            source.add(responseHeader);
        }

        @Override
        public CustomAdapter1.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new CustomAdapter1.ViewHolder(getActivity().getLayoutInflater().inflate(R.layout.pie_chart_view,parent, false));
        }

        @Override
        public void onBindViewHolder(CustomAdapter1.ViewHolder holder, int position) {
            holder.qu_no.setText( source.get(position).getQuestionNo()+"");
            holder.qu_text.setText(source.get(position).getQuestionText());
            switch(source.get(position).getQuestionType()){
                case "TEXT":
                    holder.pieChart.setNoDataText("Pie Chart not available for this question type");
                    holder.btn.setVisibility(View.GONE);
                    break;
                default:
                    List<PieEntry> data = new ArrayList<>();
                    List<Integer> colors = new ArrayList<>();
                    boolean noData = true;
                 if(xyCoord.get(source.get(position)).size()<=0){
                     holder.pieChart.setNoDataText("No response has been recorded for this question");
                     holder.btn.setVisibility(View.GONE);
                 }
                 else {
                     for (Map.Entry entry : xyCoord.get(source.get(position)).entrySet()) {
                         PieEntry e = new PieEntry(((int) entry.getValue()), (String) entry.getKey());
                         Random rnd = new Random();
                         int color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
                         colors.add(color);
                         data.add(e);
                         if((int)entry.getValue()>0){
                             noData = false;
                         }
                     }
                     if(noData){
                         holder.pieChart.setNoDataText("No response has been recorded for this question");
                         holder.btn.setVisibility(View.GONE);
                     }
                     else {
                         PieDataSet dataSet = new PieDataSet(data, "Responses");
                         dataSet.setSliceSpace(1);
                         dataSet.setColors(colors);
                         PieData pieData = new PieData(dataSet);
                         pieData.setValueTextSize(20);
                         pieData.setValueFormatter(new PercentFormatter());
                         holder.pieChart.setData(pieData);
                         holder.pieChart.setUsePercentValues(true);
                         holder.pieChart.getDescription().setEnabled(false);
                         Legend legend = holder.pieChart.getLegend();
                         legend.setMaxSizePercent(1);
                         //  legend.setXOffset(5.0f);
                         legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
                         legend.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
                         legend.setOrientation(Legend.LegendOrientation.VERTICAL);
                         legend.setForm(Legend.LegendForm.CIRCLE);
                         holder.pieChart.animateXY(2000, 2000);
                         break;
                     }
                 }
            }


        }

        @Override
        public int getItemCount() {
            return source.size();
        }


        public void onClick(View view) {

            //
        }


        protected class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            TextView qu_text;
            TextView qu_no;
            Button btn;
            PieChart pieChart;

            private ViewHolder(View itemView) {
                super(itemView);
                qu_no = itemView.findViewById(R.id.qu_no);
                qu_text = itemView.findViewById(R.id.qu_text);
                pieChart = itemView.findViewById(R.id.pieChart);
                btn = itemView.findViewById(R.id.btn);
                btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        View view1 =  recyclerView.findViewHolderForAdapterPosition(getAdapterPosition()).itemView;
                        pieChart1 = view1.findViewById(R.id.pieChart);
                        checkFilePermissionAndSave();
                    }
                });
               // btn.setOnClickListener(this);
            }

            @Override
            public void onClick(View view) {
               }
        }
    }

    public boolean saveImage(PieChart pieChart){
        return pieChart.saveToGallery("pie_chart_"+new SimpleDateFormat("yyyy_MM_dd_HHmmss").format(new Date()),100);
    }
    public void checkFilePermissionAndSave(){
        if (ContextCompat.checkSelfPermission(getContext(),android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

            } else {

                // No explanation needed, we can request the permission.

                requestPermissions(
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_PERMISSIONS_CODE_WRITE_STORAGE);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }
        else{
            if(pieChart1 != null && saveImage(pieChart1)){
                Toast.makeText(getContext(), "Chart saved", Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(getContext(), "Chart failed to save", Toast.LENGTH_SHORT).show();
            }
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode,  String[] permissions, int[] grantResults) {
        if (requestCode == REQUEST_PERMISSIONS_CODE_WRITE_STORAGE) {
            if (permissions[0].equals(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if(pieChart1 != null && saveImage(pieChart1)){
                    Toast.makeText(getContext(), "Chart saved", Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(getContext(), "Chart failed to save", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

}
