package com.example.babajidemustapha.survey;


import android.*;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
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
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.FileOutputStream;
import java.io.OutputStream;
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
public class TableFragment extends Fragment {

    int survey_id;
    SurveyDatabase db;
    RecyclerView recyclerView;
    List<Question> questions;
    Map<Question,Map<String,Integer>> xyCoord;
    private final int REQUEST_PERMISSIONS_CODE_WRITE_STORAGE = 18880;
    TableLayout tableLayout;
    LayoutInflater inflater;
    public TableFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_table, container, false);
        tableLayout = null;
        this.inflater = inflater;
        survey_id = getActivity().getIntent().getExtras().getInt("ID");
        db = new SurveyDatabase(getContext());
        questions = new ArrayList<>();
        xyCoord = new LinkedHashMap<>();
        Map<Question,List<Response>> reports = db.getReport(survey_id);
        buildXYcoord(reports);
        recyclerView = (RecyclerView) view.findViewById(R.id.tableList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(new CustomAdapter1(questions));
        return view;
    }
    public void buildXYcoord(Map<Question,List<Response>> report){

        for (Map.Entry<Question,List<Response>> x : report.entrySet()) {
            Log.e("question NO: ", x.getKey().getQuestionNo()+"");
            switch (x.getKey().getQuestionType()){
                case "TEXT":
                case "SINGLE":
                    Map<String,Integer> xy = new LinkedHashMap<>();
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

        @Override
        public CustomAdapter1.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new CustomAdapter1.ViewHolder(getActivity().getLayoutInflater().inflate(R.layout.table_view,parent, false));
        }

        @Override
        public void onBindViewHolder(CustomAdapter1.ViewHolder holder, int position) {
            holder.qu_no.setText( source.get(position).getQuestionNo()+"");
            holder.qu_text.setText(source.get(position).getQuestionText());
            int i = 0;
            if(xyCoord.get(source.get(position)).size()<=0){
               // holder.pieChart.setNoDataText("No response has been recorded for this question");
                holder.btn.setVisibility(View.GONE);
            }
            for (Map.Entry entry: xyCoord.get(source.get(position)).entrySet()) {
                TableRow tableRow = (TableRow) inflater.inflate(R.layout.table_row_view,null);
                TextView response = (TextView) tableRow.findViewById(R.id.response);
                TextView count = (TextView) tableRow.findViewById(R.id.count);
                response.setText((String) entry.getKey());
                count.setText((int) entry.getValue()+"");
                tableRow.setBackgroundColor(i%2==0?0xFFE0E0E0:0xFFFFFFFF);
                holder.tableLayout1.addView(tableRow);
                i++;
            }
        }

        @Override
        public int getItemCount() {
            return source.size();
        }



        protected class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            TextView qu_text;
            TextView qu_no;
            TextView no_data;
            Button btn;
            TableLayout tableLayout1;

            private ViewHolder(View itemView) {
                super(itemView);
                qu_no = (TextView) itemView.findViewById(R.id.qu_no);
                qu_text = (TextView) itemView.findViewById(R.id.qu_text);
                no_data = (TextView) itemView.findViewById(R.id.nodata);
                tableLayout1 = (TableLayout) itemView.findViewById(R.id.table);
                btn = (Button) itemView.findViewById(R.id.btn);
//                btn.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        View view1 =  recyclerView.findViewHolderForAdapterPosition(getAdapterPosition()).itemView;
//                        pieChart1 = (PieChart)  view1.findViewById(R.id.pieChart);
//                        checkFilePermissionAndSave();
//                    }
//                });
                btn.setOnClickListener(this);
            }

            @Override
            public void onClick(View view) {
                View view1 =  recyclerView.findViewHolderForAdapterPosition(getAdapterPosition()).itemView;
                        tableLayout = (TableLayout)  view1.findViewById(R.id.table);
                        checkFilePermissionAndSave();
            }
        }
    }
    public boolean saveImage(TableLayout tableLayout){
        Bitmap bitmap = getBitmapFromView(tableLayout);
        if( bitmap!= null){
           return saveBitmap(bitmap);
        }
        return false;
    }
    public static boolean saveBitmap(Bitmap bitmap){
        OutputStream stream = null;
        String filename = new SimpleDateFormat("ddMMyyyyHHmmss:SSSS").format(new Date());
        try {
            stream = new FileOutputStream(Environment.getExternalStorageDirectory().getAbsolutePath()
                   +"/DCIM/"+ filename + ".png");
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);

            stream.close();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }
    public static Bitmap getBitmapFromView(View view) {
        //Define a bitmap with the same size as the view
        Bitmap returnedBitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(),Bitmap.Config.ARGB_8888);
        //Bind a canvas to it
        Canvas canvas = new Canvas(returnedBitmap);
        //Get the view's background
        Drawable bgDrawable = view.getBackground();
        if (bgDrawable!=null)
            //has background drawable, then draw it on the canvas
            bgDrawable.draw(canvas);
        else
            //does not have background drawable, then draw white background on the canvas
            canvas.drawColor(Color.WHITE);
        // draw the view on the canvas
        view.draw(canvas);
        //return the bitmap
        return returnedBitmap;
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
            if(tableLayout != null && saveImage(tableLayout)){
                Toast.makeText(getContext(), "Table saved", Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(getContext(), "Table failed to save", Toast.LENGTH_SHORT).show();
            }
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode,  String[] permissions, int[] grantResults) {
        if (requestCode == REQUEST_PERMISSIONS_CODE_WRITE_STORAGE) {
            if (permissions[0].equals(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if(tableLayout != null && saveImage(tableLayout)){
                    Toast.makeText(getContext(), "Table saved", Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(getContext(), "Table failed to save", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

}
