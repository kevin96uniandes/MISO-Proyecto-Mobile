package com.uniandes.project.abcall.adapter

import android.content.Context
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.uniandes.project.abcall.R
import com.uniandes.project.abcall.models.History
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import java.text.SimpleDateFormat
import java.util.Locale
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.URLEncoder

class HistoryListAdapter (private val context: Context, private val historyList: List<History>) : RecyclerView.Adapter<HistoryListAdapter.HistoryViewHolder>() {
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
                evidenceTextView.text = data.evidence?.joinToString(", ") { it.nombreAdjunto }
                evidenceTextView.setOnClickListener {
                    data.evidence?.forEach { evidence ->
                        val fileUrl = "https://storage.googleapis.com/abcall-bucket/incident-files/${evidence.nombreAdjunto}"
                        downloadFile(context, evidence.nombreAdjunto, fileUrl)
                    }
                }
            }
        }

        private fun downloadFile(context: Context, fileName: String, fileUrl: String) {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val encodedFileName = URLEncoder.encode(fileName, "UTF-8")
                    val completeFileUrl = "https://storage.googleapis.com/abcall-bucket/incident-files/$encodedFileName"

                    Log.d("DownloadFile", "Attempting to download from URL: $completeFileUrl")

                    val url = URL(completeFileUrl)
                    val connection: HttpURLConnection = url.openConnection() as HttpURLConnection
                    connection.requestMethod = "GET"
                    connection.doOutput = false
                    connection.connect()

                    if (connection.responseCode == HttpURLConnection.HTTP_OK) {
                        val file = File(context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), fileName)
                        val inputStream: InputStream = connection.inputStream
                        val fileOutputStream = FileOutputStream(file)
                        val buffer = ByteArray(1024)
                        var len: Int

                        while (inputStream.read(buffer).also { len = it } > 0) {
                            fileOutputStream.write(buffer, 0, len)
                        }

                        fileOutputStream.close()
                        inputStream.close()

                        withContext(Dispatchers.Main) {
                            Toast.makeText(context, "Archivo descargado en: ${file.absolutePath}", Toast.LENGTH_LONG).show()
                        }
                    } else {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(context, "Error al descargar el archivo: Código de respuesta ${connection.responseCode}", Toast.LENGTH_SHORT).show()
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "Error al descargar el archivo", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
}