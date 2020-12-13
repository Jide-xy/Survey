package com.example.babajidemustapha.survey.features.takesurvey.fragments

import android.app.ProgressDialog
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.util.TypedValue
import android.view.*
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.babajidemustapha.survey.R
import com.example.babajidemustapha.survey.shared.models.QuestionType
import com.example.babajidemustapha.survey.shared.room.db.SurveyDatabase
import com.example.babajidemustapha.survey.shared.room.entities.Question
import com.example.babajidemustapha.survey.shared.room.entities.ResponseDetail
import com.example.babajidemustapha.survey.shared.room.entities.ResponseHeader
import com.example.babajidemustapha.survey.shared.utils.DbOperationHelper
import com.example.babajidemustapha.survey.shared.utils.DbOperationHelper.IDbOperationHelper
import com.example.babajidemustapha.survey.shared.utils.Utils
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.util.*

/**
 * A simple [Fragment] subclass.
 */
class AnswerSurvey : Fragment() {
    var db: SurveyDatabase? = null
    var rootView: LinearLayout? = null
    var questions: List<Question?>? = null
    var btn: Button? = null
    var name: EditText? = null
    var survey_id = 0
    var survey_name: String = ""
    var online = false
    var requestQueue: RequestQueue? = null
    var device_token: String? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.content_take_survey, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setHasOptionsMenu(true)
        val bundle = activity!!.intent.extras
        survey_id = bundle.getInt("ID")
        survey_name = bundle.getString("name")
        online = bundle.getBoolean("online")
        requestQueue = Volley.newRequestQueue(context)
        Log.e("id", survey_id.toString() + "")
        rootView = view.findViewById(R.id.rootView)
        name = view.findViewById(R.id.respondentName)
        // btn= (Button) view.findViewById(R.id.submitResponse);
//        btn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                v.setEnabled(false);
////                if(name.getText().toString().isEmpty()){
////                    Toast.makeText(getContext(),"Please enter your name",Toast.LENGTH_SHORT).show();
////                }
////                else {
//                    if (validate(questions)) {
//                        if(!online) {
//                            submit();
//                            Toast.makeText(getContext(), "Your response has been recorded", Toast.LENGTH_SHORT).show();
//                            getActivity().finish();
//                        }
//                        else{
//                            saveOnlineResponse(questions);
//                        }
//                    } else {
//                        Toast.makeText(getContext(), "1 or more mandatory questions have not been answered", Toast.LENGTH_SHORT).show();
//                    }
//             //   }
//                v.setEnabled(true);
//            }
//        });
        if (!online) {
            db = SurveyDatabase.Companion.getInstance(context)
            DbOperationHelper.Companion.execute(object : IDbOperationHelper<List<Question?>?> {
                override fun run(): List<Question?>? {
                    return db!!.surveyDao().getQuestionsForSurveyWithOfflineId(survey_id.toLong())
                }

                override fun onCompleted(questions: List<Question?>?) {
                    this@AnswerSurvey.questions = questions
                    buildQuestions(this@AnswerSurvey.questions, rootView)
                }
            })
            activity!!.title = if (survey_name.length <= 10) survey_name else survey_name.substring(0, 7) + "..."
        } else {
            device_token = bundle.getString("device_token")
            questions = bundle.getSerializable("questions") as ArrayList<Question?>
            activity!!.title = if (survey_name.length <= 10) survey_name else survey_name.substring(0, 7) + "..."
            buildQuestions(questions, rootView)
        }
        super.onViewCreated(view, savedInstanceState)
    }

    fun buildQuestions(questions: List<Question?>?, view: ViewGroup?) {
        for (i in questions!!.indices) {
            view!!.addView(buildQuestion(questions[i]))
        }
    }

    fun submit() {
        storeResponse(questions)
    }

    fun validate(questions: List<Question?>?): Boolean {
        for (i in questions!!.indices) {
            if (questions[i]!!.isMandatory) {
                when (questions[i]!!.questionType) {
                    QuestionType.SHORT_TEXT -> {
                        val ans = rootView!!.findViewWithTag<EditText>(questions[i]!!.questionNo)
                        if (ans.text.toString().isEmpty()) {
                            return false
                        }
                    }
                    QuestionType.SINGLE_OPTION -> {
                        val radioGroup = rootView!!.findViewWithTag<RadioGroup>("rg" + questions[i]!!.questionNo)
                        if (radioGroup.checkedRadioButtonId == -1) {
                            return false
                        }
                    }
                    QuestionType.MULTIPLE_OPTION -> {
                        val question = questions[i]
                        var j = 0
                        while (j < question!!.optionCount) {
                            val cb = rootView!!.findViewWithTag<CheckBox>("qu" + question.questionNo + "op" + j)
                            if (cb.isChecked) {
                                return true
                            }
                            j++
                        }
                        return false
                    }
                }
            }
        }
        return true
    }

    fun buildResponseDetail(questions: List<Question?>?): List<ResponseDetail?> {
        val responseDetails: MutableList<ResponseDetail?> = ArrayList()
        for (i in questions!!.indices) {
            val question = questions[i]
            when (question!!.questionType) {
                QuestionType.SHORT_TEXT -> {
                    val ans = rootView!!.findViewWithTag<EditText>(question.questionNo)
                    if (!ans.text.toString().isEmpty()) {
                        // question.setAnswer(ans.getText().toString());
                        responseDetails.add(ResponseDetail(question.id, ans.text.toString()))
                    } else {
                        responseDetails.add(ResponseDetail(question.id, ""))
                    }
                }
                QuestionType.SINGLE_OPTION -> {
                    val radioGroup = rootView!!.findViewWithTag<RadioGroup>("rg" + question.questionNo)
                    if (radioGroup.checkedRadioButtonId != -1) {
                        val radioButton = rootView!!.findViewById<RadioButton>(radioGroup.checkedRadioButtonId)
                        responseDetails.add(ResponseDetail(question.id, radioButton.text.toString()))
                        // question.setAnswer(radioButton.getText().toString());
                    } else {
                        responseDetails.add(ResponseDetail(question.id, ""))
                    }
                }
                QuestionType.MULTIPLE_OPTION -> {
                    val cbList: MutableList<String> = ArrayList()
                    var j = 0
                    while (j < question.optionCount) {
                        val cb = rootView!!.findViewWithTag<CheckBox>("qu" + question.questionNo + "op" + j)
                        if (cb.isChecked) {
                            cbList.add(cb.text.toString())
                        }
                        j++
                    }
                    if (!cbList.isEmpty()) {
                        responseDetails.add(ResponseDetail(question.id, cbList.toString()))
                    } else {
                        responseDetails.add(ResponseDetail(question.id, ""))
                    }
                }
            }
        }
        return responseDetails
    }

    fun buildResponseHeader(): ResponseHeader {
        return ResponseHeader(survey_id, name!!.text.toString(), Calendar.getInstance().timeInMillis)
    }

    fun storeResponse(questions: List<Question?>?) {
        db!!.responseHeaderDao().saveResponse(buildResponseHeader(), buildResponseDetail(questions))
    }

    fun saveOnlineResponse(questions: List<Question?>?) {
        val progressDialog = ProgressDialog(context)
        progressDialog.setMessage("Saving Response. Please wait...")
        progressDialog.isIndeterminate = true
        progressDialog.setCancelable(false)
        progressDialog.show()
        val `object` = JSONObject()
        try {
            `object`.put("RESPONSE_HEADER", buildResponseHeaderJSON(buildResponseHeader()))
            `object`.put("RESPONSE_DETAIL", buildResponseDetailJSON(buildResponseDetail(questions)))
            `object`.put("DEVICE_TOKEN", device_token)
            val request = JsonObjectRequest(Request.Method.POST, "http://survhey.azurewebsites.net/survey/record_response", `object`, { response ->
                Log.e("ddd", response.toString())
                progressDialog.dismiss()
                btn!!.isEnabled = true
                try {
                    if (response.getString("STATUS").equals("success", ignoreCase = true)) {
                        Toast.makeText(context, "ResponseDetail Successfully Posted", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, "An error occured", Toast.LENGTH_SHORT).show()
                        Log.e("ddd", response.toString())
                    }
                } catch (e: JSONException) {
                    // Toast.makeText(LoginActivity.this,"An error occured",Toast.LENGTH_SHORT).show();
                    e.printStackTrace()
                }
            }) { error ->
                progressDialog.dismiss()
                btn!!.isEnabled = true
                Toast.makeText(context, "Failed to connect", Toast.LENGTH_SHORT).show()
                error.printStackTrace()
            }
            requestQueue!!.add(request)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    @Throws(JSONException::class)
    fun buildResponseHeaderJSON(responseHeader: ResponseHeader): JSONObject {
        val `object` = JSONObject()
        `object`.put("RESPONSE_ID", responseHeader.responseId)
        `object`.put("SURVEY_ID", responseHeader.surveyId)
        `object`.put("RESPONDENT_NAME", responseHeader.respondentName)
        `object`.put("RESPONSE_DATE", responseHeader.date)
        return `object`
    }

    @Throws(JSONException::class)
    fun buildResponseDetailJSON(responseDetails: List<ResponseDetail?>): JSONArray {
        val jsonArray = JSONArray()
        for (i in responseDetails.indices) {
            val responseDetail = responseDetails[i]
            val `object` = JSONObject()
            `object`.put("QUESTION_ID", responseDetail!!.questionId)
            `object`.put("RESPONSE", responseDetail.response)
            jsonArray.put(`object`)
        }
        return jsonArray
    }

    fun buildQuestion(question: Question?): View {
        val cardView = CardView(context!!)
        val layoutParams: FrameLayout.LayoutParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT)
        layoutParams.setMargins(0,
                Utils.pxToDp(resources, 2),
                0,
                Utils.pxToDp(resources, 2))
        cardView.layoutParams = layoutParams
        val ll = LinearLayout(context)
        ll.orientation = LinearLayout.VERTICAL
        ll.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        ll.setPadding(Utils.pxToDp(resources, 16),
                Utils.pxToDp(resources, 16),
                Utils.pxToDp(resources, 16),
                Utils.pxToDp(resources, 16))
        val q_text = TextView(context)
        val q_No = TextView(context)
        if (question!!.isMandatory) {
            val star = SpannableString("*" + question.questionNo + ".")
            star.setSpan(ForegroundColorSpan(-0x10000), 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            q_No.text = star
        } else {
            q_No.text = String.format(Locale.getDefault(), "%d.", question.questionNo)
        }
        q_No.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20f)
        q_No.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        q_text.text = question.questionText
        q_text.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20f)
        q_text.setPadding(Utils.pxToDp(resources, 4),
                Utils.pxToDp(resources, 4),
                Utils.pxToDp(resources, 4),
                Utils.pxToDp(resources, 4))
        q_text.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        val subll = LinearLayout(context)
        subll.orientation = LinearLayout.HORIZONTAL
        subll.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        subll.addView(q_No)
        subll.addView(q_text)
        //        if(question.isMandatory()){
//
//        }
        ll.addView(subll)
        when (question.questionType) {
            QuestionType.SHORT_TEXT -> {
                val ans = EditText(context)
                ans.hint = "Enter answer here"
                ans.tag = question.questionNo
                ll.addView(ans)
            }
            QuestionType.SINGLE_OPTION -> {
                val radioGroup = RadioGroup(context)
                radioGroup.tag = "rg" + question.questionNo
                val options: List<String>? = question.options
                var i = 0
                while (i < question.optionCount) {
                    val opt = RadioButton(context)
                    opt.id = View.generateViewId()
                    opt.text = options!![i]
                    radioGroup.addView(opt)
                    i++
                }
                ll.addView(radioGroup)
            }
            QuestionType.MULTIPLE_OPTION -> {
                val llcb = LinearLayout(context)
                llcb.orientation = LinearLayout.VERTICAL
                llcb.layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                val questionOptions: List<String>? = question.options
                var i = 0
                while (i < question.optionCount) {
                    val cb = CheckBox(context)
                    cb.text = questionOptions!![i]
                    cb.tag = "qu" + question.questionNo + "op" + i
                    llcb.addView(cb)
                    i++
                }
                ll.addView(llcb)
            }
        }
        cardView.addView(ll)
        return cardView
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.save_answers) {
            if (validate(questions)) {
                if (!online) {
                    DbOperationHelper.Companion.execute(object : IDbOperationHelper<Void?> {
                        override fun run(): Void? {
                            submit()
                            return null
                        }

                        override fun onCompleted(aVoid: Void?) {
                            Toast.makeText(context, "Your response has been recorded", Toast.LENGTH_SHORT).show()
                            activity!!.finish()
                        }
                    })
                } else {
                    saveOnlineResponse(questions)
                }
            } else {
                Toast.makeText(context, "1 or more mandatory questions have not been answered", Toast.LENGTH_SHORT).show()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.take_survey_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }
}