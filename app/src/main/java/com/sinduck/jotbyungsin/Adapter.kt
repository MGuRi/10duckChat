package com.sinduck.jotbyungsin

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import kotlinx.android.synthetic.main.rv_layout.view.*


class Adapter(val mMessagesData: ArrayList<MessagesData>) : RecyclerView.Adapter<Adapter.Holder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        return Holder(
            LayoutInflater.from(parent.context).inflate(R.layout.rv_layout, parent, false)

        )
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val data: MessagesData = mMessagesData[position]
        holder.heading.text = data.heading
        holder.messages.text = data.messages
        holder.time.text = data.time
    }

    override fun getItemCount() = mMessagesData.size

    inner class Holder(itemView: View) : ViewHolder(itemView) {
        var heading: TextView = itemView.heading
        var messages: TextView = itemView.messageBody
        var time: TextView = itemView.item_date
    }
}