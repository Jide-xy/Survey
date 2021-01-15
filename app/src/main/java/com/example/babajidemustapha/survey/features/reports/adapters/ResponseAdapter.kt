package com.example.babajidemustapha.survey.features.reports.adapters

import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.babajidemustapha.survey.R
import com.jide.surveyapp.model.ResponseHeader

class ResponseAdapter(private val source: List<ResponseHeader>, var mListener: OnResponseSelectedListener) : RecyclerView.Adapter<ResponseAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.response_list_item_layout, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind()
    }

    override fun getItemCount(): Int {
        return source.size
    }

    interface OnResponseSelectedListener {
        fun onSelected(responseHeader: ResponseHeader)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        var name: TextView = itemView.findViewById(R.id.response_name)
        var date: TextView = itemView.findViewById(R.id.response_date)
        var time: TextView = itemView.findViewById(R.id.response_time)

        override fun onClick(view: View) {
            mListener.onSelected(source[adapterPosition])
        }

        fun bind() {
            name.text = "Response by: ${if (source[adapterPosition].respondentName.isNullOrBlank().not()) source[adapterPosition].respondentName else "Anonymous"}"
            date.text = DateFormat.format("dd/MM/yy", source[adapterPosition].date.toLong())
            time.text = DateFormat.format("HH:mm", source[adapterPosition].date.toLong())
        }

        init {
            itemView.setOnClickListener(this)
        }
    }
}