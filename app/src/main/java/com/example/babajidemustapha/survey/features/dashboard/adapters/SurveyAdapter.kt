package com.example.babajidemustapha.survey.features.dashboard.adapters

import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import com.example.babajidemustapha.survey.R
import com.jide.surveyapp.model.Survey

class SurveyAdapter(private var source: List<Survey>, private val surveyActionListener: SurveyActionListener) : RecyclerView.Adapter<SurveyAdapter.ViewHolder>() {

    fun changeSource(source: List<Survey>) {
        this.source = source
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.survey_item_layout, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.name.text = source[position].name
        holder.desc.text = source[position].description
        holder.date.text = DateFormat.format("dd/MM/yy", source[position].dateCreated.toLong())

//            holder.no_of_ques.setText(source.get(position).getNoOfQues()+" question(s)");
        holder.privacy.text = if (source[position].shared) "(Public)" else "(Private)"
        holder.no_of_responses.text = source[position].responseCount.toString()
    }

    override fun getItemCount(): Int {
        return source.size
    }

    interface SurveyActionListener {
        fun takeSurvey(survey: Survey, isOnline: Boolean)
        fun viewReport(survey: Survey)
    }

    inner class ViewHolder constructor(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        var name: TextView
        var desc: TextView
        var date: TextView
        var no_of_responses: TextView
        var privacy: TextView
        override fun onClick(view: View) {
            val popup = PopupMenu(view.context, view)
            val inflater = popup.menuInflater
            inflater.inflate(R.menu.menu_survey_action, popup.menu)
            popup.setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.action_take_survey -> {
                        surveyActionListener.takeSurvey(source[adapterPosition], false)
                        return@OnMenuItemClickListener true
                    }
                    R.id.action_view_report -> {
                        surveyActionListener.viewReport(source[adapterPosition])
                        return@OnMenuItemClickListener true
                    }
                }
                false
            })
            popup.show()
        }

        init {
            name = itemView.findViewById(R.id.name)
            desc = itemView.findViewById(R.id.desc)
            date = itemView.findViewById(R.id.date)
            no_of_responses = itemView.findViewById(R.id.no_of_responses)
            privacy = itemView.findViewById(R.id.privacy)
            itemView.setOnClickListener(this)
        }
    }
}