package ch.heigvd.scanalyze.fragments

import java.time.LocalDateTime

interface OnDateRangeSelectedListener {
    fun onDateRangeSelected(dateRange: Pair<LocalDateTime, LocalDateTime>)
}

interface OnYearSelectedListener{
    fun onYearSelected(yearRange: Pair<LocalDateTime, LocalDateTime>)
}