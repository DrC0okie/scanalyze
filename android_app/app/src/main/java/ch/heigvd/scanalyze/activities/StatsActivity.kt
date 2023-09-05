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
import ch.heigvd.scanalyze.databinding.ActivityStatsBinding
import ch.heigvd.scanalyze.fragments.DateRangePickerFragment
import ch.heigvd.scanalyze.fragments.OnDateRangeSelectedListener
import ch.heigvd.scanalyze.fragments.OnYearSelectedListener
import ch.heigvd.scanalyze.fragments.YearPickerFragment
import ch.heigvd.scanalyze.receipt.JsonReceipt
import ch.heigvd.scanalyze.statistics.StatisticsApiResponse
import ch.heigvd.scanalyze.statistics.TimeUnit
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.google.android.material.button.MaterialButton
import com.google.gson.Gson
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.WeekFields


class StatsActivity : AppCompatActivity(), OnDateRangeSelectedListener, OnYearSelectedListener {
    private lateinit var binding: ActivityStatsBinding
    private lateinit var barChart: BarChart
    private lateinit var weekButton: MaterialButton
    private lateinit var monthButton: MaterialButton
    private lateinit var yearButton: MaterialButton
    private lateinit var intervalButton: Button
    private lateinit var totalTextView: TextView
    private lateinit var barChartDescr: TextView
    private lateinit var begin: LocalDateTime
    private lateinit var end: LocalDateTime
    private lateinit var activityTimeUnit: TimeUnit
    private lateinit var statisticsData: StatisticsApiResponse
    private lateinit var aggregatedData: Map<String, Float>
    val monthNames = arrayOf(
        "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"
    )

    override fun onDateRangeSelected(dateRange: Pair<LocalDateTime, LocalDateTime>) {
        Log.d("Scanalyze", "Selected range: ${dateRange.first} to ${dateRange.second}")
        begin = dateRange.first
        end = dateRange.second

        //Update the graph
        updateGraph(activityTimeUnit)

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

        //Update the graph
        updateGraph(activityTimeUnit)

        // Close the fragment
        val fragment = supportFragmentManager.findFragmentByTag("YearPickerFragment")
        if (fragment != null) {
            val dialogFragment = fragment as DialogFragment
            dialogFragment.dismiss()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStatsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        barChart = binding.barchartSpending
        weekButton = binding.buttonWeek
        monthButton = binding.buttonMonth
        yearButton = binding.buttonYear
        intervalButton = binding.buttonInterval
        totalTextView = binding.textViewTotal
        barChartDescr = binding.textViewChartDescr

        statisticsData = getStatData()

        initPage(statisticsData)

        weekButton.setOnClickListener {
            activityTimeUnit = TimeUnit.WEEK
            reinitializeDates(activityTimeUnit)
            updateButtonsBackgnd(activityTimeUnit)
            updateGraph(activityTimeUnit)
        }

        monthButton.setOnClickListener {
            activityTimeUnit = TimeUnit.MONTH
            reinitializeDates(activityTimeUnit)
            updateButtonsBackgnd(activityTimeUnit)
            updateGraph(activityTimeUnit)
        }

        yearButton.setOnClickListener {
            activityTimeUnit = TimeUnit.YEAR
            reinitializeDates(activityTimeUnit)
            updateButtonsBackgnd(activityTimeUnit)
            updateGraph(activityTimeUnit)
        }

        intervalButton.setOnClickListener {
            showDatePicker(activityTimeUnit)
        }
    }

    private fun updateGraph(timeUnit: TimeUnit) {
        aggregatedData = when (timeUnit){
            TimeUnit.WEEK -> aggregateDataForWeek(statisticsData.receipts, begin, end)
            TimeUnit.MONTH -> aggregateDataForMonth(statisticsData.receipts, begin, end)
            TimeUnit.YEAR -> aggregateDataForYears(statisticsData.receipts)
        }
        formatGraph(aggregatedData)
    }

    private fun showDatePicker(timeUnit: TimeUnit) {
        when (timeUnit) {
            TimeUnit.WEEK -> {
                val drpf = DateRangePickerFragment.newInstance()
                drpf.mListener = this
                drpf.show(supportFragmentManager, "DateRangePicker")
            }

            TimeUnit.MONTH -> {
                val ypf =
                    YearPickerFragment.newInstance(extractUniqueYears(statisticsData.receipts))
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
    }

    private fun MaterialButton.setBackgnd(backgroundColor: Int) {
        // Get the color from resources
        val color = ContextCompat.getColor(this@StatsActivity, backgroundColor)

        // Create a ColorStateList
        val colorStateList = ColorStateList.valueOf(color)

        // Set the background color
        this.backgroundTintList = colorStateList
    }

    private fun getStatData(): StatisticsApiResponse {
        //Test data
        val json = """{
                "total": 74.6,
                "receipts": [
                    {"date": "2019-05-01T13:56:51.692Z", "total": 18.65},
                    {"date": "2019-01-01T14:56:53.503Z","total": 18.65},
                    {"date": "2019-03-01T14:56:57.360Z","total": 18.65},
                    {"date": "2021-11-01T14:56:59.730Z","total": 18.65},
                    {"date": "2021-09-03T17:25:23.774Z","total": 18.65},
                    {"date": "2022-11-03T17:25:23.774Z","total": 18.65},
                    {"date": "2023-01-03T17:25:23.774Z","total": 18.65},
                    {"date": "2023-01-03T18:25:23.774Z","total": 18.65},
                    {"date": "2023-03-02T18:25:23.774Z","total": 18.65},
                    {"date": "2023-07-03T17:25:23.774Z","total": 18.65}
                ],
                "total_category": {"fruits-vegetables": 107.85,"starches": 9.2,"dairies-eggs": 9.6,"meat-fish": 40.25,"bread": 7.75}}"""

        val gson = Gson()
        return gson.fromJson(json, StatisticsApiResponse::class.java)
    }

    private fun initPage(data: StatisticsApiResponse) {

        //Init the filter to Month
        activityTimeUnit = TimeUnit.MONTH

        //Reinitialize the begin-end variables
        reinitializeDates(activityTimeUnit)

        //Select the month button
        updateButtonsBackgnd(activityTimeUnit)

        //Aggregate data and show graph
        aggregatedData = aggregateDataForMonth(data.receipts, begin, end)
        formatGraph(aggregatedData)
    }


    fun extractUniqueYears(data: List<JsonReceipt>): ArrayList<Int> {
        val uniqueYears = HashSet<Int>()

        for (receipt in data) {
            val receiptDate = LocalDateTime.parse(receipt.date, DateTimeFormatter.ISO_DATE_TIME)

            // Adds the year to the set (duplicates are ignored)
            uniqueYears.add(receiptDate.year)
        }

        // Convert the set to a sorted list
        return ArrayList(uniqueYears.toList().sorted())
    }

    fun aggregateDataForYears(data: List<JsonReceipt>): Map<String, Float> {
        val aggregatedData = mutableMapOf<String, Float>()

        for (d in data) {
            val dDate = LocalDateTime.parse(d.date, DateTimeFormatter.ISO_DATE_TIME)
            val key = dDate.year.toString()
            aggregatedData[key] = aggregatedData.getOrDefault(key, 0f) + d.total
        }
        return aggregatedData
    }

    fun aggregateDataForMonth(
        data: List<JsonReceipt>, begin: LocalDateTime, end: LocalDateTime
    ): Map<String, Float> {
        val aggregatedData = mutableMapOf<String, Float>()

        // Generate month keys based on the date range
        var current = begin
        while (current.isBefore(end) || current.isEqual(end)) {
            val month = monthNames[current.monthValue - 1]
            aggregatedData[month] = 0.0f
            current = current.plusMonths(1)
        }

        for (receipt in data) {
            val receiptDate = LocalDateTime.parse(receipt.date, DateTimeFormatter.ISO_DATE_TIME)
            if (receiptDate.isAfter(begin) && receiptDate.isBefore(end)) {
                val month = monthNames[receiptDate.monthValue - 1]
                aggregatedData[month] = aggregatedData.getOrDefault(month, 0.0f) + receipt.total
            }
        }

        return aggregatedData
    }

    fun aggregateDataForWeek(
        data: List<JsonReceipt>,
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
        for (receipt in data) {
            val receiptDate = LocalDateTime.parse(receipt.date, DateTimeFormatter.ISO_DATE_TIME)
            if (receiptDate.isAfter(begin) && receiptDate.isBefore(end)) {
                val week = receiptDate.get(WeekFields.ISO.weekOfWeekBasedYear())
                val key = "$week"
                aggregatedData[key] = aggregatedData.getOrDefault(key, 0.0f) + receipt.total
            }
        }
        return aggregatedData
    }

    fun formatGraph(aggregatedData: Map<String, Float>) {
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
        barChart.description.textSize = 18f
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
}
