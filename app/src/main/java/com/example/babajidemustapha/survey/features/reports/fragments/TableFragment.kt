package com.example.babajidemustapha.survey.features.reports.fragments

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.babajidemustapha.survey.R
import com.example.babajidemustapha.survey.features.reports.viewmodel.ReportViewModel
import com.example.babajidemustapha.survey.shared.models.QuestionType

import com.example.babajidemustapha.survey.shared.room.entities.Question
import com.example.babajidemustapha.survey.shared.room.entities.ResponseDetail
import com.jide.surveyapp.model.relations.Report
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import org.json.JSONArray
import org.json.JSONException
import java.io.FileOutputStream
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class TableFragment : Fragment() {
    private lateinit var survey_id: String
    lateinit var recyclerView: RecyclerView
    var questions: MutableList<Question?>? = null
    var xyCoord: MutableMap<Question?, Map<String?, Int?>>? = null
    private val REQUEST_PERMISSIONS_CODE_WRITE_STORAGE = 18880
    var tableLayout: TableLayout? = null
    var inflater: LayoutInflater? = null

    private val viewModel by viewModels<ReportViewModel>()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        tableLayout = null
        survey_id = requireActivity().intent.extras.getString("ID")
        questions = ArrayList()
        xyCoord = LinkedHashMap()
        recyclerView = view.findViewById(R.id.tableList)
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
        this.inflater = inflater
        return inflater.inflate(R.layout.fragment_table, container, false)
    }

    fun buildXYcoord(report: Map<Question, List<ResponseDetail>>) {
        for ((key, value) in report) {
            Log.e("question NO: ", key.questionNo.toString())
            when (key.questionType) {
                QuestionType.SHORT_TEXT, QuestionType.SINGLE_OPTION -> {
                    val xy: MutableMap<String?, Int?> = LinkedHashMap()
                    for (responseDetail in value) {
                        if (responseDetail.response != null && !responseDetail.response!!.trim { it <= ' ' }.isEmpty()) {
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
                    xyCoord!![key] = xy1
                    questions!!.add(key)
                }
            }
        }
    }

    inner class CustomAdapter1 constructor(val source: List<Report>) : RecyclerView.Adapter<CustomAdapter1.ViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            return ViewHolder(activity!!.layoutInflater.inflate(R.layout.table_view, parent, false))
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.bind()
        }

        override fun getItemCount(): Int {
            return source.size
        }

        inner class ViewHolder constructor(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
            var qu_text: TextView
            var qu_no: TextView
            var no_data: TextView
            var btn: Button
            var tableLayout1: TableLayout
            override fun onClick(view: View) {
                val view1 = recyclerView.findViewHolderForAdapterPosition(adapterPosition)!!.itemView
                tableLayout = view1.findViewById(R.id.table)
                checkFilePermissionAndSave()
            }

            init {
                qu_no = itemView.findViewById(R.id.qu_no)
                qu_text = itemView.findViewById(R.id.qu_text)
                no_data = itemView.findViewById(R.id.nodata)
                tableLayout1 = itemView.findViewById(R.id.table)
                btn = itemView.findViewById(R.id.btn)
                //                btn.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        View view1 =  recyclerView.findViewHolderForAdapterPosition(getAdapterPosition()).itemView;
//                        pieChart1 = (PieChart)  view1.findViewById(R.id.pieChart);
//                        checkFilePermissionAndSave();
//                    }
//                });
                btn.setOnClickListener(this)
            }

            fun bind() {
                qu_no.text = source[adapterPosition].question.questionNo.toString()
                qu_text.text = source[adapterPosition].question.questionText
                if (source[adapterPosition].report.isEmpty()) {
                    // holder.pieChart.setNoDataText("No response has been recorded for this question");
                    btn.visibility = View.GONE
                }
                source[adapterPosition].report.forEachIndexed { index, pair ->
                    val tableRow = inflater!!.inflate(R.layout.table_row_view, itemView as ViewGroup, false) as TableRow
                    val response = tableRow.findViewById<TextView>(R.id.responseDetail)
                    val count = tableRow.findViewById<TextView>(R.id.count)
                    response.text = pair.first.optionText
                    count.text = pair.second.toString()
                    tableRow.setBackgroundColor(if (index % 2 == 0) -0x1f1f20 else -0x1)
                    tableLayout1.addView(tableRow)
                }
            }
        }
    }

    private fun saveImage(tableLayout: TableLayout): Boolean {
        val bitmap = getBitmapFromView(tableLayout)
        return if (bitmap != null) {
            saveBitmap(bitmap)
        } else false
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
            if (tableLayout != null && saveImage(tableLayout!!)) {
                Toast.makeText(context, "Table saved", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "Table failed to save", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestCode == REQUEST_PERMISSIONS_CODE_WRITE_STORAGE) {
            if (permissions[0] == Manifest.permission.WRITE_EXTERNAL_STORAGE && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (tableLayout != null && saveImage(tableLayout!!)) {
                    Toast.makeText(context, "Table saved", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "Table failed to save", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    companion object {
        fun saveBitmap(bitmap: Bitmap): Boolean {
            var stream: OutputStream? = null
            val filename = SimpleDateFormat("ddMMyyyyHHmmss:SSSS").format(Date())
            try {
                stream = FileOutputStream(Environment.getExternalStorageDirectory().absolutePath
                        + "/DCIM/" + filename + ".png")
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
                stream.close()
            } catch (e: Exception) {
                e.printStackTrace()
                return false
            }
            return true
        }

        fun getBitmapFromView(view: View): Bitmap {
            //Define a bitmap with the same size as the view
            val returnedBitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
            //Bind a canvas to it
            val canvas = Canvas(returnedBitmap)
            //Get the view's background
            val bgDrawable = view.background
            if (bgDrawable != null) //has background drawable, then draw it on the canvas
                bgDrawable.draw(canvas) else  //does not have background drawable, then draw white background on the canvas
                canvas.drawColor(Color.WHITE)
            // draw the view on the canvas
            view.draw(canvas)
            //return the bitmap
            return returnedBitmap
        }
    }
}