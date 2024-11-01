package com.uniandes.project.abcall.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.uniandes.project.abcall.R
import com.uniandes.project.abcall.models.Incident

class IncidentDetailAdapter (private val incidentDetail: List<Incident>) : RecyclerView.Adapter<IncidentDetailAdapter.IncidentViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IncidentViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.fragment_detail_incident, parent, false)
        return IncidentViewHolder(view)
    }

    override fun getItemCount(): Int = incidentDetail.size

    override fun onBindViewHolder(holder: IncidentViewHolder, position: Int) {
        val incident = incidentDetail[position]
        holder.bind(incident)
    }

    inner class IncidentViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val asuntoTextView: TextView = view.findViewById(R.id.asuntoTextView)

        fun bind(data: Incident) {
            Log.d("AsuntoAdapter", "ASUNTO: ${data.asunto}")
            asuntoTextView.text = data.asunto
        }
    }
}