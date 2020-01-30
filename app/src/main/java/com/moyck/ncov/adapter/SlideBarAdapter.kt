package com.moyck.ncov.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.moyck.ncov.R
import com.moyck.ncov.domain.SlideItem
import org.w3c.dom.Text

class SlideBarAdapter(val context: Context, val data: ArrayList<SlideItem>) : BaseAdapter() {


    override fun getView(p0: Int, convertView: View?, p2: ViewGroup?): View {
        val viewHolder: ViewHolder
        var view = convertView
        if (view == null) {
            viewHolder = ViewHolder()
            view = LayoutInflater.from(context).inflate(R.layout.slide_item, null);
            viewHolder.icon = view!!.findViewById(R.id.icon)
            viewHolder.title = view!!.findViewById(R.id.title)
            view.setTag(viewHolder)
        } else {
            viewHolder = convertView!!.tag as ViewHolder
        }
        viewHolder.title.text = data[p0].text
        viewHolder.icon.setImageResource(data[p0].icon)
        return view
    }

    override fun getItem(p0: Int): Any {
        return data[p0]
    }

    override fun getItemId(p0: Int): Long {
        return p0.toLong()
    }

    override fun getCount(): Int {
        return data.size
    }


    class ViewHolder {

        lateinit var title: TextView
        lateinit var icon: ImageView
    }

}