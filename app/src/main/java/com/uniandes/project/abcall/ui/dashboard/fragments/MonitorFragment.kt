package com.uniandes.project.abcall.ui.dashboard.fragments

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.utils.ColorTemplate
import com.google.android.material.textfield.TextInputEditText
import com.uniandes.project.abcall.R
import com.uniandes.project.abcall.adapter.BoardChartsAdapter
import com.uniandes.project.abcall.config.ApiResult
import com.uniandes.project.abcall.databinding.FragmentMonitorBinding
import com.uniandes.project.abcall.enums.IncidenceStatus
import com.uniandes.project.abcall.enums.Technology
import com.uniandes.project.abcall.getDateStringFromCalendar
import com.uniandes.project.abcall.models.Board
import com.uniandes.project.abcall.models.BoardPercentage
import com.uniandes.project.abcall.models.ChartItem
import com.uniandes.project.abcall.repositories.rest.BoardPercentageClient
import com.uniandes.project.abcall.repositories.rest.BoardSummaryClient
import com.uniandes.project.abcall.ui.dashboard.intefaces.FragmentChangeListener
import com.uniandes.project.abcall.viewmodels.BoardPercentageViewModel
import com.uniandes.project.abcall.viewmodels.BoardSummaryViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


class MonitorFragment : Fragment() {

    private lateinit var pieChart: PieChart
    private lateinit var filterStartDateEt: TextInputEditText
    private lateinit var filterEndDateEt: TextInputEditText
    private lateinit var filterChannelEt: TextInputEditText
    private lateinit var filterStateEt: TextInputEditText

    private lateinit var filterStartDateData: Calendar
    private lateinit var filterEndDateData: Calendar

    private var _binding: FragmentMonitorBinding? = null
    private val binding get() = _binding!!

    private var fragmentChangeListener: FragmentChangeListener? = null

    private lateinit var boardSummaryViewModel: BoardSummaryViewModel
    private val boardSummaryClient = BoardSummaryClient()

    private lateinit var boardPercentageViewModel: BoardPercentageViewModel
    private val boardPercentageClient = BoardPercentageClient()

    private lateinit var recyclerViewContainer: RecyclerView
    private lateinit var adapter: BoardChartsAdapter

    private val chartItems = mutableListOf<ChartItem>()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is FragmentChangeListener) {
            fragmentChangeListener = context
            context.getString(R.string.monitor)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentMonitorBinding.inflate(inflater, container, false)
        filterStartDateEt = binding.filterStartDateEt
        filterEndDateEt = binding.filterEndDateEt
        filterChannelEt = binding.filterChannelEt
        filterStateEt = binding.filterStateEt
        recyclerViewContainer = binding.graphBoardRecyclerView

        setupRecyclerView()

        boardSummaryViewModel = BoardSummaryViewModel(boardSummaryClient)
        boardPercentageViewModel = BoardPercentageViewModel(boardPercentageClient)

        recyclerViewContainer.layoutManager = LinearLayoutManager(binding.root.context)

        filterStartDateEt.setOnClickListener {
            showDatePickerDialog { calendar ->
                if (isValidDateRange(calendar, filterEndDateData)) {
                    filterStartDateData = calendar
                    val date = "${calendar.get(Calendar.DAY_OF_MONTH)}/${calendar.get(Calendar.MONTH) + 1}/${calendar.get(Calendar.YEAR)}"
                    filterStartDateEt.setText(date)
                    getBoardSummary()
                    getBoardPercentage()
                }
            }
        }

        filterEndDateEt.setOnClickListener {
            showDatePickerDialog { calendar ->
                if (isValidDateRange(filterStartDateData, calendar)){
                    filterEndDateData = calendar
                    val date = "${calendar.get(Calendar.DAY_OF_MONTH)}/${calendar.get(Calendar.MONTH) + 1}/${calendar.get(Calendar.YEAR)}"
                    filterEndDateEt.setText(date)
                    getBoardSummary()
                    getBoardPercentage()
                }

            }
        }

        filterChannelEt.setOnClickListener {
            val items = (listOf(getString(R.string.all)) + Technology.entries.map { it.channel }).toTypedArray()
            val builder = AlertDialog.Builder(binding.root.context)
            builder.setTitle(getString(R.string.select_channel))
                .setSingleChoiceItems(items, -1) { dialog, which ->
                    val selectedItem = items[which]
                    filterChannelEt.setText(selectedItem)
                    getBoardSummary()
                    getBoardPercentage()
                    dialog.dismiss()
                }
                .setNegativeButton(getString(R.string.cancel)) { dialog, _ ->
                    dialog.dismiss()
                }
            builder.show()
        }

        filterStateEt.setOnClickListener {
            val items = (listOf(getString(R.string.all)) + IncidenceStatus.entries.map { it.status }).toTypedArray()
            val builder = AlertDialog.Builder(binding.root.context)
            builder.setTitle(getString(R.string.select_state))
                .setSingleChoiceItems(items, -1) { dialog, which ->
                    val selectedItem = items[which]
                    filterStateEt.setText(selectedItem)
                    getBoardSummary()
                    getBoardPercentage()
                    dialog.dismiss()
                }
                .setNegativeButton(getString(R.string.cancel)) { dialog, _ ->
                    dialog.dismiss()
                }
            builder.show()
        }
        boardSummaryViewModelObserver()
        boardPercentageViewModelObserver()

        filtersInitialValues()
        getBoardPercentage()
        getBoardSummary()

        return binding.root
    }

    private fun showDatePickerDialog(onDateSet: (Calendar) -> Unit) {
        val calendar = Calendar.getInstance()
        val datePickerDialog = DatePickerDialog(binding.root.context, { _, year, month, dayOfMonth ->
            val selectedDate = Calendar.getInstance()
            selectedDate.set(year, month, dayOfMonth, 0,0,0)
            onDateSet(selectedDate)
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH))

        datePickerDialog.show()
    }

    private fun filtersInitialValues() {
        val calendar = Calendar.getInstance()
        val date = "${calendar.get(Calendar.DAY_OF_MONTH)}/${calendar.get(Calendar.MONTH) + 1}/${calendar.get(Calendar.YEAR)}"
        filterEndDateEt.setText(date)
        filterStartDateEt.setText(date)
        filterEndDateData = calendar
        filterStartDateData = calendar

        val status = IncidenceStatus.entries.first()
        filterStateEt.setText(status.status)

        val channel = Technology.entries.first()
        filterChannelEt.setText(channel.channel)
    }

    fun isValidDateRange(startDate: Calendar, endDate: Calendar): Boolean {
        val currentDate = Calendar.getInstance()

        return when {
            startDate.after(endDate) -> {
                Toast.makeText(
                    binding.root.context,
                    getString(R.string.start_date_cannot_be_greater_than_end_date),
                    Toast.LENGTH_SHORT
                ).show()
                false
            }

            startDate.after(currentDate) || endDate.after(currentDate) -> {
                Toast.makeText(
                    binding.root.context,
                    getString(R.string.dates_cannot_exceed_current_date),
                    Toast.LENGTH_SHORT
                ).show()
                false
            }

            else -> true
        }
    }

    fun fillBoardObject(): Board =
        Board(
            channelId = Technology.ordinalFromChannel(
                filterChannelEt.text.toString()
            ),
            stateId = IncidenceStatus.ordinalFromChannel(
                filterStateEt.text.toString()
            ),
            startDate = getDateStringFromCalendar(filterStartDateData),
            endDate = getDateStringFromCalendar(filterEndDateData)
        )

    fun getBoardSummary() {
        boardSummaryViewModel.getBoardSummary(fillBoardObject())
    }

    fun getBoardPercentage() {
        boardPercentageViewModel.getBoardPercentage(fillBoardObject())
    }

    fun boardSummaryViewModelObserver() {
        boardSummaryViewModel.result.observe(viewLifecycleOwner) { result ->
            when (result) {
                is ApiResult.Success -> {
                    Log.d("Monitor summary", result.data.toString())
                    fillCharts(result.data)
                }
                is ApiResult.Error -> {}
                is ApiResult.NetworkError -> {}
            }
        }
    }

    fun boardPercentageViewModelObserver() {
        boardPercentageViewModel.result.observe(viewLifecycleOwner) { result ->
            when (result) {
                is ApiResult.Success -> {
                    Log.d("Monitor percentage", result.data.toString())
                    fillPercentages(result.data)
                }
                is ApiResult.Error -> {}
                is ApiResult.NetworkError -> {}
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun fillCharts(data: BoardSummaryClient.BoardSummaryResponse) {
        val dataLabelsDate = mutableSetOf<String>()
        data.incidences.map { dataLabelsDate.add(it.createDate) }
        val estateCounts = data.incidences.groupingBy { it.estate }.eachCount()

        val pieEntries = estateCounts.map {
            PieEntry(it.value.toFloat(), it.key)
        }
        Log.d("pie chart Monitor", pieEntries.toString())
        val groupedByState = data.incidences.groupBy { it.estate }
        val lineDataSets = mutableListOf<LineDataSet>()

        var lineIndex = 0
        groupedByState.forEach { (estado, items) ->
            val entries = items.mapIndexed { i, item ->
                // Aquí se agrega un Entry por cada Incidence
                Entry(i.toFloat(), item.id.toFloat())
            }

            val lineDataSet = LineDataSet(entries, estado)
            lineDataSets.add(lineDataSet)

            lineIndex++  // Incrementar el índice para el siguiente grupo de estado
        }

        val items = adapter.items // Accede a la lista del adaptador
        items[1] = ChartItem.PieChartItem(entries = pieEntries, title = getString(R.string.incident_distribution_by_status))
        recyclerViewContainer.adapter?.notifyDataSetChanged()
        adapter.notifyItemChanged(1)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun fillPercentages(percentage: BoardPercentageClient.BoardPercentageResponse) {
        val percentages = percentage.channels.map {BoardPercentage(
            channel = it.channel,
            value = it.value.toInt()
        )}
           val chartItem = ChartItem.PercentagesItem(entries = percentages)

        adapter.items[0] = chartItem
        adapter.notifyItemChanged(0) // Notifica que la posición 1 cambió
    }

    fun setupRecyclerView() {
        // Crear una lista inicial con dos elementos
        val initialItems = mutableListOf(
            ChartItem.PercentagesItem(entries = listOf()),
            ChartItem.PieChartItem(entries = listOf(), title = "Pie Chart")
        )

        // Inicializar el adaptador con esta lista inicial
        adapter = BoardChartsAdapter(initialItems)

        // Asignar el adaptador al RecyclerView
        recyclerViewContainer.adapter = adapter
        recyclerViewContainer.setHasFixedSize(true) // Optimización para tamaños fijos
    }

    companion object {
        const val TITLE = ""
        @JvmStatic
        fun newInstance() =
            MonitorFragment()
    }

}