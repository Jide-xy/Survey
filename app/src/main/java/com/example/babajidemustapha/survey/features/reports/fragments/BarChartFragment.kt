package com.example.babajidemustapha.survey.features.reports.fragments


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
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.babajidemustapha.survey.R
import com.example.babajidemustapha.survey.features.reports.viewmodel.ReportViewModel
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
import com.jide.surveyapp.model.QuestionType
import com.jide.surveyapp.model.relations.Report
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class BarChartFragment : Fragment() {
    private lateinit var survey_id: String
    lateinit var recyclerView: RecyclerView
    private val REQUEST_PERMISSIONS_CODE_WRITE_STORAGE = 18880
    var barChart1: BarChart? = null
    private val viewModel by viewModels<ReportViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        barChart1 = null
        survey_id = requireActivity().intent.extras.getString("ID")
        recyclerView = view.findViewById(R.id.barList)
        recyclerView.layoutManager = LinearLayoutManager(context)
        viewModel.getReport(survey_id)

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.reportFlow.collect {
                recyclerView.adapter = CustomAdapter1(it)
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_bar_chart, container, false)
    }

    inner class CustomAdapter1(var source: List<Report>) : RecyclerView.Adapter<CustomAdapter1.ViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            return ViewHolder(activity!!.layoutInflater.inflate(R.layout.bar_chart_view, parent, false))
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.bind()
        }

        override fun getItemCount(): Int {
            return source.size
        }

        inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
            private var qu_text: TextView = itemView.findViewById(R.id.qu_text)
            private var qu_no: TextView = itemView.findViewById(R.id.qu_no)
            private var btn: Button = itemView.findViewById(R.id.btn)
            private var barChart: BarChart = itemView.findViewById(R.id.barChart)

            override fun onClick(view: View) {
//                Intent intent = new Intent(getContext(), ResponseDetail.class);
//                intent.putExtra("survey_id",survey_id);
//                intent.putExtra("ID",source.get(getAdapterPosition()).getResponse_id());
//                startActivity(intent);
            }

            init {
                btn.setOnClickListener {
                    val view1 = recyclerView.findViewHolderForAdapterPosition(adapterPosition)!!.itemView
                    barChart1 = view1.findViewById(R.id.barChart)
                    checkFilePermissionAndSave()
                }
                //itemView.setOnClickListener(this);
            }

            fun bind() {
                qu_no.text = source[adapterPosition].question.questionNo.toString()
                qu_text.text = source[adapterPosition].question.questionText
                when (source[adapterPosition].question.questionType) {
                    QuestionType.SHORT_TEXT, QuestionType.LONG_TEXT -> {
                        barChart.setNoDataText("Bar Chart not available for this question type")
                        btn.visibility = View.GONE
                    }
                    else -> {
                        val data: MutableList<BarEntry> = mutableListOf()
                        val labels = Array(source[adapterPosition].report.size) { "" }
                        val colors: MutableList<Int> = mutableListOf()

                        if (source[adapterPosition].report.isEmpty() || source[adapterPosition].report.all { it.second == 0 }) {
                            barChart.setNoDataText("No response has been recorded for this question")
                            btn.visibility = View.GONE
                        } else {
                            source[adapterPosition].report.forEachIndexed { index, pair ->
                                data.add(BarEntry(index.toFloat(), pair.second.toFloat()))
                                val rnd = Random()
                                val color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256))
                                colors.add(color)
                                labels[index] = pair.first.optionText ?: ""
                            }
                            val dataSet = BarDataSet(data, "Responses")
                            dataSet.colors = colors
                            dataSet.isHighlightEnabled = true
                            val barData = BarData(dataSet)
                            barData.setValueTextSize(20f)
                            barData.barWidth = 0.5f
                            barData.setValueFormatter { _, entry, _, _ -> if (entry.y == 0f) "" else entry.y.toInt().toString() + "" }
                            barChart.data = barData
                            barChart.setPinchZoom(true)
                            barChart.axisRight.isEnabled = false
                            val xaxis = barChart.xAxis
                            val yaxis = barChart.axisLeft
                            yaxis.setDrawGridLines(false)
                            xaxis.position = XAxis.XAxisPosition.BOTTOM
                            xaxis.setDrawGridLines(false)
                            xaxis.setDrawAxisLine(true)
                            xaxis.granularity = 1f
                            xaxis.valueFormatter = MyXAxisValueFormatter(labels)
                            //   barChart.setMarker(new MyMarkerView(getContext(),R.layout.marker_view,labels));
                            barChart.description.isEnabled = false
                            // Legend legend = barChart.getLegend();
                            barChart.animateXY(3000, 3000)
                        }
                    }
                }
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
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(),
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