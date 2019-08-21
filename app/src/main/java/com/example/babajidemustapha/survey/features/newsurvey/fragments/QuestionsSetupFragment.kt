package com.example.babajidemustapha.survey.features.newsurvey.fragments

import android.content.Context
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment

import com.example.babajidemustapha.survey.R
import com.example.babajidemustapha.survey.features.newsurvey.adapters.QuestionListAdapter
import com.example.babajidemustapha.survey.features.newsurvey.adapters.QuestionTypeAdapter
import com.example.babajidemustapha.survey.shared.models.QuestionType
import com.example.babajidemustapha.survey.shared.room.db.SurveyDatabase
import com.example.babajidemustapha.survey.shared.room.entities.Question
import com.example.babajidemustapha.survey.shared.room.entities.Survey
import com.example.babajidemustapha.survey.shared.utils.DbOperationHelper
import kotlinx.android.synthetic.main.fragment_questions_setup.*
import java.util.*


/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [QuestionsSetupFragment.OnSurveySetupInteractionListener] interface
 * to handle interaction events.
 * Use the [QuestionsSetupFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class QuestionsSetupFragment : Fragment(), QuestionTypeAdapter.QuestionTypeInteractionListener, QuestionListAdapter.QuestionsSetupInteractionListener {


    private var listener: OnSurveySetupInteractionListener? = null

    lateinit var db: SurveyDatabase
    lateinit var adapter: QuestionListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.fragment_questions_setup, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        db = SurveyDatabase.getInstance(context)
        adapter = QuestionListAdapter(this)
        questionsRecyclerView.adapter = adapter
        fab.setOnClickListener {
            getQuestionType()
        }
        emptyLayout.setOnClickListener {
            getQuestionType()
        }
    }

    private fun getQuestionType() {
        val questionTypeDialogFragment = QuestionTypeDialogFragment.newInstance()
        questionTypeDialogFragment.show(childFragmentManager, QuestionTypeDialogFragment.TAG)
        questionTypeDialogFragment.listener = this
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnSurveySetupInteractionListener) {
            listener = context
        }
//        else {
//            throw RuntimeException("$context must implement OnSurveySetupInteractionListener")
//        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    override fun onSelectQuestionType(questionType: QuestionType) {
        adapter.addQuestion(questionType)
    }

    override fun onSubmit(isSuccessful: Boolean, questions: List<Question>?, index: Int?, message: String?) {
        if (isSuccessful) {
            val survey = listener?.getSurvey() ?: return
            survey.date = Calendar.getInstance().timeInMillis
            DbOperationHelper.execute(object : DbOperationHelper.IDbOperationHelper<Unit> {
                override fun run() {
                    db.surveyDao().createSurveyWithQuestions(survey, questions)
                    return
                }

                override fun onCompleted(unit: Unit) {
                    Toast.makeText(context, "Your Survey has been recorded", Toast.LENGTH_SHORT).show()
                    activity?.finish()
                }

            })
        } else {
            Toast.makeText(context, message ?: "An error occured", Toast.LENGTH_LONG).show()
            if (index != null) {
                questionsRecyclerView.scrollToPosition(index)
            }
        }
    }

    override fun isEmpty(isEmpty: Boolean) {
        if (isEmpty) {
            questionsRecyclerView.visibility = View.GONE
            emptyLayout.visibility = View.VISIBLE
        } else {
            emptyLayout.visibility = View.GONE
            questionsRecyclerView.visibility = View.VISIBLE
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, menuInflater: MenuInflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        super.onCreateOptionsMenu(menu, menuInflater)
        menuInflater.inflate(R.menu.question_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.save) {
            adapter.validateList()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    interface OnSurveySetupInteractionListener {
        fun getSurvey(): Survey
        fun createSurvey(survey: Survey)
    }

    companion object {
        @JvmStatic
        fun newInstance() =
                QuestionsSetupFragment()

        val TAG = this::class.java.simpleName
    }
}
