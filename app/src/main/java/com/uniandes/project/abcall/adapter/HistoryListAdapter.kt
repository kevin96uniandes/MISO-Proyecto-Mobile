package com.uniandes.project.abcall.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.uniandes.project.abcall.R
import com.uniandes.project.abcall.models.History

class HistoryListAdapter (private val historyList: List<History>) : RecyclerView.Adapter<HistoryListAdapter.HistoryViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.item_process, parent, false)
        return HistoryViewHolder(view)
    }

    override fun getItemCount(): Int = historyList.size

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        val history = historyList[position]
        holder.bind(history)
    }

    inner class HistoryViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val fechaCreacionTextView: TextView = view.findViewById(R.id.fechaCreacionTextView)

        fun bind(data: History) {
            Log.d("AsuntoAdapter", "ASUNTO: ${data.fechaCreacion}")
            fechaCreacionTextView.text = data.fechaCreacion
        }
    }
}