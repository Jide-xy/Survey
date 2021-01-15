package com.example.babajidemustapha.survey.features.reports.activities

import android.graphics.Color
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.TypedValue
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.cardview.widget.CardView
import androidx.lifecycle.lifecycleScope
import com.example.babajidemustapha.survey.R
import com.example.babajidemustapha.survey.features.reports.viewmodel.ResponseListViewModel
import com.example.babajidemustapha.survey.shared.models.Reactions
import com.example.babajidemustapha.survey.shared.views.ReactionsViewGroup
import com.jide.surveyapp.model.QuestionType
import com.jide.surveyapp.model.relations.QuestionWithOptionsAndResponse
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class ResponseDetail : AppCompatActivity() {

    private val viewModel by viewModels<ResponseListViewModel>()
    private lateinit var responseHeaderId: String
    private lateinit var rootView: LinearLayout
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_response_detail)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        val bundle = intent.extras
        responseHeaderId = bundle.getString("ID")!!
        rootView = findViewById(R.id.responseRootView)
        viewModel.getResponseDetails(responseHeaderId)
        lifecycleScope.launchWhenStarted {
            viewModel.responseDetailFlow.collect {
                buildQuestions(it, rootView)
            }
        }
    }

    private fun buildQuestion(question: QuestionWithOptionsAndResponse): View {
        val cardview = CardView(this)
        val layoutParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT)
        layoutParams.setMargins(0,
                TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 6f, resources.displayMetrics).toInt(),
                0,
                TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 6f, resources.displayMetrics).toInt())
        cardview.layoutParams = layoutParams
        val ll = LinearLayout(this)
        ll.orientation = LinearLayout.VERTICAL
        ll.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        ll.setPadding(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 16f, resources.displayMetrics).toInt(),
                TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 16f, resources.displayMetrics).toInt(),
                TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 16f, resources.displayMetrics).toInt(),
                TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 16f, resources.displayMetrics).toInt())
        val q_text = TextView(this)
        val q_No = TextView(this)
        if (question.question.mandatory) {
            val star = SpannableString("*" + question.question.questionNo + ".")
            star.setSpan(ForegroundColorSpan(Color.RED), 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            q_No.text = star
        } else {
            q_No.text = "${question.question.questionNo}."
        }
        q_No.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20f)
        q_No.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        q_text.text = question.question.questionText
        q_text.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20f)
        q_text.setPadding(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4f, resources.displayMetrics).toInt(),
                TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4f, resources.displayMetrics).toInt(),
                TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4f, resources.displayMetrics).toInt(),
                TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4f, resources.displayMetrics).toInt())
        q_text.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        val subll = LinearLayout(this)
        subll.orientation = LinearLayout.HORIZONTAL
        subll.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        subll.addView(q_No)
        subll.addView(q_text)
        ll.addView(subll)
        when (question.question.questionType) {
            QuestionType.SHORT_TEXT, QuestionType.LONG_TEXT -> {
                val ans = TextView(this)
                ans.text = "Answer: ${question.responses.firstOrNull()?.freeTextResponse?.orEmpty()}"
                ll.addView(ans)
            }
            QuestionType.SINGLE_OPTION -> {
                val radioGroup = RadioGroup(this)
                question.options.forEach {
                    val opt = RadioButton(this)
                    opt.id = View.generateViewId()
                    opt.text = it.optionText
                    if (it.id == question.responses.firstOrNull()?.optionId) {
                        opt.isChecked = true
                    }
                    opt.isEnabled = false
                    radioGroup.addView(opt)
                }
                ll.addView(radioGroup)
            }
            QuestionType.REACTIONS -> {
                val reactionsViewGroup = ReactionsViewGroup(this)
                if (question.responses.firstOrNull()?.freeTextResponse.isNullOrBlank().not()) {
                    reactionsViewGroup.check(Reactions.valueOf(question.responses.first().freeTextResponse!!).getReactionViewId())
                }
                reactionsViewGroup.disableAllButtons()
                ll.addView(reactionsViewGroup)
            }
            QuestionType.MULTIPLE_OPTION -> {
                val llcb = LinearLayout(this)
                llcb.orientation = LinearLayout.VERTICAL
                llcb.layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                question.options.forEach {
                    val cb = CheckBox(this)
                    cb.text = it.optionText
                    cb.isChecked = question.responses.any { responseDetail -> responseDetail.optionId == it.id }
                    cb.isEnabled = false
                    llcb.addView(cb)
                }
                ll.addView(llcb)
            }
        }
        cardview.addView(ll)
        return cardview
    }

    private fun buildQuestions(questions: List<QuestionWithOptionsAndResponse>, view: ViewGroup) {
        view.clipToPadding = false
        questions.forEach {
            view.addView(buildQuestion(it))
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}