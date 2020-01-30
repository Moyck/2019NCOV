package com.moyck.ncov.adapter

import android.content.Context
import android.media.Image
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.moyck.ncov.R
import com.moyck.ncov.domain.New
import com.github.vipulasri.timelineview.TimelineView
import com.moyck.ncov.domain.DataItem
import com.moyck.ncov.domain.UrlCallback


class DatasAdapter(val data: List<DataItem>, val callback: UrlCallback) :
    RecyclerView.Adapter<DatasAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.datas_item, parent, false)
        return ViewHolder(v, viewType)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.title.text = data[position].text
        holder.icon.setImageResource(data[position].icon)
        holder.card.setOnClickListener {
            callback.onClick(data[position].url)
        }
    }


    class ViewHolder(itemView: View, viewType: Int) : RecyclerView.ViewHolder(itemView) {

        var title: TextView = itemView.findViewById<TextView>(R.id.title)
        var icon: ImageView = itemView.findViewById<ImageView>(R.id.icon)
        var card: CardView = itemView.findViewById<CardView>(R.id.card)
    }

}