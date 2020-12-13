package com.example.babajidemustapha.survey.features.searchsurvey.fragments

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.format.DateFormat
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.babajidemustapha.survey.R
import com.example.babajidemustapha.survey.features.dashboard.fragments.SurveyList.OnNavigationMenuSelected
import com.example.babajidemustapha.survey.features.takesurvey.activities.SurveyAction
import com.example.babajidemustapha.survey.shared.models.QuestionType
import com.example.babajidemustapha.survey.shared.room.entities.Question
import com.example.babajidemustapha.survey.shared.room.entities.Survey
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.util.*

/**
 * A simple [Fragment] subclass.
 */
class SearchSurvey : Fragment() {
    var requestQueue: RequestQueue? = null
    var editText: EditText? = null
    var preferences: SharedPreferences? = null
    var recyclerView: RecyclerView? = null
    var progressBar: ProgressBar? = null
    var btn: Button? = null
    var token: String? = null
    var mListener: OnNavigationMenuSelected? = null
    override fun onAttach(context: Context) {
        super.onAttach(context)
        mListener = if (context is OnNavigationMenuSelected) {
            context
        } else {
            throw RuntimeException(context.toString()
                    + " must implement OnNavigationMenuSelected")
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mListener!!.setTitle("Search Online Surveys")
        recyclerView = view.findViewById(R.id.online_surveys)
        progressBar = view.findViewById(R.id.search_progress)
        requestQueue = Volley.newRequestQueue(context)
        preferences = activity!!.getSharedPreferences("user_data", Context.MODE_PRIVATE)
        editText = view.findViewById(R.id.search_param)
        btn = view.findViewById(R.id.online_search)
        btn?.setOnClickListener(View.OnClickListener {
            btn?.isEnabled = false
            progressBar?.visibility = View.VISIBLE
            searchSurvey()
        })
        recyclerView?.layoutManager = LinearLayoutManager(context)
        // recyclerView.setAdapter(new CustomAdapter1(db.getMySurveys()));
        recyclerView?.addItemDecoration(DividerItemDecoration(context, RecyclerView.VERTICAL))
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_search_survey, container, false)
    }

    @Throws(JSONException::class)
    private fun buildSurvey(`object`: JSONObject): List<Survey> {
        val result: MutableList<Survey> = ArrayList()
        for (i in 0 until `object`.getJSONArray("SURVEYS").length()) {
            val object1 = `object`.getJSONArray("SURVEYS").getJSONObject(i)
            val survey = Survey(object1.getInt("ID"), object1.getString("SURVEY_NAME"), object1.getLong("DATE_CREATED"), object1.getString("DESCRIPTION"),
                    object1.getString("USERNAME"), object1.getString("DEVICE_TOKEN"))
            var ques_no = 0
            for (j in 0 until `object`.getJSONArray("QUESTIONS").length()) {
                if (`object`.getJSONArray("QUESTIONS").getJSONObject(j).getInt("SURVEY_ID") == object1.getInt("ID")) {
                    ques_no++
                    val object2 = `object`.getJSONArray("QUESTIONS").getJSONObject(j)
                    val question = Question(object2.getInt("QUESTION_ID"), object2.getInt("QUESTION_NO"), QuestionType.valueOf(object2.getString("QUESTION_TYPE")), object2.getInt("SURVEY_ID"),
                            JSONArray(object2.getString("QUESTION_OPTIONS")), object2.getBoolean("MANDATORY"), object2.getString("QUESTION_TEXT"))
                    //survey.addQuestion(question);
                }
            }
            survey.setNoOFQues(ques_no)
            result.add(survey)
        }
        return result
    }

    private fun searchSurvey() {
        val query = editText!!.text.toString()
        if (query.isEmpty()) {
        } else {
            val `object` = JSONObject()
            try {
                // preferences.getInt("USER_ID",0)
                `object`.put("USER_ID", preferences!!.getInt("USER_ID", 0))
                `object`.put("SEARCH_PARAM", query)
            } catch (e: JSONException) {
                e.printStackTrace()
                return
            }
            val request = JsonObjectRequest(Request.Method.POST, "http://survhey.azurewebsites.net/survey/search", `object`, { response ->
                Log.e("ddd", response.toString())
                progressBar!!.visibility = View.GONE
                btn!!.isEnabled = true
                try {
                    if (response.getString("STATUS").equals("success", ignoreCase = true)) {
                        recyclerView!!.adapter = CustomAdapter1(buildSurvey(response).toMutableList())
                    } else {
                        Toast.makeText(context, "An error occured", Toast.LENGTH_SHORT).show()
                        Log.e("ddd", response.toString())
                    }
                } catch (e: JSONException) {
                    // Toast.makeText(LoginActivity.this,"An error occured",Toast.LENGTH_SHORT).show();
                    e.printStackTrace()
                }
            }) { error ->
                progressBar!!.visibility = View.GONE
                btn!!.isEnabled = true
                Toast.makeText(context, "Failed to connect", Toast.LENGTH_SHORT).show()
                error.printStackTrace()
            }
            requestQueue!!.add(request)
        }
    }

    private inner class CustomAdapter1(var source: MutableList<Survey>) : RecyclerView.Adapter<CustomAdapter1.ViewHolder>() {
        private fun add(survey: Survey) {
            source.add(survey)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            return ViewHolder(activity!!.layoutInflater.inflate(R.layout.survey_item_layout, parent, false))
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.name.text = if (source[position].name!!.length > 30) source[position].name!!.substring(0, 30) + "..." else source[position].name
            holder.desc.text = if (source[position].desc!!.length > 40) source[position].desc!!.substring(0, 40) + "..." else source[position].desc
            holder.date.text = DateFormat.format("yyyy-MM-dd", source[position].date)

//            holder.no_of_ques.setText(source.get(position).getNoOfQues()+" question(s)");
            holder.privacy.text = if (source[position].isPrivacy) "Public" else "Private"
            holder.privacy.visibility = View.GONE
        }

        override fun getItemCount(): Int {
            return source.size
        }

        inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
            var name: TextView
            var desc: TextView
            var date: TextView

            //          TextView no_of_ques;
            var privacy: TextView
            override fun onClick(view: View) {
                val intent = Intent(activity, SurveyAction::class.java)
                intent.putExtra("online", true)
                //intent.putExtra("questions", (Serializable) source.get(getAdapterPosition()).getQuestions());
                intent.putExtra("name", source[adapterPosition].name)
                intent.putExtra("ID", source[adapterPosition].id)
                intent.putExtra("Description", source[adapterPosition].desc)
                intent.putExtra("Username", source[adapterPosition].username)
                intent.putExtra("device_token", source[adapterPosition].deviceToken)
                intent.putExtra("quesNo", source[adapterPosition].noOfQues)
                startActivity(intent)
            }

            init {
                name = itemView.findViewById(R.id.name)
                desc = itemView.findViewById(R.id.desc)
                date = itemView.findViewById(R.id.date)
                //     no_of_ques = (TextView) itemView.findViewById(R.id.no_of_questions);
                privacy = itemView.findViewById(R.id.privacy)
                itemView.setOnClickListener(this)
            }
        }
    }
}