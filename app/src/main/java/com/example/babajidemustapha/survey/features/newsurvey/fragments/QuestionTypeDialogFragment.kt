package com.example.babajidemustapha.survey.features.newsurvey.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.babajidemustapha.survey.R
import com.example.babajidemustapha.survey.features.newsurvey.adapters.QuestionTypeAdapter
import com.example.babajidemustapha.survey.features.newsurvey.adapters.QuestionTypeAdapter.QuestionTypeInteractionListener
import com.example.babajidemustapha.survey.shared.models.QuestionType
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.dialog_question_type.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [QuestionTypeDialogFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [QuestionTypeDialogFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class QuestionTypeDialogFragment : BottomSheetDialogFragment(), QuestionTypeInteractionListener {

    override fun onSelectQuestionType(questionType: QuestionType) {
        listener?.onSelectQuestionType(questionType)
        dismiss()
    }

    var listener: QuestionTypeInteractionListener? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

//        dialog?.window?.setGravity(Gravity.BOTTOM)
        return inflater.inflate(R.layout.dialog_question_type, container, false)
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is QuestionTypeInteractionListener) {
            listener = context
        }
//        else {
//            throw RuntimeException("$context must implement QuestionTypeInteractionListener")
//        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        questionTypeList.adapter = QuestionTypeAdapter(QuestionType.values().toList(), this)
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @return A new instance of fragment QuestionTypeDialogFragment.
         */
        @JvmStatic
        fun newInstance() =
                QuestionTypeDialogFragment()

        val TAG = QuestionTypeDialogFragment::class.java.simpleName
    }
}
