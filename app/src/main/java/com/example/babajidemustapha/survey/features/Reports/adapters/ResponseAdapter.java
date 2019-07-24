package com.example.babajidemustapha.survey.features.Reports.adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.babajidemustapha.survey.R;
import com.example.babajidemustapha.survey.shared.room.entities.ResponseHeader;

import java.text.SimpleDateFormat;
import java.util.List;

public class ResponseAdapter extends RecyclerView.Adapter<ResponseAdapter.ViewHolder> {
    OnResponseSelectedListener mListener;
    private List<ResponseHeader> source;

    public ResponseAdapter(List<ResponseHeader> source, OnResponseSelectedListener mListener) {
        this.source = source;
        this.mListener = mListener;
    }

    public void add(ResponseHeader responseHeader) {
        source.add(responseHeader);
    }

    @Override
    public ResponseAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ResponseAdapter.ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.response_list_item_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(ResponseAdapter.ViewHolder holder, int position) {
        Log.e("id", source.get(position).getResponse_id() + "");
        holder.name.setText("ResponseDetail by: " + (!source.get(position).getRespondentName().isEmpty() ? source.get(position).getRespondentName() : "Anonymous"));
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

    public interface OnResponseSelectedListener {
        void onSelected(ResponseHeader responseHeader);
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
            mListener.onSelected(source.get(getAdapterPosition()));
        }
    }
}
