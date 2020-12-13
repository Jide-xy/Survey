package com.example.babajidemustapha.survey.features.takesurvey.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.babajidemustapha.survey.R
import java.util.*

/**
 * A simple [Fragment] subclass.
 */
class SurveyDescription : Fragment() {
    var survey_id = 0
    var name: String? = null
    var quesNo = 0
    var desc: String? = null
    var online = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.content_survey_description, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val bundle = activity!!.intent.extras
        val txtName = view.findViewById<TextView>(R.id.name)
        val txtDesc = view.findViewById<TextView>(R.id.desc)
        val txtQues = view.findViewById<TextView>(R.id.ques)
        survey_id = bundle.getInt("ID")
        online = bundle.getBoolean("online")
        name = bundle.getString("name")
        quesNo = bundle.getInt("quesNo")
        desc = bundle.getString("Description")
        txtName.text = name
        txtDesc.text = desc
        txtQues.text = String.format(Locale.ENGLISH, "%d %s", quesNo, if (quesNo > 1) "questions" else "question")
        if (online) {
            val username = view.findViewById<TextView>(R.id.username)
            username.text = "Survey By: @" + bundle.getString("Username")
            username.visibility = View.VISIBLE
        }
    }
}