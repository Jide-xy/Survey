package com.example.babajidemustapha.survey.features.newsurvey.fragments


import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.babajidemustapha.survey.R
import com.example.babajidemustapha.survey.features.newsurvey.fragments.QuestionsSetupFragment.OnSurveySetupInteractionListener
import com.example.babajidemustapha.survey.shared.room.entities.Survey
import kotlinx.android.synthetic.main.fragment_create_survey.*


/**
 * A simple [Fragment] subclass.
 * Use the [CreateSurveyFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class CreateSurveyFragment : Fragment() {

    private var listener: OnSurveySetupInteractionListener? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_create_survey, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity?.title = "Create Survey"
        go.setOnClickListener { v ->

            if (!name.text.toString().isEmpty() && !desc.text.toString().isEmpty()) {
                val survey = Survey()
                survey.name = name.text.toString()
                survey.desc = desc.text.toString()
                survey.isPrivacy = privacy.isChecked
                listener?.createSurvey(survey)
            } else {
                Toast.makeText(context, "1 or more fields empty", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnSurveySetupInteractionListener) {
            listener = context
        } else {
            throw RuntimeException("$context must implement OnSurveySetupInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    companion object {
        fun newInstance(): CreateSurveyFragment = CreateSurveyFragment()
    }


}// Required empty public constructor
