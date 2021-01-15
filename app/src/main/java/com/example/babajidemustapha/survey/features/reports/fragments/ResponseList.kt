package com.example.babajidemustapha.survey.features.reports.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.babajidemustapha.survey.R
import com.example.babajidemustapha.survey.features.reports.activities.ResponseDetail
import com.example.babajidemustapha.survey.features.reports.adapters.ResponseAdapter
import com.example.babajidemustapha.survey.features.reports.adapters.ResponseAdapter.OnResponseSelectedListener
import com.example.babajidemustapha.survey.features.reports.viewmodel.ResponseListViewModel
import com.jide.surveyapp.model.ResponseHeader
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect


@AndroidEntryPoint
class ResponseList : Fragment(), OnResponseSelectedListener {
    lateinit var recyclerView: RecyclerView
    private lateinit var survey_id: String
    private lateinit var placeholder: TextView
    var adapter1: ResponseAdapter? = null

    private val viewModel by viewModels<ResponseListViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.content_response_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val bundle = requireActivity().intent.extras
        survey_id = bundle.getString("ID")
        //Log.e("survey_id", survey_id+"");
        recyclerView = view.findViewById(R.id.responseList)
        placeholder = view.findViewById(R.id.emptyPlaceholder)
        recyclerView.layoutManager = LinearLayoutManager(context)
        loadResponses()
    }

    private fun loadResponses() {
        viewModel.getResponseHeaders(survey_id)
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.responseHeaderFlow.collect {
                if (it.isEmpty()) {
                    recyclerView.visibility = View.GONE
                    placeholder.visibility = View.VISIBLE
                    return@collect
                }
                placeholder.visibility = View.GONE
                recyclerView.visibility = View.VISIBLE
                adapter1 = ResponseAdapter(it, this@ResponseList)
                recyclerView.adapter = adapter1
            }
        }
    }

    override fun onSelected(responseHeader: ResponseHeader) {
        val intent = Intent(requireContext(), ResponseDetail::class.java)
        intent.putExtra("survey_id", survey_id)
        intent.putExtra("ID", responseHeader.id)
        startActivity(intent)
    }

    companion object
}