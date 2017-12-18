package com.example.babajidemustapha.survey;


import android.*;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.utils.MPPointF;
import com.github.mikephil.charting.utils.ViewPortHandler;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * A simple {@link Fragment} subclass.
 */
public class BarChartFragment extends Fragment {

    int survey_id;
    SurveyDatabase db;
    RecyclerView recyclerView;
    List<Question> questions;
    Map<Question,Map<String,Integer>> xyCoord;
    private final int REQUEST_PERMISSIONS_CODE_WRITE_STORAGE = 18880;
    BarChart barChart1;
    public BarChartFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bar_chart,container,false);
        barChart1 = null;
        survey_id = getActivity().getIntent().getExtras().getInt("ID");
        db = new SurveyDatabase(getContext());
        questions = new ArrayList<>();
        xyCoord = new LinkedHashMap<>();
        Map<Question,List<Response>> reports = db.getReport(survey_id);
        buildXYcoord(reports);
        recyclerView = (RecyclerView) view.findViewById(R.id.barList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(new CustomAdapter1(questions));
        return view;
    }
    public void buildXYcoord(Map<Question,List<Response>> report){

        for (Map.Entry<Question,List<Response>> x : report.entrySet()) {
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
                    for (Response response: x.getValue()) {
                        if (!response.getResponse().trim().isEmpty()) {
                            if (xy.containsKey(response.getResponse())) {
                                int i = xy.get(response.getResponse()) + 1;
                                xy.put(response.getResponse(), i);
                            } else {
                                xy.put(response.getResponse(), 1);
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
                    for (Response response: x.getValue()) {
                        try {
                            JSONArray res = new JSONArray(response.getResponse());
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
            return new CustomAdapter1.ViewHolder(getActivity().getLayoutInflater().inflate(R.layout.bar_chart_view,parent, false));
        }

        @Override
        public void onBindViewHolder(CustomAdapter1.ViewHolder holder, int position) {
            holder.qu_no.setText( source.get(position).getQuestionNo()+"");
            holder.qu_text.setText(source.get(position).getQuestionText());
            switch(source.get(position).getQuestionType()){
                case "TEXT":
                    holder.barChart.setNoDataText("Bar Chart not available for this question type");
                    holder.btn.setVisibility(View.GONE);
                    break;
                default:
                    List<BarEntry> data = new ArrayList<>();
                    int i = 0;
                    boolean noData = true;
                    String[] labels = new String[xyCoord.get(source.get(position)).entrySet().size()];
                    List<Integer> colors = new ArrayList<>();
                    Log.e("sharp",xyCoord.get(source.get(position)).size()+"");
                    if(xyCoord.get(source.get(position)).size()<=0){
                        holder.barChart.setNoDataText("No response has been recorded for this question");
                        holder.btn.setVisibility(View.GONE);
                    }
                    else {
                        for (Map.Entry entry : xyCoord.get(source.get(position)).entrySet()) {
                            data.add(new BarEntry(i, ((int) entry.getValue())));
                            Random rnd = new Random();
                            int color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
                            colors.add(color);
                            labels[((int) (i))] = (String) entry.getKey();
                            i += 1;
                            if((int)entry.getValue()>0){
                                noData = false;
                            }
                        }
                        if(noData){
                            holder.barChart.setNoDataText("No response has been recorded for this question");
                            holder.btn.setVisibility(View.GONE);
                        }
                        else {
                            BarDataSet dataSet = new BarDataSet(data, "Responses");
                            dataSet.setColors(colors);
                            dataSet.setHighlightEnabled(true);
                            BarData barData = new BarData(dataSet);
                            barData.setValueTextSize(20);
                            barData.setBarWidth(0.5f);
                            barData.setValueFormatter(new IValueFormatter() {
                                @Override
                                public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
                                    return entry.getY() == 0 ? "" : ((int) entry.getY()) + "";
                                }
                            });
                            holder.barChart.setData(barData);
                            holder.barChart.setPinchZoom(true);
                            holder.barChart.getAxisRight().setEnabled(false);
                            XAxis xaxis = holder.barChart.getXAxis();
                            YAxis yaxis = holder.barChart.getAxisLeft();
                            yaxis.setDrawGridLines(false);
                            xaxis.setPosition(XAxis.XAxisPosition.BOTTOM);
                            xaxis.setDrawGridLines(false);
                            xaxis.setDrawAxisLine(true);
                            xaxis.setGranularity(1f);
                            xaxis.setValueFormatter(new MyXAxisValueFormatter(labels));
                            //   holder.barChart.setMarker(new MyMarkerView(getContext(),R.layout.marker_view,labels));
                            holder.barChart.getDescription().setEnabled(false);
                            // Legend legend = holder.barChart.getLegend();
                            holder.barChart.animateXY(3000, 3000);
                        }
                    }
                    break;
            }


        }

        @Override
        public int getItemCount() {
            return source.size();
        }

        protected class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            TextView qu_text;
            TextView qu_no;
            Button btn;
            BarChart barChart;

            private ViewHolder(View itemView) {
                super(itemView);
                qu_no = (TextView) itemView.findViewById(R.id.qu_no);
                qu_text = (TextView) itemView.findViewById(R.id.qu_text);
                barChart = (BarChart) itemView.findViewById(R.id.barChart);
                btn = (Button) itemView.findViewById(R.id.btn);
                btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        View view1 =  recyclerView.findViewHolderForAdapterPosition(getAdapterPosition()).itemView;
                         barChart1 = (BarChart)  view1.findViewById(R.id.barChart);
                        checkFilePermissionAndSave();
                    }
                });
                //itemView.setOnClickListener(this);
            }

            @Override
            public void onClick(View view) {
//                Intent intent = new Intent(getContext(), ResponseDetail.class);
//                intent.putExtra("survey_id",survey_id);
//                intent.putExtra("ID",source.get(getAdapterPosition()).getResponse_id());
//                startActivity(intent);
            }
        }
        protected class MyXAxisValueFormatter implements IAxisValueFormatter {

            private String[] mValues;

            public MyXAxisValueFormatter(String[] values) {
                this.mValues = values;
            }

            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                // "value" represents the position of the label on the axis (x or y)
                Log.e("AXIS VALUE",value+"");
                return mValues[(int) value];
            }

            /** this is only needed if numbers are returned, else return 0 */

            public int getDecimalDigits() { return 0; }
        }
    }
    private class MyMarkerView extends MarkerView {

        private TextView tvContent;
        private String[] labels;

        public MyMarkerView(Context context, int layoutResource, String[] labels) {
            super(context, layoutResource);
            tvContent = (TextView) findViewById(R.id.marker);
         //   tvContent = (TextView) getActivity().getLayoutInflater().inflate(layoutResource,null).findViewById(R.id.marker);
            this.labels = labels;
            // find your layout components
        }

        // callbacks everytime the MarkerView is redrawn, can be used to update the
        // content (user-interface)
        @Override
        public void refreshContent(Entry e, Highlight highlight) {
            Log.e("refreshing marker: ",labels[(int)e.getX()-1]);
            tvContent.setText(labels[(int)e.getX()-1] + ": " + e.getY());

            // this will perform necessary layouting
            super.refreshContent(e, highlight);
        }

        private MPPointF mOffset;

        @Override
        public MPPointF getOffset() {

            if(mOffset == null) {
                // center the marker horizontally and vertically
                mOffset = new MPPointF(-(getWidth() / 2), -getHeight());
            }

            return mOffset;
        }
    }
    public boolean saveImage(BarChart barChart){
        return barChart.saveToGallery("bar_chart_"+new SimpleDateFormat("yyyy_MM_dd_HHmmss").format(new Date()),100);
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
                        new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_PERMISSIONS_CODE_WRITE_STORAGE);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }
        else{
            if(barChart1 != null && saveImage(barChart1)){
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
                if(barChart1 != null && saveImage(barChart1)){
                    Toast.makeText(getContext(), "Chart saved", Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(getContext(), "Chart failed to save", Toast.LENGTH_SHORT).show();
                }
            }
            else{
                Toast.makeText(getContext(), "Chart failed to save", Toast.LENGTH_SHORT).show();
            }
        }

    }
}
