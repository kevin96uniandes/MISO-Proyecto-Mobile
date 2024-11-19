package com.uniandes.project.abcall.models

import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.PieEntry

sealed class ChartItem {
    data class PieChartItem(val entries: List<PieEntry>, val title: String) : ChartItem()
    //data class BarChartItem(val entries: List<BarEntry>, val title: String) : ChartItem()
    //data class LineChartItem(val entries: List<Entry>, val yLabels: List<String>, val title: String, ) : ChartItem()
    data class PercentagesItem(val entries: List<BoardPercentage>) : ChartItem()

}