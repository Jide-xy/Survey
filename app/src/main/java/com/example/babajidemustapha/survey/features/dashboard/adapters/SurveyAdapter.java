package com.example.babajidemustapha.survey.features.dashboard.adapters;

import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;

import com.example.babajidemustapha.survey.R;
import com.example.babajidemustapha.survey.shared.room.entities.Survey;
import com.example.babajidemustapha.survey.shared.room.entities.SurveyWithResponseHeader;

import java.util.List;

public class SurveyAdapter extends RecyclerView.Adapter<SurveyAdapter.ViewHolder> {
    private List<SurveyWithResponseHeader> source;
    private SurveyActionListener surveyActionListener;

    public SurveyAdapter(List<SurveyWithResponseHeader> source, SurveyActionListener surveyActionListener) {
        this.source = source;
        this.surveyActionListener = surveyActionListener;
    }

//    public void add(Survey survey) {
//        source.add(survey);
//    }

    public void changeSource(List<SurveyWithResponseHeader> source) {
        this.source = source;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public SurveyAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new SurveyAdapter.ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.survey_item_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(SurveyAdapter.ViewHolder holder, int position) {
        holder.name.setText(source.get(position).getSurvey().getName());
        holder.desc.setText(source.get(position).getSurvey().getDesc());
        holder.date.setText(DateFormat.format("dd/MM/yy", source.get(position).getSurvey().getDate()));

//            holder.no_of_ques.setText(source.get(position).getNoOfQues()+" question(s)");
        holder.privacy.setText(source.get(position).getSurvey().isPrivacy() ? "(Public)" : "(Private)");
        holder.no_of_responses.setText(String.valueOf((source.get(position)).getResponseCount()));
    }

    @Override
    public int getItemCount() {
        return source.size();
    }

    public interface SurveyActionListener {
        void takeSurvey(Survey survey, boolean isOnline);

        void viewReport(Survey survey);
    }

    protected class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView name;
        TextView desc;
        TextView date;
        TextView no_of_responses;
        TextView privacy;

        private ViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            desc = itemView.findViewById(R.id.desc);
            date = itemView.findViewById(R.id.date);
            no_of_responses = itemView.findViewById(R.id.no_of_responses);
            privacy = itemView.findViewById(R.id.privacy);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {

            PopupMenu popup = new PopupMenu(view.getContext(), view);
            MenuInflater inflater = popup.getMenuInflater();
            inflater.inflate(R.menu.menu_survey_action, popup.getMenu());
            popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.action_take_survey:
                            surveyActionListener.takeSurvey(source.get(getAdapterPosition()).getSurvey(), false);
                            return true;
                        case R.id.action_view_report:
                            surveyActionListener.viewReport(source.get(getAdapterPosition()).getSurvey());
                            return true;
                    }
                    return false;
                }


            });
            popup.show();
        }
    }
}
