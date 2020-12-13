package com.example.babajidemustapha.survey.features.Reports.fragments

import android.content.Intent
import android.os.Bundle
import android.text.format.DateFormat
import android.view.*
import android.widget.TextView
import androidx.appcompat.widget.SearchView
import androidx.core.view.MenuItemCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.babajidemustapha.survey.R
import com.example.babajidemustapha.survey.features.Reports.activities.SurveyReportActivity
import com.example.babajidemustapha.survey.features.dashboard.activities.DashboardActivity
import com.example.babajidemustapha.survey.shared.room.db.SurveyDatabase
import com.example.babajidemustapha.survey.shared.room.entities.Survey
import java.util.*

/**
 * A simple [Fragment] subclass.
 */
class ReportFragment : Fragment() {
    var db: SurveyDatabase? = null
    var adapter1: CustomAdapter1? = null
    var recyclerView: RecyclerView? = null
    var placeholder: TextView? = null
    var surveys: List<Survey?>? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        db = SurveyDatabase.Companion.getInstance(context)
        activity!!.title = "Reports"
        val view = inflater.inflate(R.layout.fragment_survey_list, container, false)
        recyclerView = view.findViewById(R.id.surveyList)
        placeholder = view.findViewById(R.id.emptyPlaceholder)
        surveys = ArrayList()
        recyclerView.setLayoutManager(LinearLayoutManager(context))
        loadSurveys()
        setHasOptionsMenu(true)
        return view
    }

    fun loadSurveys() {
        surveys = db!!.surveyDao().allSurveys
        if (surveys!!.isEmpty()) {
            recyclerView!!.visibility = View.GONE
            placeholder!!.visibility = View.VISIBLE
            return
        }
        placeholder!!.visibility = View.GONE
        recyclerView!!.visibility = View.VISIBLE
        adapter1 = CustomAdapter1(surveys)
        recyclerView!!.adapter = adapter1
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.main, menu)
        //        SearchView searchView =
//                (SearchView) menu.findItem(R.id.search).getActionView();
        val item = menu.findItem(R.id.search)
        val sv = SearchView((activity as DashboardActivity?)!!.supportActionBar!!.themedContext)
        MenuItemCompat.setShowAsAction(item, MenuItemCompat.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW or MenuItemCompat.SHOW_AS_ACTION_IF_ROOM)
        MenuItemCompat.setActionView(item, sv)
        sv.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                adapter1!!.changeSource(db!!.surveyDao().searchSurveys(query))
                adapter1!!.notifyDataSetChanged()
                return true
            }

            override fun onQueryTextChange(newText: String): Boolean {
                adapter1!!.changeSource(db!!.surveyDao().searchSurveys(newText))
                adapter1!!.notifyDataSetChanged()
                return true
            }
        })
        super.onCreateOptionsMenu(menu, inflater)
    }

    inner class CustomAdapter1 private constructor(source: MutableList<Survey?>) : RecyclerView.Adapter<CustomAdapter1.ViewHolder>() {
        var source: MutableList<Survey?>?
        private fun add(survey: Survey) {
            source!!.add(survey)
        }

        fun changeSource(source: MutableList<Survey?>?) {
            this.source = source
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            return ViewHolder(activity!!.layoutInflater.inflate(R.layout.survey_item_layout, parent, false))
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.name.text = if (source!![position]!!.name!!.length > 30) source!![position]!!.name!!.substring(0, 30) + "..." else source!![position]!!.name
            holder.desc.text = if (source!![position]!!.desc!!.length > 40) source!![position]!!.desc!!.substring(0, 40) + "..." else source!![position]!!.desc
            holder.date.text = DateFormat.format("yyyy-MM-dd", source!![position]!!.date)

//            holder.no_of_ques.setText(source.get(position).getNoOfQues()+" question(s)");
            holder.privacy.text = if (source!![position]!!.isPrivacy) "Public" else "Private"
        }

        override fun getItemCount(): Int {
            return source!!.size
        }

        protected inner class ViewHolder private constructor(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
            var name: TextView
            var desc: TextView
            var date: TextView
            var no_of_ques: TextView? = null
            var privacy: TextView
            override fun onClick(view: View) {
                val intent = Intent(activity, SurveyReportActivity::class.java)
                intent.putExtra("ID", source!![adapterPosition]!!.id)
                intent.putExtra("name", source!![adapterPosition]!!.name)
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

        init {
            this.source = source
        }
    }
}