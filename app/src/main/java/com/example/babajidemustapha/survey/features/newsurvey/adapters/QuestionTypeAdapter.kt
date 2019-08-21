package com.example.babajidemustapha.survey.features.newsurvey.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.babajidemustapha.survey.R
import com.example.babajidemustapha.survey.shared.models.QuestionType

class QuestionTypeAdapter(private val questionTypes: List<QuestionType>, private val questionTypeInteractionListener: QuestionTypeInteractionListener)
    : RecyclerView.Adapter<QuestionTypeAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.view_question_type, parent, false))
    }

    override fun getItemCount(): Int {
        return questionTypes.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindto(questionTypes[position])
    }


    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val questionTypeText: TextView = itemView.findViewById(R.id.question_type_text)
        private val questionTypeImage: ImageView = itemView.findViewById(R.id.question_type_image)

        fun bindto(questionType: QuestionType) {
            questionTypeText.text = questionType.getDisplayName()
            questionTypeImage.setImageResource(questionType.getImageResource())
            itemView.setOnClickListener {
                questionTypeInteractionListener.onSelectQuestionType(questionType)
            }
        }
    }

    interface QuestionTypeInteractionListener {
        fun onSelectQuestionType(questionType: QuestionType)
    }
}