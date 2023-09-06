package ch.heigvd.scanalyze.statistics

import ch.heigvd.scanalyze.receipt.JsonReceipt
import com.google.gson.annotations.SerializedName

data class ApiResponse(
    @SerializedName("total") val totalSpent: Float,
    @SerializedName("receipts") val receipts: List<JsonReceipt>,
    @SerializedName("total_category") val totalCategory: Map<String, Float>
) {
    // Helper function to get weekly/monthly/yearly total
    fun getTotalForTimeRange(fromDate: String, toDate: String): Float {
        // Logic here
        return 0.0f // Dummy value
    }

    // Helper function to get accumulated totals
    fun getAccumulatedTotals(): List<Float> {
        // Logic here
        return listOf() // Dummy value
    }

    // Helper function to get total by category
    fun getTotalByCategory(): Map<String, Float> {
        return totalCategory // As the data is already in the required format
    }

}