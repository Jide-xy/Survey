package com.example.babajidemustapha.survey.features.Reports.fragments

import android.Manifest
import android.content.Context
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
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.MarkerView
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.formatter.IAxisValueFormatter
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.utils.MPPointF
import org.json.JSONArray
import org.json.JSONException
import java.text.SimpleDateFormat
import java.util.*

/**
 * A simple [Fragment] subclass.
 */
class BarChartFragment : Fragment() {
    var survey_id = 0
    var db: SurveyDatabase? = null
    lateinit var recyclerView: RecyclerView
    var questions: MutableList<Question> = mutableListOf()
    var xyCoord: MutableMap<Question, Map<String, Int>?> = mutableMapOf()
    private val REQUEST_PERMISSIONS_CODE_WRITE_STORAGE = 18880
    var barChart1: BarChart? = null
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        barChart1 = null
        survey_id = activity!!.intent.extras.getInt("ID")
        db = SurveyDatabase.Companion.getInstance(context)
        questions = ArrayList()
        xyCoord = LinkedHashMap()
        recyclerView = view.findViewById(R.id.barList)
        recyclerView.layoutManager = LinearLayoutManager(context)
        DbOperationHelper.execute<Map<Question, List<ResponseDetail>>>(object : IDbOperationHelper<Map<Question, List<ResponseDetail>>> {
            override fun run(): Map<Question, List<ResponseDetail>> {
                return db!!.surveyDao().generateReport(survey_id)
            }

            override fun onCompleted(result: Map<Question, List<ResponseDetail>>) {
                buildXYcoord(result)
                recyclerView.adapter = CustomAdapter1(questions)
            }
        })
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_bar_chart, container, false)
    }

    fun buildXYcoord(report: Map<Question, List<ResponseDetail>>) {
        for ((key, value) in report) {

            when (key.questionType) {
                QuestionType.SHORT_TEXT -> {
                    xyCoord[key] = null
                    questions.add(key)
                }
                QuestionType.SINGLE_OPTION -> {
                    val xy: MutableMap<String, Int> = LinkedHashMap()
                    var i = 0
                    while (i < key.options!!.size) {
                        xy[key.options!![i]] = 0
                        i++
                    }
                    for (responseDetail in value) {
                        if (responseDetail.response != null && !responseDetail.response!!.trim { it <= ' ' }.isEmpty()) {
                            if (xy.containsKey(responseDetail.response!!)) {
                                val i = xy[responseDetail.response!!]!! + 1
                                xy[responseDetail.response!!] = i
                            } else {
                                xy[responseDetail.response!!] = 1
                            }
                        }
                    }
                    xyCoord[key] = xy
                    questions.add(key)
                }
                QuestionType.MULTIPLE_OPTION -> {
                    val xy1: MutableMap<String, Int> = LinkedHashMap()
                    var i = 0
                    while (i < key.options!!.size) {
                        xy1[key.options!![i]] = 0
                        i++
                    }
                    for (responseDetail in value) {
                        try {
                            val res = JSONArray(responseDetail.response)
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
                    xyCoord[key] = xy1
                    questions.add(key)
                }
            }
        }
    }

    inner class CustomAdapter1(var source: MutableList<Question>) : RecyclerView.Adapter<CustomAdapter1.ViewHolder>() {
        private fun add(responseHeader: Question) {
            source.add(responseHeader)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            return ViewHolder(activity!!.layoutInflater.inflate(R.layout.bar_chart_view, parent, false))
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.qu_no.text = source[position].questionNo.toString()
            holder.qu_text.text = source[position].questionText
            when (source[position].questionType) {
                QuestionType.SHORT_TEXT -> {
                    holder.barChart.setNoDataText("Bar Chart not available for this question type")
                    holder.btn.visibility = View.GONE
                }
                else -> {
                    val data: MutableList<BarEntry> = ArrayList()
                    var i = 0
                    var noData = true
                    val labels = arrayOf<String>(xyCoord[source[position]]!!.entries.size.toString())
                    val colors: MutableList<Int> = ArrayList()
                    Log.e("sharp", xyCoord[source[position]]!!.size.toString() + "")
                    if (xyCoord[source[position]]!!.isEmpty()) {
                        holder.barChart.setNoDataText("No response has been recorded for this question")
                        holder.btn.visibility = View.GONE
                    } else {
                        for ((key, value) in xyCoord[source[position]]!!) {
                            data.add(BarEntry(i.toFloat(), value.toFloat()))
                            val rnd = Random()
                            val color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256))
                            colors.add(color)
                            labels[i] = key
                            i += 1
                            if (value > 0) {
                                noData = false
                            }
                        }
                        if (noData) {
                            holder.barChart.setNoDataText("No response has been recorded for this question")
                            holder.btn.visibility = View.GONE
                        } else {
                            val dataSet = BarDataSet(data, "Responses")
                            dataSet.colors = colors
                            dataSet.isHighlightEnabled = true
                            val barData = BarData(dataSet)
                            barData.setValueTextSize(20f)
                            barData.barWidth = 0.5f
                            barData.setValueFormatter { value, entry, dataSetIndex, viewPortHandler -> if (entry.y == 0f) "" else entry.y.toInt().toString() + "" }
                            holder.barChart.data = barData
                            holder.barChart.setPinchZoom(true)
                            holder.barChart.axisRight.isEnabled = false
                            val xaxis = holder.barChart.xAxis
                            val yaxis = holder.barChart.axisLeft
                            yaxis.setDrawGridLines(false)
                            xaxis.position = XAxis.XAxisPosition.BOTTOM
                            xaxis.setDrawGridLines(false)
                            xaxis.setDrawAxisLine(true)
                            xaxis.granularity = 1f
                            xaxis.valueFormatter = MyXAxisValueFormatter(labels)
                            //   holder.barChart.setMarker(new MyMarkerView(getContext(),R.layout.marker_view,labels));
                            holder.barChart.description.isEnabled = false
                            // Legend legend = holder.barChart.getLegend();
                            holder.barChart.animateXY(3000, 3000)
                        }
                    }
                }
            }
        }

        override fun getItemCount(): Int {
            return source.size
        }

        inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
            var qu_text: TextView
            var qu_no: TextView
            var btn: Button
            var barChart: BarChart
            override fun onClick(view: View) {
//                Intent intent = new Intent(getContext(), ResponseDetail.class);
//                intent.putExtra("survey_id",survey_id);
//                intent.putExtra("ID",source.get(getAdapterPosition()).getResponse_id());
//                startActivity(intent);
            }

            init {
                qu_no = itemView.findViewById(R.id.qu_no)
                qu_text = itemView.findViewById(R.id.qu_text)
                barChart = itemView.findViewById(R.id.barChart)
                btn = itemView.findViewById(R.id.btn)
                btn.setOnClickListener {
                    val view1 = recyclerView.findViewHolderForAdapterPosition(adapterPosition)!!.itemView
                    barChart1 = view1.findViewById(R.id.barChart)
                    checkFilePermissionAndSave()
                }
                //itemView.setOnClickListener(this);
            }
        }

        protected inner class MyXAxisValueFormatter(private val mValues: Array<String>) : IAxisValueFormatter {
            override fun getFormattedValue(value: Float, axis: AxisBase): String {
                // "value" represents the position of the label on the axis (x or y)
                Log.e("AXIS VALUE", value.toString() + "")
                return mValues[value.toInt()]
            }

            /** this is only needed if numbers are returned, else return 0  */
            val decimalDigits: Int
                get() = 0
        }
    }

    private inner class MyMarkerView(context: Context?, layoutResource: Int, labels: Array<String>) : MarkerView(context, layoutResource) {
        private val tvContent: TextView
        private val labels: Array<String>

        // callbacks everytime the MarkerView is redrawn, can be used to update the
        // content (user-interface)
        override fun refreshContent(e: Entry, highlight: Highlight) {
            Log.e("refreshing marker: ", labels[e.x.toInt() - 1])
            tvContent.text = labels[e.x.toInt() - 1] + ": " + e.y

            // this will perform necessary layouting
            super.refreshContent(e, highlight)
        }

        private var mOffset: MPPointF? = null
        override fun getOffset(): MPPointF {
            if (mOffset == null) {
                // center the marker horizontally and vertically
                mOffset = MPPointF((-(width / 2)).toFloat(), (-height).toFloat())
            }
            return mOffset!!
        }

        init {
            tvContent = findViewById(R.id.marker)
            //   tvContent = (TextView) getActivity().getLayoutInflater().inflate(layoutResource,null).findViewById(R.id.marker);
            this.labels = labels
            // find your layout components
        }
    }

    fun saveImage(barChart: BarChart): Boolean {
        return barChart.saveToGallery("bar_chart_" + SimpleDateFormat("yyyy_MM_dd_HHmmss").format(Date()), 100)
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
            if (barChart1 != null && saveImage(barChart1!!)) {
                Toast.makeText(context, "Chart saved", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "Chart failed to save", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestCode == REQUEST_PERMISSIONS_CODE_WRITE_STORAGE) {
            if (permissions[0] == Manifest.permission.WRITE_EXTERNAL_STORAGE && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (barChart1 != null && saveImage(barChart1!!)) {
                    Toast.makeText(context, "Chart saved", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "Chart failed to save", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(context, "Chart failed to save", Toast.LENGTH_SHORT).show()
            }
        }
    }
}