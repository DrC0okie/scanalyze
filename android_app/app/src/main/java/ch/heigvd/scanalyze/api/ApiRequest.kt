package ch.heigvd.scanalyze.api

import ch.heigvd.scanalyze.receipt.Receipt
import com.google.gson.annotations.SerializedName

/**
 * Data used to be serialized and posted to the API
 */
data class ApiRequest(@SerializedName("receipt") var receiptDetail: Receipt)