package ch.heigvd.scanalyze.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import java.util.*
import ch.heigvd.scanalyze.R
import com.google.android.material.button.MaterialButton
import java.time.LocalDate
import java.time.LocalDateTime

class DateRangePickerFragment : DialogFragment() {

    private lateinit var currentYearTextView: TextView
    private lateinit var selectedMonthRange: IntRange
    private lateinit var janMarButton: MaterialButton
    private lateinit var aprJunButton: MaterialButton
    private lateinit var julSepButton: MaterialButton
    private lateinit var octDecButton: MaterialButton
    private lateinit var decrementButton: ImageButton
    private lateinit var incrementButton: ImageButton
    private var selectedYear: Int = 0

    var mListener: OnDateRangeSelectedListener? = null

    companion object {
        fun newInstance(): DateRangePickerFragment {
            return DateRangePickerFragment()
        }
    }

    fun show() {
        show(parentFragmentManager, "ch.heigvd.scanalyze.fragments.DateRangePickerFragment")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_date_range_picker, container, false)

        currentYearTextView = rootView.findViewById(R.id.text_view_current_year)
        decrementButton = rootView.findViewById(R.id.button_decrement_year)
        incrementButton = rootView.findViewById(R.id.button_increment_year)
        janMarButton = rootView.findViewById(R.id.button_jan_mar)
        aprJunButton = rootView.findViewById(R.id.button_apr_jun)
        julSepButton = rootView.findViewById(R.id.button_jul_sep)
        octDecButton = rootView.findViewById(R.id.button_oct_dec)

        // Set the default year based on the current month
        val calendar: Calendar = Calendar.getInstance()
        val currentYear = calendar.get(Calendar.YEAR)

        // get the current month
        val currentMonth = calendar.get(Calendar.MONTH) + 1

        // initialize default values
        initialize(currentMonth, currentYear)

        decrementButton.setOnClickListener {
            selectedYear--
            updateView(currentMonth, currentYear)
        }

        incrementButton.setOnClickListener {
            selectedYear++
            updateView(currentMonth, currentYear)
        }

        janMarButton.setOnClickListener {
            selectedMonthRange = 1..3
            notifyDateSelected()
        }

        aprJunButton.setOnClickListener {
            selectedMonthRange = 4..6
            notifyDateSelected()
        }

        julSepButton.setOnClickListener {
            selectedMonthRange = 7..9
            notifyDateSelected()
        }

        octDecButton.setOnClickListener {
            selectedMonthRange = 10..12
            notifyDateSelected()
        }

        return rootView
    }

    private fun initialize(currentMonth: Int, currentYear: Int) {
        selectedYear = currentYear
        selectedMonthRange = when (currentMonth) {
            in 1..3 -> 1..3
            in 4..6 -> 4..6
            in 7..9 -> 7..9
            in 10..12 -> 10..12
            else -> 1..3
        }

        updateView(currentMonth, currentYear)
    }

    private fun updateView(currentMonth: Int, currentYear: Int) {
        currentYearTextView.text = selectedYear.toString()
        updateButtonBorders(currentMonth, selectedYear, currentYear)
    }

    private fun notifyDateSelected() {
        mListener?.let {
            val startDate = LocalDateTime.of(selectedYear, selectedMonthRange.first, 1, 0, 0, 0)
            val endDate = LocalDateTime.of(
                selectedYear, selectedMonthRange.last,
                LocalDate.of(selectedYear, selectedMonthRange.last, 1).lengthOfMonth(), 0, 0, 0
            )
            it.onDateRangeSelected(Pair(startDate, endDate))
        }
    }

    private fun updateButtonBorders(currentMonth: Int, selectedYear: Int, currentYear: Int) {
        // Disable future intervals

        janMarButton.isVisible = currentYear > selectedYear
        aprJunButton.isVisible = currentYear > selectedYear
        julSepButton.isVisible = currentYear > selectedYear
        octDecButton.isVisible = currentYear > selectedYear
        incrementButton.isVisible = currentYear > selectedYear

        if (currentYear == selectedYear) {
            janMarButton.isVisible = currentMonth >= 1
            aprJunButton.isVisible = currentMonth >= 4
            julSepButton.isVisible = currentMonth >= 7
            octDecButton.isVisible = currentMonth >= 10
        }
    }
}