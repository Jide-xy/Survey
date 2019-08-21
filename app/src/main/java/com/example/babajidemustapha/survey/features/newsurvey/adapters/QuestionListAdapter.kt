package com.example.babajidemustapha.survey.features.newsurvey.adapters

import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.example.babajidemustapha.survey.R
import com.example.babajidemustapha.survey.shared.models.QuestionType
import com.example.babajidemustapha.survey.shared.models.QuestionType.*
import com.example.babajidemustapha.survey.shared.room.entities.Question

class QuestionListAdapter(private val listener: QuestionsSetupInteractionListener) : RecyclerView.Adapter<QuestionListAdapter.ViewHolder>() {

    private val questions: MutableList<Question> = mutableListOf()
    override fun onViewRecycled(holder: ViewHolder) {
        super.onViewRecycled(holder)
        holder.unbind()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.view_question_setup, parent, false))
    }

    override fun getItemCount(): Int = questions.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val question: Question = questions[position]
        question.questionNo = position + 1
        holder.bindTo(question)
    }

    fun addQuestion(questionType: QuestionType) {
        val question = Question()
        question.questionType = questionType
        questions.add(question)
//        if(questions.size == 1) notifyDataSetChanged() else
        notifyItemInserted(questions.size - 1)
        if (questions.size == 1) listener.isEmpty(questions.isNullOrEmpty())
    }

    fun validateList() {
        if (questions.isNullOrEmpty()) {
            listener.onSubmit(false, null, 0, "You must add at least one question")
            return
        }
        for ((index, question) in questions.withIndex()) {
            if (!validateQuestion(question)) {
                listener.onSubmit(false, questions, index, "Question cannot be blank")
                return
            }
            if (question.questionType.hasOptions() && question.options.isNullOrEmpty()) {
                listener.onSubmit(false, questions, index, "Question type of ${question.questionType.getDisplayName()} must have at least one option")
                return
            }
            if (question.questionType.hasOptions() && !validateQuestionOptions(question)) {
                listener.onSubmit(false, questions, index, "Question options cannot be blank")
                return
            }
        }
        listener.onSubmit(true, questions)
    }

    private fun validateQuestion(question: Question): Boolean {
        if (question.questionText.isNullOrBlank()) {
            return false
        }
        return true
    }

    private fun validateQuestionOptions(question: Question): Boolean {
        for (option in question.options) {
            if (option.isNullOrBlank()) {
                return false
            }
        }
        return true
    }


    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val questionNo: TextView = itemView.findViewById(R.id.question_no)
        private val deleteButton: ImageButton = itemView.findViewById(R.id.deleteButton)
        private val questionText: EditText = itemView.findViewById(R.id.question_text)
        private val compulsory: Switch = itemView.findViewById(R.id.compulsory)
        private val questionOptionsContainer: LinearLayout = itemView.findViewById(R.id.question_options_container)
        private val addOptionsButton: TextView = itemView.findViewById(R.id.add_options_button)
        private val textWatchers: MutableList<OptionsTextWatcher> = mutableListOf()
        private val questionTextWatcher = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                questions[adapterPosition].questionText = s.toString()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

        }

        inner class OptionsTextWatcher(private val index: Int) : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                questions[adapterPosition].options[index] = s.toString()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

        }

        init {
            deleteButton.setOnClickListener {
                questions.removeAt(adapterPosition)
                notifyItemRemoved(adapterPosition)
                notifyItemRangeChanged(adapterPosition, questions.size - adapterPosition)
                if (questions.size < 1) listener.isEmpty(questions.isNullOrEmpty())
            }
            questionText.addTextChangedListener(questionTextWatcher)
            compulsory.setOnCheckedChangeListener { _, isChecked ->
                questions[adapterPosition].isMandatory = isChecked
            }
            addOptionsButton.setOnClickListener {
                questions[adapterPosition].options.add("")
                notifyItemChanged(adapterPosition)
            }
        }

        fun bindTo(question: Question) {
            questionNo.text = "Question ${question.questionNo}"
            questionText.setText(question.questionText)
            compulsory.isChecked = question.isMandatory
            if (question.options.isNullOrEmpty()) questionText.requestFocus()
            when (question.questionType) {
                SHORT_TEXT, LONG_TEXT, IMAGES, REACTIONS -> {
                    questionOptionsContainer.visibility = View.GONE
                    addOptionsButton.visibility = View.GONE
                }
                SINGLE_OPTION, MULTIPLE_OPTION -> {
                    questionOptionsContainer.visibility = View.VISIBLE
                    addOptionsButton.visibility = View.VISIBLE
                    questionOptionsContainer.removeAllViews()
                    val layoutInflater = LayoutInflater.from(itemView.context)
                    for ((index, option) in question.options.withIndex()) {
                        val editText: EditText = layoutInflater.inflate(R.layout.view_option_edittext, questionOptionsContainer, false) as EditText
                        questionOptionsContainer.addView(editText)
                        val textWatcher = OptionsTextWatcher(index)
                        textWatchers.add(index, textWatcher)
                        editText.addTextChangedListener(textWatcher)
                        editText.setText(option)
                        if (index == question.options.size - 1) editText.requestFocus()
                    }
                }
                else -> {
                }
            }

        }

        fun unbind() {
            //questionText.removeTextChangedListener(questionTextWatcher)
            for (i in 0 until questionOptionsContainer.childCount) {
                val view = questionOptionsContainer.getChildAt(i)
                if (view is EditText) {
                    view.removeTextChangedListener(textWatchers[i])
                }
            }
        }
    }

    interface QuestionsSetupInteractionListener {
        fun onSubmit(isSuccessful: Boolean, questions: List<Question>? = null, index: Int? = null, message: String? = null)
        fun isEmpty(isEmpty: Boolean)
    }
}