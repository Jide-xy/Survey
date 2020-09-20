package com.example.babajidemustapha.survey.features.Reports.adapters;

import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.babajidemustapha.survey.R;
import com.example.babajidemustapha.survey.shared.room.entities.ResponseHeader;

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

    @NonNull
    @Override
    public ResponseAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ResponseAdapter.ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.response_list_item_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(ResponseAdapter.ViewHolder holder, int position) {
        Log.e("id", source.get(position).getResponseId() + "");
        holder.name.setText("Response by: " + (source.get(position).getRespondentName() != null && !source.get(position).getRespondentName().isEmpty() ? source.get(position).getRespondentName() : "Anonymous"));
        holder.date.setText(DateFormat.format("dd/MM/yy", source.get(position).getDate()));
        holder.time.setText(DateFormat.format("HH:mm", source.get(position).getDate()));
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
