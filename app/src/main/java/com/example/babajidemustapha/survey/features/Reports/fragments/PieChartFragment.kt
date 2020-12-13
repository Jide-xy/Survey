package com.example.babajidemustapha.survey.features.Reports.fragments

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.babajidemustapha.survey.R
import com.example.babajidemustapha.survey.shared.models.QuestionType
import com.example.babajidemustapha.survey.shared.room.db.SurveyDatabase
import com.example.babajidemustapha.survey.shared.room.entities.Question
import com.example.babajidemustapha.survey.shared.room.entities.ResponseDetail
import com.example.babajidemustapha.survey.shared.utils.DbOperationHelper
import com.example.babajidemustapha.survey.shared.utils.DbOperationHelper.IDbOperationHelper
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter
import org.json.JSONArray
import org.json.JSONException
import java.text.SimpleDateFormat
import java.util.*

/**
 * A simple [Fragment] subclass.
 */
class PieChartFragment : Fragment() {
    var survey_id = 0
    var db: SurveyDatabase? = null
    var recyclerView: RecyclerView? = null
    var questions: MutableList<Question?>? = null
    var xyCoord: MutableMap<Question?, Map<String?, Int?>?>? = null
    private val REQUEST_PERMISSIONS_CODE_WRITE_STORAGE = 18880
    var pieChart1: PieChart? = null
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        pieChart1 = null
        survey_id = activity!!.intent.extras.getInt("ID")
        db = SurveyDatabase.Companion.getInstance(context)
        questions = ArrayList()
        xyCoord = LinkedHashMap()
        recyclerView = view.findViewById(R.id.pieList)
        recyclerView.setLayoutManager(LinearLayoutManager(context))
        DbOperationHelper.Companion.execute(object : IDbOperationHelper<Map<Question?, List<ResponseDetail?>?>?> {
            override fun run(): Map<Question?, List<ResponseDetail?>?>? {
                return db!!.surveyDao().generateReport(survey_id)
            }

            override fun onCompleted(reports: Map<Question?, List<ResponseDetail?>?>?) {
                buildXYcoord(reports)
                recyclerView.setAdapter(CustomAdapter1(questions))
            }
        })
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_pie_chart, container, false)
    }

    fun buildXYcoord(report: Map<Question?, List<ResponseDetail?>?>?) {
        for ((key, value) in report!!) {
            Log.e("question NO: ", key!!.questionNo + "")
            when (key.questionType) {
                QuestionType.SHORT_TEXT -> {
                    xyCoord!![key] = null
                    questions!!.add(key)
                }
                QuestionType.SINGLE_OPTION -> {
                    val xy: MutableMap<String?, Int?> = LinkedHashMap()
                    var i = 0
                    while (i < key.options!!.size) {
                        xy[key.options!![i]] = 0
                        i++
                    }
                    for (responseDetail in value!!) {
                        if (responseDetail!!.response != null && !responseDetail.response!!.trim { it <= ' ' }.isEmpty()) {
                            if (xy.containsKey(responseDetail.response)) {
                                val i = xy[responseDetail.response]!! + 1
                                xy[responseDetail.response] = i
                            } else {
                                xy[responseDetail.response] = 1
                            }
                        }
                    }
                    xyCoord!![key] = xy
                    questions!!.add(key)
                }
                QuestionType.MULTIPLE_OPTION -> {
                    val xy1: MutableMap<String?, Int?> = LinkedHashMap()
                    var i = 0
                    while (i < key.options!!.size) {
                        xy1[key.options!![i]] = 0
                        i++
                    }
                    for (responseDetail in value!!) {
                        try {
                            val res = JSONArray(responseDetail!!.response)
                            Log.e("multi ans", res.toString())
                            var i = 0
                            while (i < res.length()) {
                                if (xy1.containsKey(res.getString(i))) {
                                    val j = xy1[res.getString(i)]!! + 1
                                    xy1[res.getString(i)] = j
                                } else {
                                    xy1[res.getString(i)] = 1
                                }
                                i++
                            }
                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }
                    }
                    xyCoord!![key] = xy1
                    questions!!.add(key)
                }
            }
        }
    }

    private inner class CustomAdapter1 private constructor(var source: MutableList<Question>) : RecyclerView.Adapter<CustomAdapter1.ViewHolder>() {
        private fun add(responseHeader: Question) {
            source.add(responseHeader)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            return ViewHolder(activity!!.layoutInflater.inflate(R.layout.pie_chart_view, parent, false))
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.qu_no.setText(source[position].questionNo + "")
            holder.qu_text.text = source[position].questionText
            when (source[position].questionType) {
                QuestionType.SHORT_TEXT -> {
                    holder.pieChart.setNoDataText("Pie Chart not available for this question type")
                    holder.btn.visibility = View.GONE
                }
                else -> {
                    val data: MutableList<PieEntry> = ArrayList()
                    val colors: MutableList<Int> = ArrayList()
                    var noData = true
                    if (xyCoord!![source[position]]!!.size <= 0) {
                        holder.pieChart.setNoDataText("No response has been recorded for this question")
                        holder.btn.visibility = View.GONE
                    } else {
                        for ((key, value) in xyCoord!![source[position]]!!) {
                            val e = PieEntry(value as Int, key)
                            val rnd = Random()
                            val color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256))
                            colors.add(color)
                            data.add(e)
                            if (value as Int > 0) {
                                noData = false
                            }
                        }
                        if (noData) {
                            holder.pieChart.setNoDataText("No response has been recorded for this question")
                            holder.btn.visibility = View.GONE
                        } else {
                            val dataSet = PieDataSet(data, "Responses")
                            dataSet.sliceSpace = 1f
                            dataSet.colors = colors
                            val pieData = PieData(dataSet)
                            pieData.setValueTextSize(20f)
                            pieData.setValueFormatter(PercentFormatter())
                            holder.pieChart.data = pieData
                            holder.pieChart.setUsePercentValues(true)
                            holder.pieChart.description.isEnabled = false
                            val legend = holder.pieChart.legend
                            legend.maxSizePercent = 1f
                            //  legend.setXOffset(5.0f);
                            legend.horizontalAlignment = Legend.LegendHorizontalAlignment.RIGHT
                            legend.verticalAlignment = Legend.LegendVerticalAlignment.TOP
                            legend.orientation = Legend.LegendOrientation.VERTICAL
                            legend.form = Legend.LegendForm.CIRCLE
                            holder.pieChart.animateXY(2000, 2000)
                            break
                        }
                    }
                }
            }
        }

        override fun getItemCount(): Int {
            return source.size
        }

        fun onClick(view: View?) {

            //
        }

        protected inner class ViewHolder private constructor(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
            var qu_text: TextView
            var qu_no: TextView
            var btn: Button
            var pieChart: PieChart
            override fun onClick(view: View) {}

            init {
                qu_no = itemView.findViewById(R.id.qu_no)
                qu_text = itemView.findViewById(R.id.qu_text)
                pieChart = itemView.findViewById(R.id.pieChart)
                btn = itemView.findViewById(R.id.btn)
                btn.setOnClickListener {
                    val view1 = recyclerView!!.findViewHolderForAdapterPosition(adapterPosition)!!.itemView
                    pieChart1 = view1.findViewById(R.id.pieChart)
                    checkFilePermissionAndSave()
                }
                // btn.setOnClickListener(this);
            }
        }
    }

    fun saveImage(pieChart: PieChart): Boolean {
        return pieChart.saveToGallery("pie_chart_" + SimpleDateFormat("yyyy_MM_dd_HHmmss").format(Date()), 100)
    }

    fun checkFilePermissionAndSave() {
        if (ContextCompat.checkSelfPermission(context!!, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity!!,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            } else {

                // No explanation needed, we can request the permission.
                requestPermissions(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), REQUEST_PERMISSIONS_CODE_WRITE_STORAGE)

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else {
            if (pieChart1 != null && saveImage(pieChart1!!)) {
                Toast.makeText(context, "Chart saved", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "Chart failed to save", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestCode == REQUEST_PERMISSIONS_CODE_WRITE_STORAGE) {
            if (permissions[0] == Manifest.permission.WRITE_EXTERNAL_STORAGE && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (pieChart1 != null && saveImage(pieChart1!!)) {
                    Toast.makeText(context, "Chart saved", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "Chart failed to save", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}