package com.example.babajidemustapha.survey.features.Reports.fragments

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.babajidemustapha.survey.R
import com.example.babajidemustapha.survey.features.Reports.activities.ResponseDetail
import com.example.babajidemustapha.survey.features.Reports.adapters.ResponseAdapter
import com.example.babajidemustapha.survey.features.Reports.adapters.ResponseAdapter.OnResponseSelectedListener
import com.example.babajidemustapha.survey.features.dashboard.fragments.SurveyList
import com.example.babajidemustapha.survey.shared.room.db.SurveyDatabase
import com.example.babajidemustapha.survey.shared.room.entities.ResponseHeader
import com.example.babajidemustapha.survey.shared.utils.DbOperationHelper
import com.example.babajidemustapha.survey.shared.utils.DbOperationHelper.IDbOperationHelper
import java.util.*

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [SurveyList.OnNavigationMenuSelected] interface
 * to handle interaction events.
 * Use the [SurveyList.newInstance] factory method to
 * create an instance of this fragment.
 */
class ResponseList : Fragment(), OnResponseSelectedListener {
    var recyclerView: RecyclerView? = null
    var db: SurveyDatabase? = null
    var survey_id = 0
    var context: Context? = null
    var placeholder: TextView? = null
    var responses: MutableList<ResponseHeader?>? = null
    var adapter1: ResponseAdapter? = null

    // TODO: Rename and change types of parameters
    private var mParam1: String? = null
    private var mParam2: String? = null
    private var mListener: OnFragmentInteractionListener? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            mParam1 = arguments!!.getString(ARG_PARAM1)
            mParam2 = arguments!!.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.content_response_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        db = SurveyDatabase.Companion.getInstance(getContext())
        val bundle = activity!!.intent.extras
        survey_id = bundle.getInt("ID")
        //Log.e("survey_id", survey_id+"");
        recyclerView = view.findViewById(R.id.responseList)
        placeholder = view.findViewById(R.id.emptyPlaceholder)
        responses = ArrayList()
        recyclerView.setLayoutManager(LinearLayoutManager(getContext()))
        loadResponses()
    }

    fun loadResponses() {
        DbOperationHelper.Companion.execute(object : IDbOperationHelper<MutableList<ResponseHeader?>?> {
            override fun run(): MutableList<ResponseHeader?>? {
                return db!!.responseHeaderDao().getResponseHeaders(survey_id)
            }

            override fun onCompleted(responseHeaders: MutableList<ResponseHeader?>?) {
                responses = responseHeaders
                if (responses!!.isEmpty()) {
                    recyclerView!!.visibility = View.GONE
                    placeholder!!.visibility = View.VISIBLE
                    return
                }
                placeholder!!.visibility = View.GONE
                recyclerView!!.visibility = View.VISIBLE
                adapter1 = ResponseAdapter(responses, this@ResponseList)
                recyclerView!!.adapter = adapter1
            }
        })
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        this.context = context
        if (context is OnFragmentInteractionListener) {
            mListener = context
        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
        }
    }

    override fun onDetach() {
        super.onDetach()
        mListener = null
    }

    override fun onSelected(responseHeader: ResponseHeader?) {
        val intent = Intent(getContext(), ResponseDetail::class.java)
        intent.putExtra("survey_id", survey_id)
        intent.putExtra("ID", responseHeader!!.responseId)
        startActivity(intent)
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
    interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        fun onFragmentInteraction(uri: Uri?)
    }

    companion object {
        // TODO: Rename parameter arguments, choose names that match
        // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
        private const val ARG_PARAM1 = "param1"
        private const val ARG_PARAM2 = "param2"
    }
}