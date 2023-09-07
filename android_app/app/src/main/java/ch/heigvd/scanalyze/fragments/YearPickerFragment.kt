package ch.heigvd.scanalyze.fragments

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import java.util.*
import ch.heigvd.scanalyze.R
import com.google.android.material.button.MaterialButton
import java.time.LocalDate
import java.time.LocalDateTime

class YearPickerFragment : DialogFragment() {

    // The list of years for which data is available, populated dynamically
    private var availableYears = listOf<Int>()

    var mListener: OnYearSelectedListener? = null

    private var selectedYear = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_year_picker, container, false)

        // Unpack the bundle to get the list of available years
        availableYears = arguments?.getIntegerArrayList("availableYears") ?: listOf()

        // get the grid layout that will contain the buttons
        val gridLayout: GridLayout = rootView.findViewById(R.id.grid_layout_button_container)

        // Populate the grid layout
        for (year in availableYears) {
            val button = MaterialButton(requireContext()).apply {
                text = year.toString()
                layoutParams = GridLayout.LayoutParams().apply {
                    width = GridLayout.LayoutParams.WRAP_CONTENT
                    height = GridLayout.LayoutParams.WRAP_CONTENT
                    setGravity(Gravity.FILL)
                    setMargins(12, 12, 12, 12)
                }
                setBackgroundResource(R.drawable.button_background_interval)
                setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
                backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.scanalyze_grey)
            }

            // Add onClickListener to each button
            button.setOnClickListener {
                selectedYear = button.text.toString().toInt()
                notifyDateSelected()
            }

            // Add the button to the GridLayout
            gridLayout.addView(button)
        }

        return rootView
    }

    companion object {
        fun newInstance(availableYears: ArrayList<Int>): YearPickerFragment {
            val fragment = YearPickerFragment()
            val args = Bundle()
            args.putIntegerArrayList("availableYears", availableYears)
            fragment.arguments = args
            return fragment
        }
    }

    fun show() {
        show(parentFragmentManager, "ch.heigvd.scanalyze.fragments.YearPickerFragment")
    }

    private fun notifyDateSelected() {
        mListener?.let {
            val startDate = LocalDateTime.of(selectedYear, 1, 1, 0, 0, 0)
            val endDate = LocalDateTime.of(
                selectedYear, 12,
                LocalDate.of(selectedYear, 12, 1).lengthOfMonth(), 23, 59, 59
            )
            it.onYearSelected(Pair(startDate, endDate))
        }
    }
}
