package ch.heigvd.scanalyze.activities

import android.content.res.ColorStateList
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import ch.heigvd.scanalyze.R
import ch.heigvd.scanalyze.Utils.Utils
import ch.heigvd.scanalyze.api.Api
import ch.heigvd.scanalyze.api.ApiCallback
import ch.heigvd.scanalyze.databinding.ActivityStatsBinding
import ch.heigvd.scanalyze.fragments.DateRangePickerFragment
import ch.heigvd.scanalyze.fragments.OnDateRangeSelectedListener
import ch.heigvd.scanalyze.fragments.OnYearSelectedListener
import ch.heigvd.scanalyze.fragments.YearPickerFragment
import ch.heigvd.scanalyze.receipt.Receipt
import ch.heigvd.scanalyze.api.ApiResponse
import ch.heigvd.scanalyze.statistics.TimeUnit
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.PercentFormatter
import com.google.android.material.button.MaterialButton
import com.google.gson.Gson
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.WeekFields

class StatsActivity : AppCompatActivity(), OnDateRangeSelectedListener, OnYearSelectedListener {
    private lateinit var binding: ActivityStatsBinding
    private lateinit var begin: LocalDateTime
    private lateinit var end: LocalDateTime
    private lateinit var activityTimeUnit: TimeUnit
    private lateinit var statisticsData: ApiResponse
    private lateinit var aggregatedData: Map<String, Float>
    private lateinit var categoryData: Map<String, Float>
    private lateinit var gson: Gson

    private val monthNames = arrayOf(
        "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"
    )

    // API callback implementation
    private val callback = object : ApiCallback {
        override fun onSuccess(response: String) {
            // parse the received json
            statisticsData = gson.fromJson(response, ApiResponse::class.java)

            // Once we have the data, initialize the graph
            runOnUiThread {
                //Aggregate data and show graph
                updateGraph(activityTimeUnit)
            }
        }

        override fun onFailure(errorMessage: String) {
            runOnUiThread { Utils.showErrorDialog(errorMessage, this@StatsActivity) }
        }
    }

    override fun onDateRangeSelected(dateRange: Pair<LocalDateTime, LocalDateTime>) {
        updateAfterDatePick("DateRangePicker", dateRange)
    }

    override fun onYearSelected(yearRange: Pair<LocalDateTime, LocalDateTime>) {
        updateAfterDatePick("YearPickerFragment", yearRange)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //Get the binding with the UI layout
        binding = ActivityStatsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Init variables and default page behavior
        initialization()

        // Fetch the data from the API
        getStatData(callback)
    }

    private fun initialization() {

        gson = Gson()

        // Init the buttons listeners
        with(binding) {
            buttonWeek.setOnClickListener { updateDataForTimeUnit(TimeUnit.WEEK) }
            buttonMonth.setOnClickListener { updateDataForTimeUnit(TimeUnit.MONTH) }
            buttonYear.setOnClickListener { updateDataForTimeUnit(TimeUnit.YEAR) }
            buttonInterval.setOnClickListener { showDatePicker(activityTimeUnit) }
        }

        //Init the default date filter
        activityTimeUnit = TimeUnit.MONTH

        //Initialize the begin-end variables
        reinitializeDates(activityTimeUnit)

        //Select the month button
        updateButtonsBackgnd(activityTimeUnit)
    }

    private fun updateDataForTimeUnit(timeUnit: TimeUnit) {
        activityTimeUnit = timeUnit
        reinitializeDates(activityTimeUnit)
        updateButtonsBackgnd(activityTimeUnit)
        getStatData(callback)
    }

    private fun updateGraph(timeUnit: TimeUnit) {
        aggregatedData = when (timeUnit) {
            TimeUnit.WEEK -> aggregateDataForWeek(statisticsData.receipts, begin, end)
            TimeUnit.MONTH -> aggregateDataForMonth(statisticsData.receipts, begin, end)
            TimeUnit.YEAR -> aggregateDataForYears(statisticsData.receipts)
        }

        categoryData = mutableMapOf()
        if (statisticsData.totalCategory != null) {
            categoryData = statisticsData.totalCategory
        }

        formatGraph(aggregatedData)
        updatePieChart(categoryData)
    }

    private fun showDatePicker(timeUnit: TimeUnit) {
        when (timeUnit) {
            TimeUnit.WEEK -> {
                val drpf = DateRangePickerFragment.newInstance()
                drpf.mListener = this
                drpf.show(supportFragmentManager, "DateRangePicker")
            }

            TimeUnit.MONTH -> {
                // Get the last 9 years to be displayed on the fragment
                val availableYears = ArrayList(List(9) { i -> LocalDateTime.now().year - i })
                val ypf = YearPickerFragment.newInstance(availableYears)
                ypf.mListener = this
                ypf.show(supportFragmentManager, "YearPickerFragment")
            }

            TimeUnit.YEAR -> return
        }
    }

    private fun updateAfterDatePick(tag: String, yearRange: Pair<LocalDateTime, LocalDateTime>) {
        Log.d("Scanalyze", "Selected year: ${yearRange.first} to ${yearRange.second}")
        begin = yearRange.first
        end = yearRange.second

        //Update the dates on the screen
        with(binding) {
            textViewTimeIntervalBegin.text = begin.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))
            textViewTimeIntervalEnd.text = end.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))
        }

        //Fetch the API
        getStatData(callback)

        //Close the fragment
        val fragment = supportFragmentManager.findFragmentByTag(tag)
        if (fragment != null) {
            val dialogFragment = fragment as DialogFragment
            dialogFragment.dismiss()
        }
    }

    private fun updateButtonsBackgnd(timeUnit: TimeUnit) {

        with(binding) {
            buttonWeek.setBackgnd(R.color.scanalyze_grey)
            buttonMonth.setBackgnd(R.color.scanalyze_grey)
            buttonYear.setBackgnd(R.color.scanalyze_grey)

            when (timeUnit) {
                TimeUnit.WEEK -> buttonWeek.setBackgnd(R.color.scanalyze_purple)
                TimeUnit.MONTH -> buttonMonth.setBackgnd(R.color.scanalyze_purple)
                TimeUnit.YEAR -> buttonYear.setBackgnd(R.color.scanalyze_purple)
            }
        }
    }

    private fun reinitializeDates(timeUnit: TimeUnit) {
        end = LocalDateTime.now()
        begin = when (timeUnit) {
            TimeUnit.WEEK -> end.minusWeeks(12) // Take the last 12 weeks
            TimeUnit.MONTH -> {
                // Take the last year
                LocalDateTime.of(end.year, end.month, 1, 0, 0, 0).minusMonths(11)
            }

            TimeUnit.YEAR -> {end.minusYears(10)} // Take the last 10 years
        }
        val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")
        binding.textViewTimeIntervalBegin.text = begin.format(formatter)
        binding.textViewTimeIntervalEnd.text = end.format(formatter)
    }

    private fun MaterialButton.setBackgnd(backgroundColor: Int) {
        // Get the color from resources
        val color = ContextCompat.getColor(this@StatsActivity, backgroundColor)

        // Create a ColorStateList
        val colorStateList = ColorStateList.valueOf(color)

        // Set the background color
        this.backgroundTintList = colorStateList
    }

    private fun getStatData(callback: ApiCallback) {

        var beginApiParam = ""
        var endApiParam = ""

        try {
            val apiDateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")
            beginApiParam = begin.format(apiDateFormatter)
            endApiParam = end.format(apiDateFormatter)
        } catch (e: Exception) {
            Utils.showErrorDialog(e, this@StatsActivity)
        }

        Api.getStats(beginApiParam, endApiParam, callback, this)
    }

    private fun aggregateDataForYears(data: List<Receipt>?): Map<String, Float> {
        val aggregatedData = mutableMapOf<String, Float>()

        if (data != null) {
            for (d in data) {
                val dDate = LocalDateTime.parse(d.date, DateTimeFormatter.ISO_DATE_TIME)
                val key = dDate.year.toString()
                val total = d.total ?: 0f
                aggregatedData[key] = aggregatedData.getOrDefault(key, 0f) + total
            }
        }
        return aggregatedData.keys.sortedBy { it.toInt() }.associateWith { aggregatedData[it]!! }
    }

    private fun aggregateDataForMonth(
        data: List<Receipt>?, begin: LocalDateTime, end: LocalDateTime
    ): Map<String, Float> {
        val aggregatedData = mutableMapOf<String, Float>()

        // Generate month keys based on the date range
        var current = begin
        while (current.isBefore(end) || current.isEqual(end)) {
            val month = monthNames[current.monthValue - 1]
            aggregatedData[month] = 0.0f
            current = current.plusMonths(1)
        }
        if (data != null) {
            for (receipt in data) {
                val receiptDate =
                    LocalDateTime.parse(receipt.date, DateTimeFormatter.ISO_DATE_TIME)
                if ((receiptDate.isAfter(begin) || receiptDate.isEqual(begin))
                    && (receiptDate.isBefore(end) || receiptDate.isEqual(end))) {

                    val month = monthNames[receiptDate.monthValue - 1]
                    val total = receipt.total ?: 0f
                    aggregatedData[month] = aggregatedData.getOrDefault(month, 0.0f) + total
                }
            }
        }

        return aggregatedData
    }

    private fun aggregateDataForWeek(
        data: List<Receipt>?,
        begin: LocalDateTime,
        end: LocalDateTime
    ): Map<String, Float> {
        val aggregatedData = mutableMapOf<String, Float>()

        // Initialize weeks with 0.0 based on the range between startWeek and endWeek
        // Handle the year transition
        var current = begin
        while (!current.isAfter(end)) {
            val week = current.get(WeekFields.ISO.weekOfWeekBasedYear())
            val key = "$week"
            aggregatedData[key] = 0.0f

            // Advance to the next week
            current = current.plusWeeks(1)
        }

        // Aggregate data
        if (data != null) {
            for (receipt in data) {
                val receiptDate =
                    LocalDateTime.parse(receipt.date, DateTimeFormatter.ISO_DATE_TIME)
                if (receiptDate.isAfter(begin) && receiptDate.isBefore(end)) {
                    val week = receiptDate.get(WeekFields.ISO.weekOfWeekBasedYear())
                    val key = "$week"
                    val total = receipt.total ?: 0f
                    aggregatedData[key] = aggregatedData.getOrDefault(key, 0.0f) + total
                }
            }
        }
        return aggregatedData
    }

    private fun formatGraph(aggregatedData: Map<String, Float>) {
        val barEntries = ArrayList<BarEntry>()
        var totalInterval = 0.0f
        val labels = mutableListOf<String>()
        for ((index, entry) in aggregatedData.toList().withIndex()) {
            // Use index as x-value, entry.second as y-value
            barEntries.add(BarEntry(index.toFloat(), entry.second))
            labels.add(entry.first)
            // Add the accumulated spending amount
            totalInterval += entry.second
        }

        //Init data set
        val barDataSet = BarDataSet(barEntries, "Spending")

        // format the dataset
        barDataSet.color = resources.getColor(R.color.scanalyze_purple)
        barDataSet.valueTextColor = Color.WHITE
        barDataSet.valueTextSize = 10f
        val barData = BarData(barDataSet)

        with(binding) {

            //Format the UI
            textViewTotal.text = String.format("%.2f", totalInterval)
            barChartSpending.xAxis.valueFormatter = IndexAxisValueFormatter(labels)
            textViewChartDescr.text = "Spending by ${activityTimeUnit.name.lowercase()}"
            barChartSpending.axisRight.isEnabled = false
            barChartSpending.xAxis.setDrawGridLines(false)
            barChartSpending.legend.isEnabled = false
            barChartSpending.description.text = ""
            barChartSpending.description.textColor = Color.WHITE

            // Set label color
            val xAxis = barChartSpending.xAxis
            xAxis.textColor = Color.WHITE
            barChartSpending.axisLeft.textColor = Color.WHITE

            // To display all the labels on the axis
            xAxis.granularity = 1f

            barChartSpending.data = barData
            barChartSpending.invalidate()  // refresh the chart
        }
    }

    private fun updatePieChart(totalCategory: Map<String, Float>) {
        val pieEntries = ArrayList<PieEntry>()

        for ((key, value) in totalCategory) {
            pieEntries.add(PieEntry(value, key))
        }

        // Init the dataset
        val pieDataSet = PieDataSet(pieEntries, "")
        val pieData = PieData(pieDataSet)

        // format dataset
        pieDataSet.colors.add(Color.parseColor("#D0005F"))
        pieDataSet.colors.add(Color.parseColor("#DE4F45"))
        pieDataSet.colors.add(Color.parseColor("#49C3FB"))
        pieDataSet.colors.add(Color.parseColor("#65A6FA"))
        pieDataSet.colors.add(Color.parseColor("#7E80E7"))
        pieDataSet.valueTextSize = 22f
        pieDataSet.valueTextColor = Color.BLACK

        // format chart
        with(binding) {
            pieChartCategories.holeRadius = 30f
            pieChartCategories.setHoleColor(Color.TRANSPARENT)
            pieChartCategories.transparentCircleRadius = 58f
            pieChartCategories.legend.isEnabled = true
            pieChartCategories.holeRadius = 54f
            pieChartCategories.legend.textColor = Color.WHITE
            pieChartCategories.setDrawEntryLabels(false)
            pieChartCategories.legend.textSize = 12f
            pieChartCategories.setUsePercentValues(true)
            pieChartCategories.legend.formSize = 12f
            pieChartCategories.description.text = ""
            pieChartCategories.data = pieData
            pieChartCategories.invalidate()
        }
    }
}
