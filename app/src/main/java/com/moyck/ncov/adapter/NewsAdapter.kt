package com.moyck.ncov.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.moyck.ncov.R
import com.moyck.ncov.domain.New
import com.github.vipulasri.timelineview.TimelineView







class NewsAdapter(val data: List<New>) : RecyclerView.Adapter<NewsAdapter.ViewHolder>() {


    override fun getItemViewType(position: Int): Int {
        return TimelineView.getTimeLineViewType(position, itemCount)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.news_item, parent, false)
        return ViewHolder(v,viewType)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.time.text = data[position].time
        holder.title.text = data[position].title
        holder.from.text = data[position].source
        holder.desc.text = data[position].desc
     }


    class ViewHolder(itemView: View,viewType: Int) : RecyclerView.ViewHolder(itemView) {

        var mTimelineView: TimelineView = itemView.findViewById(R.id.timeline)
        var time: TextView = itemView.findViewById<TextView>(R.id.tv_time)
        var title: TextView = itemView.findViewById<TextView>(R.id.tv_title)
        var desc: TextView = itemView.findViewById<TextView>(R.id.tv_desc)
        var from: TextView = itemView.findViewById<TextView>(R.id.tv_from)


        init {
            mTimelineView.initLine(viewType)
        }

    }

}