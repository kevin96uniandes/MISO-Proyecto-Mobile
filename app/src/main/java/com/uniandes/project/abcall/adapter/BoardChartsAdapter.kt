package com.uniandes.project.abcall.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.renderer.BarChartRenderer
import com.github.mikephil.charting.utils.ColorTemplate
import com.uniandes.project.abcall.R
import com.uniandes.project.abcall.models.ChartItem
import com.uniandes.project.abcall.ui.dashboard.fragments.MonitorFragment
import java.text.SimpleDateFormat
import java.util.Date


class BoardChartsAdapter(val items: MutableList<ChartItem>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun getItemViewType(position: Int) = position

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            0 -> BoardPercentageViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_percentage_chart, parent, false))
            1 -> PieChartViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_pie_chart, parent, false))
            else -> throw IllegalArgumentException("Invalid View Type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (position) {
            0 -> (holder as BoardPercentageViewHolder).bind(items[0] as ChartItem.PercentagesItem)
            1 -> (holder as PieChartViewHolder).bind(items[1] as ChartItem.PieChartItem)

        }
    }

    override fun getItemCount(): Int = 2

    // ViewHolder para PieChart
    class PieChartViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val pieChart: PieChart = itemView.findViewById(R.id.pieChart1)
        private val titleTextView: TextView = itemView.findViewById(R.id.titleGraphPieChart)
        fun bind(pieChartItem: ChartItem.PieChartItem) {
            titleTextView.text = pieChartItem.title

            if (pieChartItem.entries.isEmpty()) {
                pieChart.visibility = View.GONE
                titleTextView.visibility = View.GONE
            } else {
                pieChart.visibility = View.VISIBLE
                titleTextView.visibility = View.VISIBLE
                val dataSet = PieDataSet(pieChartItem.entries, pieChartItem.title)
                dataSet.setColors(*ColorTemplate.MATERIAL_COLORS)
                val pieData = PieData(dataSet)
                pieData.setDrawValues(true)
                pieChart.data = pieData
                pieChart.invalidate()
            }
        }
    }

    // ViewHolder para BarChart
/*
    class BarChartViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val barChart: BarChart = itemView.findViewById(R.id.BarChart1)

        fun bind(barChartItem: ChartItem.BarChartItem) {
            val dataSet = BarDataSet(barChartItem.entries, barChartItem.title)

            // Usar ValueFormatter para personalizar los valores mostrados sobre las barras
            dataSet.valueFormatter = object : ValueFormatter() {
                override fun getFormattedValue(value: Float): String {
                    // Asegúrate de que el índice esté dentro del rango de las entradas
                    val entry = barChartItem.entries.find { it.x == value }
                    val barCharLabels = entry?.data as MonitorFragment.BarChartLabels?
                    // Obtener el nombre del estado (que se pasa como 'data')
                    return barCharLabels?.state ?: ""
                }
            }

            val barData = BarData(dataSet)
            barChart.data = barData

            // Configuración del eje X
            val xAxis = barChart.xAxis
            xAxis.valueFormatter = object : ValueFormatter() {
                override fun getFormattedValue(value: Float): String {
                    val index = value.toInt() + 1

                    // Asegurarse de que el índice esté dentro del rango de las entradas
                    val entries = barChartItem.entries
                        val entry = barChartItem.entries.find { it.y.toInt() == index }
                        val barCharLabels = entry?.data as MonitorFragment.BarChartLabels?
                        // Obtener el nombre del estado (que se pasa como 'data')
                        return barCharLabels?.date?.keys?.first() ?: "grgger"

                }
            }

            // Configuración para el eje X
            xAxis.setDrawLabels(true)  // Habilitar las etiquetas en el eje X
            xAxis.setDrawAxisLine(false)  // No mostrar línea del eje X
            xAxis.setDrawGridLines(false) // Deshabilitar las líneas de la cuadrícula en el eje X
            xAxis.position = XAxis.XAxisPosition.BOTTOM  // Ubicar las etiquetas de las barras en la parte inferior

            // Configuración del eje Y para usar un paso de 1 unidad
            val yAxis = barChart.axisLeft
            yAxis.axisMinimum = 0f       // Establecer el valor mínimo del eje Y en 0
            yAxis.granularity = 1f       // Definir el paso de la escala del eje Y en 1
            yAxis.setGranularityEnabled(true)  // Habilitar el granularity
            yAxis.axisMaximum = 10f      // Ajusta este valor según el rango de tus datos

            // Opcional: Configurar el eje Y derecho (si lo tienes) de forma similar
            barChart.axisRight.isEnabled = false

            // Opciones para la visualización de las etiquetas encima de las barras
            dataSet.setDrawValues(true)   // Habilitar la visualización de valores sobre las barras
            dataSet.valueTextSize = 10f   // Tamaño de la fuente de los valores

            // Mostrar el gráfico de barras
            barChart.invalidate()
        }
    }
    // ViewHolder para LineChart

    class LineChartViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val lineChart: LineChart = itemView.findViewById(R.id.lineChart1)

        fun bind(lineChartItem: ChartItem.LineChartItem) {
            val dataSet = LineDataSet(lineChartItem.entries, lineChartItem.title)
            dataSet.setColors(*ColorTemplate.MATERIAL_COLORS)

            val lineData = LineData(dataSet)
            lineChart.data = lineData
             lineChart.axisLeft.valueFormatter = YAxisValueFormatter(lineChartItem.yLabels)
            lineChart.axisLeft.granularity = 1f
            lineChart.invalidate()
        }
    }
*/
    class BoardPercentageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val channelEmailText: TextView = itemView.findViewById(R.id.idChannelPercentageChartEmail)
        private val valueEmailText: TextView = itemView.findViewById(R.id.idValuePercentageChartEmail)
        private val containerEmail: LinearLayout = itemView.findViewById(R.id.idContainerEmailPercentageChart)

        private val channelCallText: TextView = itemView.findViewById(R.id.idChannelPercentageChartCall)
        private val valueCallText: TextView = itemView.findViewById(R.id.idValuePercentageChartCall)
        private val containerCall: LinearLayout = itemView.findViewById(R.id.idContainerCallPercentageChart)

        private val channelMobileText: TextView = itemView.findViewById(R.id.idChannelPercentageChartMobile)
        private val valueMobileText: TextView = itemView.findViewById(R.id.idValuePercentageChartMobile)
        private val containerMobile: LinearLayout = itemView.findViewById(R.id.idContainerMobilePercentageChart)

        private val containerGeneral: LinearLayout = itemView.findViewById(R.id.idContainerGeneralPercentageChart)


        fun bind(percentageItem: ChartItem.PercentagesItem) {
            Log.d("Board bind", percentageItem.toString())


            valueCallText.text = buildString {
                append(0)
                append(" %")
            }
            valueEmailText.text = buildString {
                append(0)
                append(" %")
            }
            valueMobileText.text = buildString {
                append(0)
                append(" %")
            }
            percentageItem.entries.forEach { entry ->

                if (entry.channel.lowercase().contains("correo")) {
                    valueEmailText.text = buildString {
                        append(entry.value.toString())
                        append(" %")
                    }
                }else if (entry.channel.lowercase().contains("llamada")){
                    valueCallText.text = buildString {
                        append(entry.value.toString())
                        append(" %")
                    }
                }else if (entry.channel.lowercase().contains("app")) {
                    valueMobileText.text = buildString {
                        append(entry.value.toString())
                        append(" %")
                    }
                }
            }
        }
    }
}

class YAxisValueFormatter(val labels: List<String>) : ValueFormatter() {
    override fun getFormattedValue(value: Float): String {
        return if (value.toInt() in labels.indices) {
            labels[value.toInt()]
        } else {
            value.toString()
        }
    }
}