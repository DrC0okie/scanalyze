package ch.heigvd.scanalyze.activities

import android.content.res.ColorStateList
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
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
import ch.heigvd.scanalyze.receipt.JsonReceipt
import ch.heigvd.scanalyze.api.ApiResponse
import ch.heigvd.scanalyze.statistics.TimeUnit
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import com.google.android.material.button.MaterialButton
import com.google.gson.Gson
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.WeekFields

class StatsActivity : AppCompatActivity(), OnDateRangeSelectedListener, OnYearSelectedListener {
    private lateinit var binding: ActivityStatsBinding
    private lateinit var barChart: BarChart
    private lateinit var pieChart: PieChart
    private lateinit var weekButton: MaterialButton
    private lateinit var monthButton: MaterialButton
    private lateinit var yearButton: MaterialButton
    private lateinit var intervalButton: Button
    private lateinit var totalTextView: TextView
    private lateinit var barChartDescr: TextView
    private lateinit var beginTextView: TextView
    private lateinit var endTextView: TextView
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

    // define the API callback implementation
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
        Log.d("Scanalyze", "Selected range: ${dateRange.first} to ${dateRange.second}")
        begin = dateRange.first
        end = dateRange.second
        beginTextView.text = begin.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))
        endTextView.text = end.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))

        //Update the graph with the newly fetched API data
        getStatData(callback)

        // Close the ch.heigvd.scanalyze.fragments.DateRangePickerFragment
        val fragment = supportFragmentManager.findFragmentByTag("DateRangePicker")
        if (fragment != null) {
            val dialogFragment = fragment as DialogFragment
            dialogFragment.dismiss()
        }
    }

    override fun onYearSelected(yearRange: Pair<LocalDateTime, LocalDateTime>) {
        Log.d("Scanalyze", "Selected year: ${yearRange.first} to ${yearRange.second}")
        begin = yearRange.first
        end = yearRange.second
        beginTextView.text = begin.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))
        endTextView.text = end.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))

        //Update the graph with the newly fetched API data
        getStatData(callback)

        // Close the fragment
        val fragment = supportFragmentManager.findFragmentByTag("YearPickerFragment")
        if (fragment != null) {
            val dialogFragment = fragment as DialogFragment
            dialogFragment.dismiss()
        }
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

        // Init the layout elements binding
        barChart = binding.barChartSpending
        pieChart = binding.pieChartCategories
        weekButton = binding.buttonWeek
        monthButton = binding.buttonMonth
        yearButton = binding.buttonYear
        intervalButton = binding.buttonInterval
        totalTextView = binding.textViewTotal
        barChartDescr = binding.textViewChartDescr
        beginTextView = binding.textViewTimeIntervalBegin
        endTextView = binding.textViewTimeIntervalEnd
        gson = Gson()

        // Init the buttons listeners
        weekButton.setOnClickListener {
            activityTimeUnit = TimeUnit.WEEK
            reinitializeDates(activityTimeUnit)
            updateButtonsBackgnd(activityTimeUnit)
            getStatData(callback)
        }

        monthButton.setOnClickListener {
            activityTimeUnit = TimeUnit.MONTH
            reinitializeDates(activityTimeUnit)
            updateButtonsBackgnd(activityTimeUnit)
            getStatData(callback)
        }

        yearButton.setOnClickListener {
            activityTimeUnit = TimeUnit.YEAR
            reinitializeDates(activityTimeUnit)
            updateButtonsBackgnd(activityTimeUnit)
            getStatData(callback)
        }

        intervalButton.setOnClickListener {
            showDatePicker(activityTimeUnit)
        }

        //Init the filter to Month
        activityTimeUnit = TimeUnit.MONTH

        //Initialize the begin-end variables
        reinitializeDates(activityTimeUnit)

        //Select the month button
        updateButtonsBackgnd(activityTimeUnit)
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
                val ypf =
                    YearPickerFragment.newInstance(availableYears)
                ypf.mListener = this
                ypf.show(supportFragmentManager, "YearPickerFragment")
            }

            TimeUnit.YEAR -> return
        }
    }

    private fun updateButtonsBackgnd(timeUnit: TimeUnit) {

        weekButton.setBackgnd(R.color.scanalyze_grey)
        monthButton.setBackgnd(R.color.scanalyze_grey)
        yearButton.setBackgnd(R.color.scanalyze_grey)

        when (timeUnit) {
            TimeUnit.WEEK -> weekButton.setBackgnd(R.color.scanalyze_purple)
            TimeUnit.MONTH -> monthButton.setBackgnd(R.color.scanalyze_purple)
            TimeUnit.YEAR -> yearButton.setBackgnd(R.color.scanalyze_purple)
        }
    }

    private fun reinitializeDates(timeUnit: TimeUnit) {
        end = LocalDateTime.now()
        begin = when (timeUnit) {
            TimeUnit.WEEK -> end.minusWeeks(12)
            TimeUnit.MONTH -> {
                // we subtract 1 year from now
                LocalDateTime.of(end.year, end.month, 1, 0, 0, 0).minusMonths(11)
            }

            TimeUnit.YEAR -> end.minusYears(10)
        }
        val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")
        beginTextView.text = begin.format(formatter)
        endTextView.text = end.format(formatter)
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

    private fun aggregateDataForYears(data: List<JsonReceipt>?): Map<String, Float> {
        val aggregatedData = mutableMapOf<String, Float>()

        if (data != null) {
            for (d in data) {
                val dDate = LocalDateTime.parse(d.date, DateTimeFormatter.ISO_DATE_TIME)
                val key = dDate.year.toString()
                val total = d.total ?: 0f
                aggregatedData[key] = aggregatedData.getOrDefault(key, 0f) + total
            }
        }
        return aggregatedData
    }

    private fun aggregateDataForMonth(
        data: List<JsonReceipt>?, begin: LocalDateTime, end: LocalDateTime
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
                if (receiptDate.isAfter(begin) && receiptDate.isBefore(end)) {
                    val month = monthNames[receiptDate.monthValue - 1]
                    val total = receipt.total ?: 0f
                    aggregatedData[month] = aggregatedData.getOrDefault(month, 0.0f) + total
                }
            }
        }

        return aggregatedData
    }

    private fun aggregateDataForWeek(
        data: List<JsonReceipt>?,
        begin: LocalDateTime,
        end: LocalDateTime
    ): Map<String, Float> {
        val aggregatedData = mutableMapOf<String, Float>()

        // Initialize weeks with 0.0 based on the range between startWeek and endWeek
        // This way we handle the year transition
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

        //Display accumulated total
        totalTextView.text = String.format("%.2f", totalInterval)

        val barDataSet = BarDataSet(barEntries, "Spending")

        barChart.xAxis.valueFormatter = IndexAxisValueFormatter(labels)

        // set bars color
        barDataSet.color = resources.getColor(R.color.scanalyze_purple)

        barChartDescr.text = "Spending by ${activityTimeUnit.name.lowercase()}"

        // Remove right axis, grid lines and the legend
        barChart.axisRight.isEnabled = false
        barChart.xAxis.setDrawGridLines(false)
        barChart.legend.isEnabled = false
        barChart.description.text = ""
        barChart.description.textColor = Color.WHITE
        barDataSet.valueTextColor = Color.WHITE
        barDataSet.valueTextSize = 10f

        // Set label color
        val xAxis = barChart.xAxis
        xAxis.textColor = Color.WHITE
        barChart.axisLeft.textColor = Color.WHITE

        // To display all the labels on the axis
        xAxis.granularity = 1f

        val barData = BarData(barDataSet)

        barChart.data = barData
        barChart.invalidate()  // refresh the chart
    }

    fun updatePieChart(totalCategory: Map<String, Float>) {
        val pieEntries = ArrayList<PieEntry>()

        for ((key, value) in totalCategory) {
            pieEntries.add(PieEntry(value, key))
        }

        val pieDataSet = PieDataSet(pieEntries, "")
        val pieData = PieData(pieDataSet)

        // Customize your PieDataSet here (e.g., colors)
        // ...
        pieDataSet.colors.add(Color.parseColor("#D0005F"))
        pieDataSet.colors.add(Color.parseColor("#DE4F45"))
        pieDataSet.colors.add(Color.parseColor("#49C3FB"))
        pieDataSet.colors.add(Color.parseColor("#65A6FA"))
        pieDataSet.colors.add(Color.parseColor("#7E80E7"))

        // Making it a ring (donut) chart
        pieChart.holeRadius = 30f
        pieChart.setHoleColor(Color.TRANSPARENT)
        pieChart.transparentCircleRadius = 58f
        pieChart.legend.isEnabled = true
        pieChart.holeRadius = 54f
        pieChart.legend.textColor = Color.WHITE
        pieChart.setDrawEntryLabels(false)
        pieChart.legend.textSize = 12f
        pieChart.setUsePercentValues(true)
        pieChart.legend.formSize = 12f
        pieDataSet.valueTextSize = 22f
        pieDataSet.valueTextColor = Color.BLACK
        pieChart.description.text = ""
        pieChart.data = pieData
        pieChart.invalidate()
    }
}
