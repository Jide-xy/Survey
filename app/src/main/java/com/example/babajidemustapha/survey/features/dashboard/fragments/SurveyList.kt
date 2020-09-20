package com.example.babajidemustapha.survey.features.dashboard.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.babajidemustapha.survey.R
import com.example.babajidemustapha.survey.features.Reports.activities.SurveyReportActivity
import com.example.babajidemustapha.survey.features.dashboard.activities.DashboardActivity
import com.example.babajidemustapha.survey.features.dashboard.adapters.SurveyAdapter
import com.example.babajidemustapha.survey.features.dashboard.adapters.SurveyAdapter.SurveyActionListener
import com.example.babajidemustapha.survey.features.dashboard.fragments.SurveyList
import com.example.babajidemustapha.survey.features.dashboard.fragments.SurveyList.OnNavigationMenuSelected
import com.example.babajidemustapha.survey.features.newsurvey.activities.QuestionsSetupActivity
import com.example.babajidemustapha.survey.features.takesurvey.activities.SurveyAction
import com.example.babajidemustapha.survey.shared.room.db.SurveyDatabase
import com.example.babajidemustapha.survey.shared.room.entities.Survey
import com.example.babajidemustapha.survey.shared.room.entities.SurveyWithResponseHeader
import com.example.babajidemustapha.survey.shared.utils.DbOperationHelper
import com.example.babajidemustapha.survey.shared.utils.DbOperationHelper.IDbOperationHelper
import com.google.android.material.floatingactionbutton.FloatingActionButton

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [OnNavigationMenuSelected] interface
 * to handle interaction events.
 * Use the [SurveyList.newInstance] factory method to
 * create an instance of this fragment.
 */
class SurveyList : Fragment(), SurveyActionListener {
    private var db: SurveyDatabase? = null
    private lateinit var adapter1: SurveyAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var placeholder: CardView
    private var surveys: List<SurveyWithResponseHeader> = emptyList()
    private lateinit var fab: FloatingActionButton
    private var mListener: OnNavigationMenuSelected? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_survey_list, container, false)
    }

    override fun onResume() {
        super.onResume()
        loadSurveys()
    }

    fun loadSurveys() {
        DbOperationHelper.execute<List<SurveyWithResponseHeader>>(object : IDbOperationHelper<List<SurveyWithResponseHeader>> {
            override fun run(): List<SurveyWithResponseHeader> {
                return db!!.surveyDao().allSurveysWithResponse
            }

            override fun onCompleted(surveys: List<SurveyWithResponseHeader>) {
                this@SurveyList.surveys = surveys
                if (this@SurveyList.surveys.isEmpty()) {
                    recyclerView.visibility = View.GONE
                    placeholder.visibility = View.VISIBLE
                    return
                }
                placeholder.visibility = View.GONE
                recyclerView.visibility = View.VISIBLE
                adapter1 = SurveyAdapter(this@SurveyList.surveys, this@SurveyList)
                recyclerView.adapter = adapter1
            }
        })
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mListener = if (context is OnNavigationMenuSelected) {
            context
        } else {
            throw RuntimeException(context.toString()
                    + " must implement OnNavigationMenuSelected")
        }
    }

    override fun onDetach() {
        super.onDetach()
        mListener = null
    }

    override fun takeSurvey(survey: Survey, isOnline: Boolean) {
        DbOperationHelper.execute<Int>(object : IDbOperationHelper<Int> {
            override fun run(): Int {
                return db!!.questionDao().getSurveyQuestionCount(survey.id)
            }

            override fun onCompleted(id: Int) {
                val intent = Intent(activity, SurveyAction::class.java)
                intent.putExtra("name", survey.name)
                intent.putExtra("ID", survey.id)
                intent.putExtra("Description", survey.desc)
                intent.putExtra("quesNo", id)
                intent.putExtra("online", isOnline)
                startActivity(intent)
            }
        })
    }

    override fun viewReport(survey: Survey) {
        val intent = Intent(activity, SurveyReportActivity::class.java)
        intent.putExtra("ID", survey.id)
        intent.putExtra("name", survey.name)
        startActivity(intent)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        db = SurveyDatabase.getInstance(context)
        mListener!!.setTitle("My Surveys")
        recyclerView = view.findViewById(R.id.surveyList)
        placeholder = view.findViewById(R.id.emptyPlaceholder)
        fab = view.findViewById(R.id.fab)
        recyclerView.layoutManager = LinearLayoutManager(context)
        fab.setOnClickListener {
            val intent = Intent(activity, QuestionsSetupActivity::class.java)
            startActivity(intent)
        }
        placeholder.setOnClickListener {
            val intent = Intent(activity, QuestionsSetupActivity::class.java)
            startActivity(intent)
        }
        //loadSurveys();
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.main, menu)
        //        SearchView searchView =
//                (SearchView) menu.findItem(R.id.search).getActionView();
        val item = menu.findItem(R.id.search)
        val sv = SearchView((activity as DashboardActivity?)!!.supportActionBar!!.themedContext)
        item.setShowAsAction(MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW or MenuItem.SHOW_AS_ACTION_IF_ROOM)
        item.actionView = sv
//        sv.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
//            override fun onQueryTextSubmit(query: String): Boolean {
//                if (!query.isEmpty()) {
//                    DbOperationHelper.execute<List<Survey>>(object : IDbOperationHelper<List<Survey>> {
//                        override fun run(): List<Survey> {
//                            return db!!.surveyDao().searchSurveys(query)
//                        }
//
//                        override fun onCompleted(surveys: List<Survey>) {
//                            adapter1!!.changeSource(surveys)
//                        }
//                    })
//                    return true
//                }
//                return false
//            }
//
//            override fun onQueryTextChange(newText: String): Boolean {
//                if (!newText.isEmpty()) {
//                    DbOperationHelper.execute<List<Survey>>(object : IDbOperationHelper<List<Survey>> {
//                        override fun run(): List<Survey> {
//                            return db!!.surveyDao().searchSurveys(newText)
//                        }
//
//                        override fun onCompleted(surveys: List<Survey>) {
//                            adapter1!!.changeSource(surveys)
//                        }
//                    })
//                    return true
//                }
//                return false
//            }
//        })
        super.onCreateOptionsMenu(menu, inflater)
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     *
     *
     * See the Android Training lesson [Communicating with Other Fragments](http://developer.android.com/training/basics/fragments/communicating.html) for more information.
     */
    interface OnNavigationMenuSelected {
        fun setTitle(title: String?)
    }

    companion object {
        val TAG = SurveyList::class.java.simpleName

        @JvmStatic
        fun newInstance(): SurveyList {
            return SurveyList()
        }
    }
}