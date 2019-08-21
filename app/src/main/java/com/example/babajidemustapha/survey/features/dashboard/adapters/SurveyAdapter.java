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

import java.util.List;

public class SurveyAdapter extends RecyclerView.Adapter<SurveyAdapter.ViewHolder> {
    private List<? extends Survey> source;
    private SurveyActionListener surveyActionListener;

    public SurveyAdapter(List<? extends Survey> source, SurveyActionListener surveyActionListener) {
        this.source = source;
        this.surveyActionListener = surveyActionListener;
    }

//    public void add(Survey survey) {
//        source.add(survey);
//    }

    public void changeSource(List<Survey> source) {
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
        holder.name.setText(source.get(position).getName());
        holder.desc.setText(source.get(position).getDesc());
        holder.date.setText(DateFormat.format("dd/MM/yy", source.get(position).getDate()));

//            holder.no_of_ques.setText(source.get(position).getNoOfQues()+" question(s)");
        holder.privacy.setText(source.get(position).isPrivacy() ? "(Public)" : "(Private)");
        if (source.get(position) instanceof Survey.SurveyQueryResult) {
            holder.no_of_responses.setText(String.valueOf(((Survey.SurveyQueryResult) source.get(position)).getResponseCount()));
        }
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
                            surveyActionListener.takeSurvey(source.get(getAdapterPosition()), false);
                            return true;
                        case R.id.action_view_report:
                            surveyActionListener.viewReport(source.get(getAdapterPosition()));
                            return true;
                    }
                    return false;
                }


            });
            popup.show();
        }
    }
}
