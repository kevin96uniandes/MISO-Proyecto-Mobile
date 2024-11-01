package com.uniandes.project.abcall.adapter

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.uniandes.project.abcall.R
import com.uniandes.project.abcall.models.Incident
import com.uniandes.project.abcall.ui.dashboard.fragments.DetailIncidentFragment
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class IncidentsListAdapter (private val incidents: List<Incident>) : RecyclerView.Adapter<IncidentsListAdapter.SentMessageViewHolder>() {
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
            "Abierto" -> ContextCompat.getDrawable(holder.estadoTextView.context, R.drawable.estado_abierto)
            "Desestimado" -> ContextCompat.getDrawable(holder.estadoTextView.context, R.drawable.estado_desestimado)
            "Escalado" -> ContextCompat.getDrawable(holder.estadoTextView.context, R.drawable.estado_escalado)
            "Cerrado Satisfactoriamente" -> ContextCompat.getDrawable(holder.estadoTextView.context, R.drawable.estado_cerrado_satisfactorio)
            "Cerrado Insatisfactoriamente" -> ContextCompat.getDrawable(holder.estadoTextView.context, R.drawable.estado_cerrado_insatisfactorio)
            "Reaperturado" -> ContextCompat.getDrawable(holder.estadoTextView.context, R.drawable.estado_reaperturado)
            else -> null
        }
        holder.estadoTextView.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null)

        holder.detailButton.setOnClickListener {
            val bundle = Bundle()
            bundle.putInt("idIncident", incident.id)
            val detailIncidentFragment = DetailIncidentFragment()
            detailIncidentFragment.arguments = bundle

            val activity = holder.itemView.context as AppCompatActivity
            val transaction = activity.supportFragmentManager.beginTransaction()
            Log.d("BotonAdapter", "IDAdapter: ${incident.id}")
            transaction.replace(R.id.frame_layout, detailIncidentFragment)
            transaction.addToBackStack(null)
            transaction.commit()
        }
    }

    inner class SentMessageViewHolder(view: View) : RecyclerView.ViewHolder(view), View.OnClickListener {
        private val asuntoTextView: TextView = view.findViewById(R.id.asuntoTextView)
        private val fechaTextView: TextView = view.findViewById(R.id.fechaTextView)
        private val tipoTextView: TextView = view.findViewById(R.id.tipoTextView)
        val estadoTextView: TextView = view.findViewById(R.id.estadoTextView)
        val detailButton: Button = view.findViewById(R.id.detailButton)

        fun bind(data: Incident) {
            asuntoTextView.text = data.asunto
            fechaTextView.text = formatDate(data.fechaActualizacion)
            estadoTextView.text = data.estadoNombre
            tipoTextView.text = getTypeName(data.tipoId)
        }

        private fun formatDate(fecha: String): String {
            return try {
                val formatoOriginal = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS", Locale.getDefault())
                val fechaOriginal: Date? = formatoOriginal.parse(fecha)
                val formatoDeseado = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                fechaOriginal?.let { formatoDeseado.format(it) } ?: fecha
            } catch (e: Exception) {
                e.printStackTrace()
                fecha
            }
        }

        private fun getTypeName(tipoId: Int): String {
            return when (tipoId) {
                1 -> "Petición"
                2 -> "Queja/Reclamo"
                3 -> "Sugerencia"
                else -> "Desconocido"
            }
        }

        override fun onClick(p0: View?) {
            val position = adapterPosition

        }
    }
}