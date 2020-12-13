package com.example.babajidemustapha.survey.features.newsurvey.activities

import android.graphics.Color
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.TypedValue
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.*
import android.widget.AdapterView.OnItemSelectedListener
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.cardview.widget.CardView
import com.example.babajidemustapha.survey.R
import com.example.babajidemustapha.survey.shared.models.QuestionType
import com.example.babajidemustapha.survey.shared.room.db.SurveyDatabase
import com.example.babajidemustapha.survey.shared.room.entities.Question
import com.example.babajidemustapha.survey.shared.room.entities.Survey
import com.example.babajidemustapha.survey.shared.utils.DbOperationHelper
import com.example.babajidemustapha.survey.shared.utils.DbOperationHelper.IDbOperationHelper
import org.json.JSONArray
import org.json.JSONException
import java.util.*

class CreateQuestions : AppCompatActivity() {
    var addQuestion: Button? = null
    var addOption: Button? = null
    var submit: Button? = null
    var optionConfig: LinearLayout? = null
    var quesText: EditText? = null
    var optText: EditText? = null
    var quesType: Spinner? = null
    var mandatory: Switch? = null
    var questionsPreview: LinearLayout? = null
    var optionsPreview: LinearLayout? = null
    var i = 0
    var survey_privacy: Boolean? = null
    var survey_name: String? = null
    var survey_desc: String? = null
    var db: SurveyDatabase? = null
    var questions: MutableList<Question?>? = null
    var options: MutableList<String>? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_questions)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        db = SurveyDatabase.Companion.getInstance(this)
        val bundle = intent.extras
        survey_name = bundle.getString("survey name")
        survey_desc = bundle.getString("survey desc")
        survey_privacy = bundle.getBoolean("survey privacy")
        addQuestion = findViewById(R.id.addQuestion)
        addOption = findViewById(R.id.addOption)
        //  submit = (Button) findViewById(R.id.submit);
        optionConfig = findViewById(R.id.optionConfig)
        questionsPreview = findViewById(R.id.questionsPreview)
        optionsPreview = findViewById(R.id.optionsPreview)
        quesText = findViewById(R.id.questionText)
        optText = findViewById(R.id.optionText)
        quesType = findViewById(R.id.questionType)
        mandatory = findViewById(R.id.mandatory)
        questions = ArrayList()
        options = ArrayList()
        addQuestion.setOnClickListener(View.OnClickListener { tryAddQuestion() })
        addOption.setOnClickListener(View.OnClickListener {
            if (optText.getText().toString().isEmpty()) {
                Toast.makeText(this@CreateQuestions, "Option field cant be empty", Toast.LENGTH_SHORT).show()
            } else {
                options.add(optText.getText().toString())
                when (quesType.getSelectedItem().toString()) {
                    "SINGLE OPTION" -> {
                        val radioButton = RadioButton(this@CreateQuestions)
                        radioButton.text = optText.getText().toString()
                        radioButton.isEnabled = false
                        optionsPreview.addView(radioButton)
                        optText.setText("")
                    }
                    "MULTIPLE OPTIONS" -> {
                        val checkBox = CheckBox(this@CreateQuestions)
                        checkBox.text = optText.getText().toString()
                        checkBox.isEnabled = false
                        optionsPreview.addView(checkBox)
                        optText.setText("")
                    }
                }
            }
        })
        quesType.setOnItemSelectedListener(object : OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                when (parent.getItemAtPosition(position).toString()) {
                    "TEXT" -> {
                        options.clear()
                        optionsPreview.removeAllViews()
                        optionConfig.setVisibility(View.GONE)
                        quesText.setEnabled(true)
                        quesText.requestFocus()
                    }
                    "SINGLE OPTION" -> {
                        options.clear()
                        optionsPreview.removeAllViews()
                        optionConfig.setVisibility(View.VISIBLE)
                        quesText.setEnabled(true)
                        optText.requestFocus()
                    }
                    "MULTIPLE OPTIONS" -> {
                        options.clear()
                        optionsPreview.removeAllViews()
                        optionConfig.setVisibility(View.VISIBLE)
                        quesText.setEnabled(true)
                        optText.requestFocus()
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                quesText.setText("")
                optText.setText("")
                options.clear()
                optionsPreview.removeAllViews()
                optionConfig.setVisibility(View.GONE)
                quesText.setEnabled(false)
            }
        })
        //        submit.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if(questions.size() == 0){
//                    Toast.makeText(CreateQuestions.this,"You must add at least one question",Toast.LENGTH_SHORT).show();
//                }
//                else {
//                    Survey survey = new Survey();
//                    survey.setName(survey_name);
//                    survey.setDesc(survey_desc);
//                    survey.setUsername("");
//                    survey.setPrivacy(survey_privacy);
//                    survey.setDate(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
//                    db.createSurvey(survey,questions);
//                    Toast.makeText(CreateQuestions.this,"Your Survey has been recorded",Toast.LENGTH_SHORT).show();
//                    finish();
//                }
//            }
//        });
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
    }

    fun tryAddQuestion() {
        val question = Question()
        when (QuestionType.valueOf(quesType!!.selectedItem.toString())) {
            QuestionType.SHORT_TEXT -> if (quesText!!.text.toString().isEmpty()) {
                Toast.makeText(this, "Question field can't be empty", Toast.LENGTH_SHORT).show()
                quesText!!.requestFocus()
                return
            } else {
                i++
                question.questionText = quesText!!.text.toString()
                question.isMandatory = mandatory!!.isChecked
                question.questionType = QuestionType.SHORT_TEXT
                question.questionNo = questions!!.size + 1
            }
            QuestionType.SINGLE_OPTION -> if (quesText!!.text.toString().isEmpty()) {
                Toast.makeText(this, "Question field cant be empty", Toast.LENGTH_SHORT).show()
                quesText!!.requestFocus()
                return
            } else if (options!!.isEmpty()) {
                Toast.makeText(this, "At least one option must be added", Toast.LENGTH_SHORT).show()
                return
            } else {
                i++
                question.questionText = quesText!!.text.toString()
                question.isMandatory = mandatory!!.isChecked
                question.questionType = QuestionType.SINGLE_OPTION
                question.questionNo = questions!!.size + 1
                question.options = options
            }
            QuestionType.MULTIPLE_OPTION -> if (quesText!!.text.toString().isEmpty()) {
                Toast.makeText(this, "Question field cant be empty", Toast.LENGTH_SHORT).show()
                quesText!!.requestFocus()
                return
            } else if (options!!.isEmpty()) {
                Toast.makeText(this, "At least one option must be added", Toast.LENGTH_SHORT).show()
                return
            } else {
                i++
                question.questionText = quesText!!.text.toString()
                question.isMandatory = mandatory!!.isChecked
                question.questionType = QuestionType.MULTIPLE_OPTION
                question.questionNo = questions!!.size + 1
                question.options = options
            }
            else -> {
                Toast.makeText(this, "Please select option type", Toast.LENGTH_SHORT).show()
                return
            }
        }
        questions!!.add(question)
        questionsPreview!!.removeAllViews()
        buildQuestions(questions, questionsPreview)
        optionsPreview!!.removeAllViews()
        quesType!!.setSelection(0)
        options!!.clear()
        quesText!!.setText("")
        optText!!.setText("")
        quesText!!.requestFocus()
    }

    fun buildQuestion(question: Question?): View {
        val cardView = CardView(this)
        val layoutParams: FrameLayout.LayoutParams = FrameLayout.LayoutParams(CardView.LayoutParams.MATCH_PARENT, CardView.LayoutParams.WRAP_CONTENT)
        layoutParams.setMargins(0,
                TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2f, resources.displayMetrics).toInt(),
                0,
                TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2f, resources.displayMetrics).toInt())
        cardView.layoutParams = layoutParams
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
            q_No.setText(question.questionNo + ".")
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
            QuestionType.SHORT_TEXT -> {
                val ans = EditText(this)
                ans.isEnabled = false
                ll.addView(ans)
            }
            QuestionType.SINGLE_OPTION -> {
                val radioGroup = RadioGroup(this)
                radioGroup.tag = "rg" + question.questionNo
                try {
                    val options = JSONArray(question.options.toString())
                    run {
                        var i = 0
                        while (i < question.optionCount) {
                            val opt = RadioButton(this)
                            opt.id = View.generateViewId()
                            opt.text = options.getString(i)
                            opt.isEnabled = false
                            radioGroup.addView(opt)
                            i++
                        }
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
                ll.addView(radioGroup)
            }
            QuestionType.MULTIPLE_OPTION -> {
                val llcb = LinearLayout(this)
                llcb.orientation = LinearLayout.VERTICAL
                llcb.layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                try {
                    val questionOptions = JSONArray(question.options.toString())
                    run {
                        var i = 0
                        while (i < question.optionCount) {
                            val cb = CheckBox(this)
                            cb.text = questionOptions.getString(i)
                            cb.tag = "qu" + question.questionNo + "op" + i
                            cb.isEnabled = false
                            llcb.addView(cb)
                            i++
                        }
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
                ll.addView(llcb)
            }
        }
        cardView.addView(ll)
        return cardView
    }

    fun buildQuestions(questions: List<Question?>?, view: ViewGroup?) {
        for (i in questions!!.indices) {
            view!!.addView(buildQuestion(questions[i]))
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.question_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == R.id.save) {
            if (questions!!.size == 0) {
                Toast.makeText(this@CreateQuestions, "You must add at least one question", Toast.LENGTH_SHORT).show()
            } else {
                DbOperationHelper.Companion.execute(object : IDbOperationHelper<Void?> {
                    override fun run(): Void? {
                        val survey = Survey()
                        survey.name = survey_name
                        survey.desc = survey_desc
                        survey.username = ""
                        survey.isPrivacy = survey_privacy!!
                        //survey.setDate(new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date()));
                        db!!.surveyDao().createSurveyWithQuestions(survey, questions)
                        return null
                    }

                    override fun onCompleted(aVoid: Void?) {
                        Toast.makeText(this@CreateQuestions, "Your Survey has been recorded", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                })
            }
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}