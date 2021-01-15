package com.example.babajidemustapha.survey.shared.views

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.babajidemustapha.survey.R
import com.example.babajidemustapha.survey.shared.models.Reactions
import com.jide.surveyapp.model.Option
import com.jide.surveyapp.model.QuestionType
import com.jide.surveyapp.model.QuestionType.*
import com.jide.surveyapp.model.relations.QuestionWithOptionsAndResponse
import kotlinx.android.synthetic.main.view_question.view.*

class QuestionView : ConstraintLayout, RadioGroup.OnCheckedChangeListener, CompoundButton.OnCheckedChangeListener, TextWatcher, ReactionsViewGroup.OnCheckedChangeListener {
    override fun onCheckedChanged(group: ReactionsViewGroup, selectedReaction: Reactions?) {
        listener?.onReactionSelectResponse(selectedReaction!!)
    }

    override fun afterTextChanged(s: Editable?) {
        listener?.onTextResponse(s?.toString() ?: "")
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

    }

    override fun onCheckedChanged(group: RadioGroup?, checkedId: Int) {
        selectedRadioButtonIndex = question_options_container.indexOfChild(findViewById(checkedId))
        listener?.onSingleOptionsResponse(selectedRadioButtonIndex)
    }

    override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
        if (isChecked) selectedCheckBoxIndexes.add(question_options_container.indexOfChild(buttonView))
        else selectedCheckBoxIndexes.remove(question_options_container.indexOfChild(buttonView))
        listener?.onMultiSelectResponse(selectedCheckBoxIndexes)
    }

    var listener: OnResponseProvidedListener? = null

    var viewMode: Int?
    var compulsory: Boolean
        set(value) {
            field = value
            compulsory_indicator.visibility = (if (field) {
                View.VISIBLE
            } else {
                View.GONE
            })
        }
    var questionNo: Int?
        set(value) {
            field = value
            question_no.text = "Question $field/$total"
        }
    var questionText: String?
        set(value) {
            field = value
            question_text.text = field
        }
    var options: List<Option>?
        set(value) {
            field = value
            buildOptionsView(field)
        }

    var questionType: QuestionType
        private set(value) {
            field = value
            showCorrectOptionView(field)
        }
    var total: Int

    val selectedCheckBoxIndexes: MutableList<Int> = mutableListOf()
    var textAnswer: String? = null
        get() {
            return when (questionType) {
                SHORT_TEXT -> question_answer_short_text.text.toString()
                LONG_TEXT -> question_answer_long_text.text.toString()
                else -> throw RuntimeException("Can't fetch text result for non-text question type")
            }
        }
    var selectedRadioButtonIndex: Int = -1
    var selectedReaction: Reactions? = null
        get() = reactionsLayout.selectedReaction

//    constructor(context: Context) : this(context, 0,false,) {
//
//    }

    constructor(context: Context, viewMode: Int, question: QuestionWithOptionsAndResponse, total: Int, listener: OnResponseProvidedListener? = null) : super(context) {
        initializeView(context)
        this.viewMode = viewMode
        this.compulsory = question.question.mandatory
        this.questionText = question.question.questionText
        this.questionType = question.question.questionType!!
        this.options = question.options
        this.total = total
        this.questionNo = question.question.questionNo
        this.listener = listener
    }

//    constructor(context: Context, attrs: AttributeSet) : this(context) {
//
//    }
//
//    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : this(context) {
//
//    }

    private fun initializeView(context: Context) {
        val layoutInflater: LayoutInflater = LayoutInflater.from(context)
        layoutInflater.inflate(R.layout.view_question, this)
        layoutParams = LayoutParams(ViewGroup.LayoutParams(MATCH_PARENT, MATCH_PARENT))
        question_answer_short_text.addTextChangedListener(this)
        question_answer_long_text.addTextChangedListener(this)
        reactionsLayout.setOnCheckedChangeListener(this)
    }

//    override fun onFinishInflate() {
//        super.onFinishInflate()
//        this.viewMode = viewMode
//        this.compulsory = compulsory
//        this.questionText = questionText
//        this.questionType = questionType
//        this.options = options
//    }

    private fun buildOptionsView(options: List<Option>?) {
        if (options != null) {
            selectedRadioButtonIndex = -1
            selectedCheckBoxIndexes.clear()
            question_options_container.removeAllViews()
            val layoutInflater = LayoutInflater.from(this.context)
            for (option in options) {
                val view = layoutInflater.inflate(if (questionType == SINGLE_OPTION) R.layout.view_radio_button else R.layout.view_checkbox, question_options_container, false)
                (view as TextView).text = option.optionText
                when (view) {
                    is CheckBox -> view.setOnCheckedChangeListener(this)
                    is RadioButton -> question_options_container.setOnCheckedChangeListener(this)
                }
                question_options_container.addView(view)
            }
        }
    }

    private fun showCorrectOptionView(questionType: QuestionType) {
        when (questionType) {
            SHORT_TEXT -> {
                toggleViewVisibility(question_answer_short_text, View.VISIBLE)
                toggleViewVisibility(question_answer_long_text, View.GONE)
                toggleViewVisibility(question_options_container, View.GONE)
                toggleViewVisibility(reactionsLayout, View.GONE)
            }
            LONG_TEXT -> {
                toggleViewVisibility(question_answer_long_text, View.VISIBLE)
                toggleViewVisibility(question_options_container, View.GONE)
                toggleViewVisibility(question_answer_short_text, View.GONE)
                toggleViewVisibility(reactionsLayout, View.GONE)
            }
            SINGLE_OPTION, MULTIPLE_OPTION -> {
                toggleViewVisibility(question_options_container, View.VISIBLE)
                toggleViewVisibility(question_answer_short_text, View.GONE)
                toggleViewVisibility(question_answer_long_text, View.GONE)
                toggleViewVisibility(reactionsLayout, View.GONE)
            }
            REACTIONS -> {
                toggleViewVisibility(reactionsLayout, View.VISIBLE)
                toggleViewVisibility(question_options_container, View.GONE)
                toggleViewVisibility(question_answer_short_text, View.GONE)
                toggleViewVisibility(question_answer_long_text, View.GONE)
            }
            else -> {
            }
        }
    }

    private fun toggleViewVisibility(view: View, visibility: Int) {
        view.visibility = visibility
    }

    interface OnResponseProvidedListener {
        fun onTextResponse(response: String)
        fun onSingleOptionsResponse(response: Int)
        fun onMultiSelectResponse(response: List<Int>)
        fun onReactionSelectResponse(reaction: Reactions)
    }

    companion object {
        val VIEW_QUESTION = 0
        val VIEW_ANSWER = 1
    }

}