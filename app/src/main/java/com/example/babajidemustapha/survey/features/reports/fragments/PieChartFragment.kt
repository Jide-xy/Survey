package com.example.babajidemustapha.survey.features.reports.fragments

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
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
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter
import com.jide.surveyapp.model.QuestionType
import com.jide.surveyapp.model.relations.Report
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import java.text.SimpleDateFormat
import java.util.*


@AndroidEntryPoint
class PieChartFragment : Fragment() {
    private lateinit var survey_id: String
    lateinit var recyclerView: RecyclerView
    private val REQUEST_PERMISSIONS_CODE_WRITE_STORAGE = 18880
    var pieChart1: PieChart? = null

    private val viewModel by viewModels<ReportViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        pieChart1 = null
        survey_id = requireActivity().intent.extras.getString("ID", "")
        recyclerView = view.findViewById(R.id.pieList)!!
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
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_pie_chart, container, false)
    }

    private inner class CustomAdapter1 constructor(var source: List<Report>) : RecyclerView.Adapter<CustomAdapter1.ViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            return ViewHolder(activity!!.layoutInflater.inflate(R.layout.pie_chart_view, parent, false))
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.bind()
        }

        override fun getItemCount(): Int {
            return source.size
        }

        inner class ViewHolder constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
            var qu_text: TextView
            var qu_no: TextView
            var btn: Button
            var pieChart: PieChart

            init {
                qu_no = itemView.findViewById(R.id.qu_no)
                qu_text = itemView.findViewById(R.id.qu_text)
                pieChart = itemView.findViewById(R.id.pieChart)
                btn = itemView.findViewById(R.id.btn)
                btn.setOnClickListener {
                    val view1 = recyclerView.findViewHolderForAdapterPosition(adapterPosition)!!.itemView
                    pieChart1 = view1.findViewById(R.id.pieChart)
                    checkFilePermissionAndSave()
                }
                // btn.setOnClickListener(this);
            }

            fun bind() {
                qu_no.text = source[adapterPosition].question.questionNo.toString()
                qu_text.text = source[adapterPosition].question.questionText
                when (source[adapterPosition].question.questionType) {
                    QuestionType.SHORT_TEXT -> {
                        pieChart.setNoDataText("Pie Chart not available for this question type")
                        btn.visibility = View.GONE
                    }
                    else -> {
                        val data: MutableList<PieEntry> = ArrayList()
                        val colors: MutableList<Int> = ArrayList()
                        if (source[adapterPosition].report.isEmpty() || source[adapterPosition].report.all { it.second == 0 }) {
                            pieChart.setNoDataText("No response has been recorded for this question")
                            btn.visibility = View.GONE
                        } else {
                            source[adapterPosition].report.forEachIndexed { index, pair ->
                                val e = PieEntry(index.toFloat(), pair.second.toFloat())
                                val rnd = Random()
                                val color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256))
                                colors.add(color)
                                data.add(e)
                            }
                            val dataSet = PieDataSet(data, "Responses")
                            dataSet.sliceSpace = 1f
                            dataSet.colors = colors
                            val pieData = PieData(dataSet)
                            pieData.setValueTextSize(20f)
                            pieData.setValueFormatter(PercentFormatter())
                            pieChart.data = pieData
                            pieChart.setUsePercentValues(true)
                            pieChart.description.isEnabled = false
                            val legend = pieChart.legend
                            legend.maxSizePercent = 1f
                            //  legend.setXOffset(5.0f);
                            legend.horizontalAlignment = Legend.LegendHorizontalAlignment.RIGHT
                            legend.verticalAlignment = Legend.LegendVerticalAlignment.TOP
                            legend.orientation = Legend.LegendOrientation.VERTICAL
                            legend.form = Legend.LegendForm.CIRCLE
                            pieChart.animateXY(2000, 2000)
                        }
                    }
                }
            }
        }
    }

    fun saveImage(pieChart: PieChart): Boolean {
        return pieChart.saveToGallery("pie_chart_" + SimpleDateFormat("yyyy_MM_dd_HHmmss").format(Date()), 100)
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