package com.uniandes.project.abcall.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.uniandes.project.abcall.R
import com.uniandes.project.abcall.models.History
import java.text.SimpleDateFormat
import java.util.Locale

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
        val usuarioCreadorTextView: TextView = view.findViewById(R.id.usuarioCreadorTextView)
        val observacionesTextView: TextView = view.findViewById(R.id.observacionesTextView)
        val evidenceTextView: TextView = view.findViewById(R.id.evidenceTextView)

        fun bind(data: History) {
            Log.d("AsuntoAdapter", "ASUNTO: ${data.usuarioCreador?.nombreUsuario}")
            fechaCreacionTextView.text = data.fechaCreacion
            usuarioCreadorTextView.text = data.usuarioCreador?.nombreUsuario
            observacionesTextView.text = data.observaciones

            val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS", Locale.getDefault())
            val outputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())

            try {
                val date = inputFormat.parse(data.fechaCreacion)
                fechaCreacionTextView.text = outputFormat.format(date)
            } catch (e: Exception) {
                e.printStackTrace()
                fechaCreacionTextView.text = data.fechaCreacion
            }

            if (data.evidence?.isEmpty() == true) {
                evidenceTextView.text = "No hay adjuntos"
            } else {
                evidenceTextView.text = data.evidence.toString()
            }
        }
    }
}