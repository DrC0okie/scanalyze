package ch.heigvd.scanalyze.api

import ch.heigvd.scanalyze.receipt.JsonReceipt
import com.google.gson.annotations.SerializedName

data class ApiRequest(@SerializedName("receipt") var receiptDetail: JsonReceipt)