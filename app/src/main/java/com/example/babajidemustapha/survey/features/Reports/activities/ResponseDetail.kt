package com.example.babajidemustapha.survey.features.Reports.activities

import android.graphics.Color
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.util.TypedValue
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.cardview.widget.CardView
import com.example.babajidemustapha.survey.R
import com.example.babajidemustapha.survey.shared.models.QuestionAndResponse
import com.example.babajidemustapha.survey.shared.models.QuestionType
import com.example.babajidemustapha.survey.shared.models.Reactions
import com.example.babajidemustapha.survey.shared.room.db.SurveyDatabase
import com.example.babajidemustapha.survey.shared.utils.DbOperationHelper
import com.example.babajidemustapha.survey.shared.utils.DbOperationHelper.IDbOperationHelper
import com.example.babajidemustapha.survey.shared.views.ReactionsViewGroup
import org.json.JSONArray
import org.json.JSONException

class ResponseDetail : AppCompatActivity() {
    var id = 0
    var rootView: LinearLayout? = null
    var db: SurveyDatabase? = null
    var questions: List<QuestionAndResponse?>? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_response_detail)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        val bundle = intent.extras
        id = bundle.getInt("ID")
        Log.e("id", id.toString() + "")
        rootView = findViewById(R.id.responseRootView)
        db = SurveyDatabase.Companion.getInstance(this)
        DbOperationHelper.Companion.execute(object : IDbOperationHelper<List<QuestionAndResponse?>?> {
            override fun run(): List<QuestionAndResponse?>? {
                return db!!.responseDetailDao().getResponseDetailsWithRespectiveQuestions(id)
            }

            override fun onCompleted(questionAndResponses: List<QuestionAndResponse?>?) {
                questions = questionAndResponses
                buildQuestions(questions, rootView)
            }
        })
    }

    fun buildQuestion(question: QuestionAndResponse?): View {
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
        if (question!!.isMandatory) {
            val star = SpannableString("*" + question.questionNo + ".")
            star.setSpan(ForegroundColorSpan(Color.RED), 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            q_No.text = star
        } else {
            q_No.text = "${question.questionNo}."
        }
        q_No.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20f)
        q_No.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        q_text.text = question.questionText
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
        when (question.questionType) {
            QuestionType.SHORT_TEXT, QuestionType.LONG_TEXT -> {
                val ans = TextView(this)
                ans.tag = question.questionNo
                ans.text = "Answer: " + question.response
                ll.addView(ans)
            }
            QuestionType.SINGLE_OPTION -> {
                val radioGroup = RadioGroup(this)
                radioGroup.tag = "rg" + question.questionNo
                val options: List<String>? = question.options
                var i = 0
                while (i < question.optionCount) {
                    val opt = RadioButton(this)
                    opt.id = View.generateViewId()
                    opt.text = options!![i]
                    if (options[i].equals(question.response, ignoreCase = true)) {
                        opt.isChecked = true
                    }
                    opt.isEnabled = false
                    radioGroup.addView(opt)
                    i++
                }
                ll.addView(radioGroup)
            }
            QuestionType.REACTIONS -> {
                val reactionsViewGroup = ReactionsViewGroup(this)
                reactionsViewGroup.tag = "rg" + question.questionNo
                if (!question.response.isNullOrBlank()) {
                    reactionsViewGroup.check(Reactions.valueOf(question.response!!).getReactionViewId())
                }
                reactionsViewGroup.disableAllButtons()
                ll.addView(reactionsViewGroup)
            }
            QuestionType.MULTIPLE_OPTION -> {
                val llcb = LinearLayout(this)
                llcb.orientation = LinearLayout.VERTICAL
                llcb.layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                val questionOptions: List<String>? = question.options
                var multiResponse: JSONArray? = null
                // Log.e("my response",question.getResponse());
                try {
                    multiResponse = JSONArray(question.response)
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
                var i = 0
                while (i < question.optionCount) {
                    val cb = CheckBox(this)
                    cb.text = questionOptions!![i]
                    cb.tag = "qu" + question.questionNo + "op" + i
                    if (multiResponse != null) {
                        var j = 0
                        while (j < multiResponse.length()) {
                            try {
                                if (multiResponse.getString(j).equals(questionOptions[i], ignoreCase = true)) {
                                    cb.isChecked = true
                                    break
                                }
                            } catch (e: JSONException) {
                                e.printStackTrace()
                            }
                            j++
                        }
                    }
                    cb.isEnabled = false
                    llcb.addView(cb)
                    i++
                }
                ll.addView(llcb)
            }
        }
        cardview.addView(ll)
        return cardview
    }

    fun buildQuestions(questions: List<QuestionAndResponse?>?, view: ViewGroup?) {
        view!!.clipToPadding = false
        for (i in questions!!.indices) {
            view.addView(buildQuestion(questions[i]))
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