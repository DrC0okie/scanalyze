package ch.heigvd.scanalyze.api

import ch.heigvd.scanalyze.receipt.Receipt
import com.google.gson.annotations.SerializedName

data class ApiResponse(
    @SerializedName("total") val totalSpent: Float,
    @SerializedName("receipts") val receipts: List<Receipt>,
    @SerializedName("total_category") val totalCategory: Map<String, Float>,
    @SerializedName("receipt") var receiptDetail: Receipt
)