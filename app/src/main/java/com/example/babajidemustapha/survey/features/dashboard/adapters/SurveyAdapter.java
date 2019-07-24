package com.example.babajidemustapha.survey.features.dashboard.adapters;

import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;

import com.example.babajidemustapha.survey.R;
import com.example.babajidemustapha.survey.shared.room.entities.Survey;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

public class SurveyAdapter extends RecyclerView.Adapter<SurveyAdapter.ViewHolder> {
    private List<Survey> source;
    private SurveyActionListener surveyActionListener;

    public SurveyAdapter(List<Survey> source, SurveyActionListener surveyActionListener) {
        this.source = source;
        this.surveyActionListener = surveyActionListener;
    }

    public void add(Survey survey) {
        source.add(survey);
    }

    public void changeSource(List<Survey> source) {
        this.source = source;
        notifyDataSetChanged();
    }

    @Override
    public SurveyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new SurveyAdapter.ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.survey_item_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(SurveyAdapter.ViewHolder holder, int position) {
        holder.name.setText(source.get(position).getName().length() > 30 ? source.get(position).getName().substring(0, 30) + "..." : source.get(position).getName());
        holder.desc.setText(source.get(position).getDesc().length() > 40 ? source.get(position).getDesc().substring(0, 40) + "..." : source.get(position).getDesc());
        try {
            holder.date.setText(
                    new SimpleDateFormat("yyyy-MM-dd").format(new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(
                            source.get(position).getDate())));
        } catch (ParseException e) {
            e.printStackTrace();
            holder.date.setText(source.get(position).getDate());
        }

//            holder.no_of_ques.setText(source.get(position).getNoOfQues()+" question(s)");
        holder.privacy.setText(source.get(position).isPrivacy() ? "Public" : "Private");
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
        TextView no_of_ques;
        TextView privacy;

        private ViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            desc = itemView.findViewById(R.id.desc);
            date = itemView.findViewById(R.id.date);
            //     no_of_ques = (TextView) itemView.findViewById(R.id.no_of_questions);
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
