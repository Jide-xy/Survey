package com.example.babajidemustapha.survey.features.newsurvey.adapters

import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.babajidemustapha.survey.R
import com.example.babajidemustapha.survey.shared.utils.getDisplayName
import com.example.babajidemustapha.survey.shared.utils.hasOptions
import com.jide.surveyapp.model.Option
import com.jide.surveyapp.model.Question
import com.jide.surveyapp.model.QuestionType
import com.jide.surveyapp.model.QuestionType.*
import com.jide.surveyapp.model.relations.QuestionWithOptions

class QuestionListAdapter(private val listener: QuestionsSetupInteractionListener)
    : ListAdapter<QuestionWithOptions, QuestionListAdapter.ViewHolder>(diffCallback) {

    private val focusedIndex = FocusedIndex()

    override fun onViewRecycled(holder: ViewHolder) {
        super.onViewRecycled(holder)
        holder.unbind()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.view_question_setup, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind()
    }

    fun addQuestion(questionType: QuestionType) {
        val question = QuestionWithOptions(Question(questionType = questionType), when (questionType) {
            SINGLE_OPTION, MULTIPLE_OPTION -> {
                //add two options by default
                listOf(Option(), Option())
            }
            else -> listOf(Option())
        })
        focusedIndex.apply {
            this.question = itemCount
            option = -1
        }
        submitList(currentList.toMutableList().apply { add(question) })
    }

    override fun onCurrentListChanged(previousList: MutableList<QuestionWithOptions>, currentList: MutableList<QuestionWithOptions>) {
        super.onCurrentListChanged(previousList, currentList)
        listener.isEmpty(currentList.isEmpty())
    }

    fun validateList() {
        if (itemCount == 0) {
            listener.onSubmit(false, null, 0, "You must add at least one question")
            return
        }
        for ((index, question) in currentList.withIndex()) {
            if (!validateQuestion(question)) {
                focusedIndex.apply {
                    this.question = index
                    option = -1
                }
                listener.onSubmit(false, currentList, index, "Question cannot be blank")
                return
            }
            if (question.question.questionType!!.hasOptions() && question.options.isNullOrEmpty()) {
                focusedIndex.apply {
                    this.question = index
                    option = -1
                }
                listener.onSubmit(false, currentList, index, "Question type of ${question.question.questionType!!.getDisplayName()} must have at least one option")
                return
            }
            if (question.question.questionType!!.hasOptions() && !validateQuestionOptions(question)) {
                listener.onSubmit(false, currentList, index, "Question options cannot be blank")
                return
            }
        }
        listener.onSubmit(true, currentList.mapIndexed { index, questionWithOptions -> questionWithOptions.copy(question = questionWithOptions.question.copy(questionNo = index + 1)) })
    }

    private fun validateQuestion(question: QuestionWithOptions): Boolean {
        return question.question.questionText.isNullOrBlank().not()
    }

    private fun validateQuestionOptions(question: QuestionWithOptions): Boolean {
        return question.options.any { it.optionText.isNullOrBlank() }.not()
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
                focusedIndex.apply {
                    question = adapterPosition
                    option = -1
                }
                listener.removeAnimation()
                submitList(currentList.toMutableList().apply {
                    val question = getItem(adapterPosition)
                    set(adapterPosition, question.copy(question = question.question.copy(questionText = s.toString())))
                }) {
                    listener.addAnimation()
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

        }

        inner class OptionsTextWatcher(private val index: Int) : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                focusedIndex.apply {
                    this.question = adapterPosition
                    option = index
                }
                listener.removeAnimation()
                submitList(currentList.toMutableList().apply {
                    val question = this[adapterPosition]
                    val option = question.options[index]
                    this[adapterPosition] = question.copy(options = question.options.toMutableList().apply { this[index] = option.copy(optionText = s.toString()) })
                }) {
                    listener.addAnimation()
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

        }

        init {
            deleteButton.setOnClickListener {
                focusedIndex.apply {
                    question = -1
                    option = -1
                }
                submitList(currentList.toMutableList().apply { removeAt(adapterPosition) })
            }
            compulsory.setOnCheckedChangeListener { _, isChecked ->
                val question = getItem(adapterPosition)
                submitList(currentList.toMutableList().apply {
                    this[adapterPosition] = question.copy(question = question.question.copy(mandatory = isChecked))
                })
            }
            addOptionsButton.setOnClickListener {
                val question = getItem(adapterPosition)
                focusedIndex.apply {
                    this.question = adapterPosition
                    option = question.options.size
                }
                submitList(
                        currentList.toMutableList().apply {
                            this[adapterPosition] = question.copy(options = question.options.toMutableList().apply { add(Option()) })
                        }
                )
            }
        }

        fun bind() {
            val question = getItem(adapterPosition)
            questionNo.text = "Question ${adapterPosition + 1}"
            questionText.removeTextChangedListener(questionTextWatcher)
            questionText.setText(question.question.questionText)
            questionText.addTextChangedListener(questionTextWatcher)
            compulsory.isChecked = question.question.mandatory
            if (focusedIndex.hasOptionFocused.not() && focusedIndex.question == adapterPosition) {
                questionText.requestFocus()
                questionText.setSelection(questionText.text.length)
            }
            when (question.question.questionType) {
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
                        editText.setText(option.optionText)
                        editText.addTextChangedListener(textWatcher)
                        if (focusedIndex.option == index) {
                            editText.requestFocus()
                            editText.setSelection(editText.text.length)
                        }
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
        fun onSubmit(isSuccessful: Boolean, questions: List<QuestionWithOptions>? = null, index: Int? = null, message: String? = null)
        fun isEmpty(isEmpty: Boolean)
        fun addAnimation()
        fun removeAnimation()
    }

    companion object {
        val diffCallback = object :
                DiffUtil.ItemCallback<QuestionWithOptions>() {
            override fun areItemsTheSame(oldItem: QuestionWithOptions, newItem: QuestionWithOptions): Boolean {
                return oldItem.question.id == newItem.question.id
            }

            override fun areContentsTheSame(oldItem: QuestionWithOptions, newItem: QuestionWithOptions): Boolean {
                return oldItem == newItem
            }

        }
    }

    data class FocusedIndex(var question: Int = -1, var option: Int = -1) {
        val hasOptionFocused: Boolean
            get() = option != -1
    }
}