package ch.heigvd.scanalyze.activities

import android.app.DatePickerDialog
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import ch.heigvd.scanalyze.R
import ch.heigvd.scanalyze.databinding.ActivityStatsBinding
import ch.heigvd.scanalyze.statistics.StatisticsApiResponse
import ch.heigvd.scanalyze.statistics.TimeUnit
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.google.gson.Gson
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.WeekFields
import java.util.Calendar
import java.util.Locale

class StatsActivity : AppCompatActivity() {
    private lateinit var barChart: BarChart
    private lateinit var weekButton: Button
    private lateinit var monthButton: Button
    private lateinit var yearButton: Button
    private lateinit var intervalButton: Button
    private lateinit var binding: ActivityStatsBinding
    private lateinit var begin: LocalDateTime
    private lateinit var end: LocalDateTime
    private lateinit var activityTimeUnit: TimeUnit

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStatsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        barChart = binding.barchartSpending
        weekButton = binding.buttonWeek
        monthButton = binding.buttonMonth
        yearButton = binding.buttonYear
        intervalButton = binding.buttonInterval

        val statisticsData = getStatData()
        initPage(statisticsData)

        weekButton.setOnClickListener {
            activityTimeUnit = TimeUnit.WEEK
            updateGraphWithInterval(statisticsData, activityTimeUnit, begin, end)
        }

        monthButton.setOnClickListener {
            activityTimeUnit = TimeUnit.MONTH
            updateGraphWithInterval(statisticsData, activityTimeUnit, begin, end)
        }

        yearButton.setOnClickListener {
            activityTimeUnit = TimeUnit.YEAR
            updateGraphWithInterval(statisticsData, activityTimeUnit, begin, end)
        }

        intervalButton.setOnClickListener {
            // Open "begin" date picker
            val datePickerDialog1 = DatePickerDialog(this)
            datePickerDialog1.setTitle("Begin date")
            datePickerDialog1.setOnDateSetListener { _, year1, month1, dayOfMonth1 ->
                begin = LocalDateTime.of(year1, month1 + 1, dayOfMonth1, 0, 0)

                // Open "end" date picker
                val datePickerDialog2 = DatePickerDialog(this)
                datePickerDialog2.setTitle("End date")
                datePickerDialog2.setOnDateSetListener { _, year2, month2, dayOfMonth2 ->
                    end = LocalDateTime.of(year2, month2 + 1, dayOfMonth2, 0, 0)

                    // Now update the graphs
                    updateGraphWithInterval(statisticsData, activityTimeUnit, begin, end)
                }
                datePickerDialog2.show()
            }
            datePickerDialog1.show()
        }
    }

    private fun getStatData(): StatisticsApiResponse{
        //Test data
        val json = """{
                "total": 74.6,
                "receipts": [
                    {"date": "2019-05-01T13:56:51.692Z", "total": 18.65},
                    {"date": "2019-01-01T14:56:53.503Z","total": 18.65},
                    {"date": "2019-03-01T14:56:57.360Z","total": 18.65},
                    {"date": "2021-11-01T14:56:59.730Z","total": 18.65},
                    {"date": "2021-09-03T17:25:23.774Z","total": 18.65}
                ],
                "total_category": {"fruits-vegetables": 107.85,"starches": 9.2,"dairies-eggs": 9.6,"meat-fish": 40.25,"bread": 7.75}}"""

        val gson = Gson()
        return gson.fromJson(json, StatisticsApiResponse::class.java)
    }

    private fun initPage(data: StatisticsApiResponse){
        begin = LocalDateTime.now().minusYears(1)
        end = LocalDateTime.now()
        activityTimeUnit = TimeUnit.MONTH
        updateGraphWithInterval(data, activityTimeUnit, begin, end)
    }

    // Not used anymore
    private fun updateBarChart(barChart: BarChart, statisticsData: StatisticsApiResponse, timeUnit: TimeUnit) {
        val groupedData = getSpentAmountGroupedByTimeUnit(statisticsData, timeUnit)

        val entries = ArrayList<BarEntry>()
        var index = 0f

        for ((_, value) in groupedData) {
            entries.add(BarEntry(index++, value.toFloat()))
        }

        val barDataSet = BarDataSet(entries, "Amount Spent")

        // set bars color
        barDataSet.color = resources.getColor(R.color.scanalyze_purple)

        val barData = BarData(barDataSet)
        barChart.data = barData
        barChart.invalidate() // Refresh the chart
    }

    fun getSpentAmountGroupedByTimeUnit(statisticsData: StatisticsApiResponse, timeUnit: TimeUnit): Map<String, Double> {
        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
        val result = mutableMapOf<String, Double>()

        for (receipt in statisticsData.receipts) {
            val date = sdf.parse(receipt.date) ?: continue
            val calendar = Calendar.getInstance()
            calendar.time = date

            val key = when (timeUnit) {
                TimeUnit.WEEK -> "${calendar.get(Calendar.YEAR)}-W${calendar.get(Calendar.WEEK_OF_YEAR)}"
                TimeUnit.MONTH -> "${calendar.get(Calendar.YEAR)}-${calendar.get(Calendar.MONTH) + 1}"
                TimeUnit.YEAR-> "${calendar.get(Calendar.YEAR)}"
            }

            val existingValue = result[key] ?: 0.0
            result[key] = existingValue + receipt.total
        }

        return result
    }

    // Not used anymore
    fun customizeBarChart(barChart: BarChart, timeUnit: TimeUnit) {
        // Remove right axis and grid lines
        barChart.axisRight.isEnabled = false
        barChart.xAxis.setDrawGridLines(false)

        val xAxis = barChart.xAxis
        // Set label color
        xAxis.textColor = Color.WHITE
        barChart.axisLeft.textColor = Color.WHITE

        // Set labels based on the time unit selected
        when (timeUnit) {
            TimeUnit.WEEK -> xAxis.valueFormatter = IndexAxisValueFormatter(arrayOf("2020", "2021", "2022"))
            TimeUnit.MONTH -> xAxis.valueFormatter = IndexAxisValueFormatter(arrayOf("J", "F", "M", "A", "M", "J", "J", "A", "S", "O", "N", "D"))
            TimeUnit.YEAR -> xAxis.valueFormatter = IndexAxisValueFormatter(arrayOf("2020", "2021", "2022"))
        }
    }

    fun updateGraphWithInterval(data: StatisticsApiResponse, unit: TimeUnit, begin: LocalDateTime, end: LocalDateTime) {
        // Step 1: Filter Data
        val formatter = DateTimeFormatter.ISO_DATE_TIME
        val filteredReceipts = data.receipts.filter {
            val receiptDate = LocalDateTime.parse(it.date, formatter)
            (receiptDate.isAfter(begin) || receiptDate.isEqual(begin)) && (receiptDate.isBefore(end) || receiptDate.isEqual(end))
        }

        // Step 2: Group Data by Time Unit
        val groupedData = mutableMapOf<String, Float>()
        for (receipt in filteredReceipts) {
            val date = LocalDateTime.parse(receipt.date, formatter)
            val key = when (unit) {
                TimeUnit.WEEK -> {
                    val weekOfYear = date.get(WeekFields.of(Locale.getDefault()).weekOfWeekBasedYear())
                    date.year.toString() + "-" + weekOfYear.toString()
                }
                TimeUnit.MONTH -> date.year.toString() + "-" + date.monthValue.toString()
                TimeUnit.YEAR -> date.year.toString()
            }

            groupedData[key] = (groupedData[key] ?: 0f) + receipt.total
        }

        // Sort the grouped data by key if necessary
        val sortedGroupedData = groupedData.toSortedMap()

        // Calculate the number of time units between 'begin' and 'end'
        val numOfUnits = when(unit) {
            TimeUnit.WEEK -> java.time.temporal.ChronoUnit.WEEKS.between(begin, end).toInt()
            TimeUnit.MONTH -> java.time.temporal.ChronoUnit.MONTHS.between(begin, end).toInt()
            TimeUnit.YEAR -> java.time.temporal.ChronoUnit.YEARS.between(begin, end).toInt()
        }

        // Generate labels based on the number of time units
        val labels = Array(numOfUnits + 1) { i ->
            when(unit) {
                TimeUnit.WEEK -> "W${i+1}"
                TimeUnit.MONTH -> "M${i+1}"
                TimeUnit.YEAR -> "${begin.year + i}"
            }
        }

        // Step 3: Populate Chart Data
        val entries = ArrayList<BarEntry>()
        var index = 0f
        for ((_, value) in sortedGroupedData) {
            entries.add(BarEntry(index++, value))
        }

        // Set up the X-axis labels
        val xAxis = barChart.xAxis
        xAxis.valueFormatter = IndexAxisValueFormatter(labels)
        val set = BarDataSet(entries, "Spending")
        val barData = BarData(set)

        // Assuming barchart is your BarChart view instance
        barChart.data = barData
        barChart.invalidate() // refresh chart
    }
}
