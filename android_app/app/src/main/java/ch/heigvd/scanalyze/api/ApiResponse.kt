package ch.heigvd.scanalyze.api

import ch.heigvd.scanalyze.receipt.JsonReceipt
import com.google.gson.annotations.SerializedName

data class ApiResponse(
    @SerializedName("total") val totalSpent: Float,
    @SerializedName("receipts") val receipts: List<JsonReceipt>,
    @SerializedName("total_category") val totalCategory: Map<String, Float>,
    @SerializedName("receipt") var receiptDetail: JsonReceipt
) {


}