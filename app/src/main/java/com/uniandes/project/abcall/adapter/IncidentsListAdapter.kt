package com.uniandes.project.abcall.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView.OnItemClickListener
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.uniandes.project.abcall.R
import com.uniandes.project.abcall.models.Incident

class IncidentsListAdapter (private val incidents: List<Incident>, private val listener: OnItemClickListener) : RecyclerView.Adapter<IncidentsListAdapter.SentMessageViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SentMessageViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.item_incident_list, parent, false)
        return SentMessageViewHolder(view)

    }

    override fun getItemCount(): Int = incidents.size

    override fun onBindViewHolder(holder: SentMessageViewHolder, position: Int) {
        val incident = incidents[position]
        holder.bind(incident)
        val drawable = when (incident.estadoNombre){
            "Abierto" -> ContextCompat.getDrawable(holder.tipoTextView.context, R.drawable.estado_cerrado_satisfactorio)
            else -> ContextCompat.getDrawable(holder.tipoTextView.context, R.drawable.estado_escalado)
        }
        holder.tipoTextView.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null)
    }

    inner class SentMessageViewHolder(view: View) : RecyclerView.ViewHolder(view), View.OnClickListener {
        private val asuntoTextView: TextView = view.findViewById(R.id.asuntoTextView)
        private val fechaTextView: TextView = view.findViewById(R.id.fechaTextView)
        val tipoTextView: TextView = view.findViewById(R.id.tipoTextView)
        private val estadoTextView: TextView = view.findViewById(R.id.estadoTextView)

        fun bind(data: Incident) {
            asuntoTextView.text = data.asunto
            fechaTextView.text = data.fechaCreacion
            estadoTextView.text = data.estadoId.toString()
        }

        override fun onClick(p0: View?) {
            val position = adapterPosition

        }
    }
}