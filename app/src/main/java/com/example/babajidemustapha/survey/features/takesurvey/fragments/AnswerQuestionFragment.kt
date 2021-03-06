package com.example.babajidemustapha.survey.features.takesurvey.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.babajidemustapha.survey.R
import com.example.babajidemustapha.survey.shared.models.Reactions
import com.example.babajidemustapha.survey.shared.views.QuestionView
import com.jide.surveyapp.model.Option
import com.jide.surveyapp.model.ResponseDetail
import com.jide.surveyapp.model.relations.QuestionWithOptionsAndResponse
import kotlinx.android.synthetic.main.fragment_answer_question.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"


class AnswerQuestionFragment : Fragment(), QuestionView.OnResponseProvidedListener {
    override fun onReactionSelectResponse(reaction: Reactions) {
        val question = listener?.onQuestionSelected(questionIndex)
        question?.responses = listOf(ResponseDetail(question!!.options.first().id, reaction.name))
        listener?.updateProgress()
    }

    override fun onTextResponse(response: String) {
        val question = listener?.onQuestionSelected(questionIndex)
        question?.responses = listOf(ResponseDetail(question!!.options.first().id, response))
        listener?.updateProgress()
    }

    override fun onSingleOptionsResponse(response: Int) {
        val question = listener?.onQuestionSelected(questionIndex)
        question?.responses = listOf(ResponseDetail(question!!.options[response].id))
        listener?.updateProgress()
    }

    override fun onMultiSelectResponse(response: List<Int>) {
        val question = listener?.onQuestionSelected(questionIndex)
        val responseTextList = mutableListOf<Option>()
        for (res in response) {
            responseTextList.add(question!!.options[res])
        }
        question?.responses = responseTextList.map { ResponseDetail(it.id) }
        listener?.updateProgress()
    }

    private var questionIndex: Int = -1
    private var listener: OnQuestionSelectedInteractionListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            questionIndex = it.getInt(ARG_PARAM1)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_answer_question, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val questionView = QuestionView(requireContext(), QuestionView.VIEW_QUESTION, listener!!.onQuestionSelected(questionIndex), listener!!.getTotal(), this)
        rootView.addView(questionView)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnQuestionSelectedInteractionListener) {
            listener = context
        } else {
            throw RuntimeException("$context must implement OnQuestionSelectedInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    interface OnQuestionSelectedInteractionListener {
        fun onQuestionSelected(index: Int): QuestionWithOptionsAndResponse
        fun getTotal(): Int
        fun updateProgress()
    }

    companion object {
        @JvmStatic
        fun newInstance(questionIndex: Int) =
                AnswerQuestionFragment().apply {
                    arguments = Bundle().apply {
                        putInt(ARG_PARAM1, questionIndex)
                    }
                }
    }
}
