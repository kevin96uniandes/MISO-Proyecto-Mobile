package com.uniandes.project.abcall.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.uniandes.project.abcall.R
import java.io.File

class SelectedFilesAdapter(
    private val files: MutableList<File>, // Cambiamos a MutableList para permitir eliminar elementos
    private val onRemoveFile: (File) -> Unit // Callback para eliminar un archivo
) : RecyclerView.Adapter<SelectedFilesAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val fileName: TextView = view.findViewById(R.id.is_tv_file_name)
        val deleteButton: ImageButton = view.findViewById(R.id.btn_delete_file)

        fun bind(file: File) {
            fileName.text = file.name // Mostrar el nombre del archivo

            deleteButton.setOnClickListener {
                onRemoveFile(file) // Llama al callback para eliminar el archivo
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    files.removeAt(position) // Eliminar el archivo de la lista
                    notifyItemRemoved(position) // Notificar al adaptador que el ítem fue removido
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_selected_file, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(files[position])
    }

    override fun getItemCount(): Int = files.size
}

