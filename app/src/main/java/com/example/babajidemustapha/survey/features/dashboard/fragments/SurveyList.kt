package com.example.babajidemustapha.survey.features.dashboard.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.cardview.widget.CardView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.babajidemustapha.survey.R
import com.example.babajidemustapha.survey.features.dashboard.activities.DashboardActivity
import com.example.babajidemustapha.survey.features.dashboard.adapters.SurveyAdapter
import com.example.babajidemustapha.survey.features.dashboard.adapters.SurveyAdapter.SurveyActionListener
import com.example.babajidemustapha.survey.features.dashboard.viewmodel.SurveyListViewModel
import com.example.babajidemustapha.survey.features.newsurvey.activities.QuestionsSetupActivity
import com.example.babajidemustapha.survey.features.reports.activities.SurveyReportActivity
import com.example.babajidemustapha.survey.features.takesurvey.activities.SurveyAction
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.jide.surveyapp.model.Survey
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect


@AndroidEntryPoint
class SurveyList : Fragment(), SurveyActionListener {
    private lateinit var adapter1: SurveyAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var placeholder: CardView
    private var surveys = emptyList<Survey>()
    private lateinit var fab: FloatingActionButton
    private var mListener: OnNavigationMenuSelected? = null

    private val viewModel by viewModels<SurveyListViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_survey_list, container, false)
    }

    fun loadSurveys() {
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.surveyFlow.collect {
                surveys = it
                recyclerView.isVisible = surveys.isEmpty().not()
                placeholder.isVisible = surveys.isEmpty()
                adapter1 = SurveyAdapter(surveys, this@SurveyList)
                recyclerView.adapter = adapter1
            }
        }
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
        val intent = Intent(activity, SurveyAction::class.java)
        intent.putExtra("name", survey.name)
        intent.putExtra("ID", survey.id)
        intent.putExtra("Description", survey.description)
//        intent.putExtra("quesNo", id)
        intent.putExtra("online", isOnline)
        startActivity(intent)
    }

    override fun viewReport(survey: Survey) {
        val intent = Intent(activity, SurveyReportActivity::class.java)
        intent.putExtra("ID", survey.id)
        intent.putExtra("name", survey.name)
        startActivity(intent)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
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
        loadSurveys()
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
        sv.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                viewModel.searchSurvey(query)
                return true
            }

            override fun onQueryTextChange(newText: String): Boolean {
                viewModel.searchSurvey(newText)
                return true
            }
        })
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