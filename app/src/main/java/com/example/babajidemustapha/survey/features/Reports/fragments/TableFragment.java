package com.example.babajidemustapha.survey.features.Reports.fragments;


import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.babajidemustapha.survey.R;
import com.example.babajidemustapha.survey.shared.room.db.SurveyDatabase;
import com.example.babajidemustapha.survey.shared.room.entities.Question;
import com.example.babajidemustapha.survey.shared.room.entities.ResponseDetail;
import com.example.babajidemustapha.survey.shared.utils.DbOperationHelper;

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
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tableLayout = null;
        survey_id = getActivity().getIntent().getExtras().getInt("ID");
        db = SurveyDatabase.getInstance(getContext());
        questions = new ArrayList<>();
        xyCoord = new LinkedHashMap<>();
        recyclerView = view.findViewById(R.id.tableList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        DbOperationHelper.execute(new DbOperationHelper.IDbOperationHelper<Map<Question, List<ResponseDetail>>>() {
            @Override
            public Map<Question, List<ResponseDetail>> run() {
                return db.surveyDao().generateReport(survey_id);
            }

            @Override
            public void onCompleted(Map<Question, List<ResponseDetail>> reports) {
                buildXYcoord(reports);
                recyclerView.setAdapter(new CustomAdapter1(questions));
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        this.inflater = inflater;
        return inflater.inflate(R.layout.fragment_table, container, false);
    }

    public void buildXYcoord(Map<Question, List<ResponseDetail>> report) {

        for (Map.Entry<Question, List<ResponseDetail>> x : report.entrySet()) {
            Log.e("question NO: ", x.getKey().getQuestionNo()+"");
            switch (x.getKey().getQuestionType()){
                case "TEXT":
                case "SINGLE":
                    Map<String,Integer> xy = new LinkedHashMap<>();
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
                TextView response = tableRow.findViewById(R.id.responseDetail);
                TextView count = tableRow.findViewById(R.id.count);
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
                qu_no = itemView.findViewById(R.id.qu_no);
                qu_text = itemView.findViewById(R.id.qu_text);
                no_data = itemView.findViewById(R.id.nodata);
                tableLayout1 = itemView.findViewById(R.id.table);
                btn = itemView.findViewById(R.id.btn);
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
                tableLayout = view1.findViewById(R.id.table);
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
