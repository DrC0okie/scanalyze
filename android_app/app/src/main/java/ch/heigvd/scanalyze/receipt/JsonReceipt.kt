package ch.heigvd.scanalyze.receipt

import com.google.gson.annotations.SerializedName

class JsonReceipt(
    val user_id: String,
    @SerializedName("date") val date: String,
    val shop_name: String,
    val shop_branch: String,
    val products: Array<JsonProduct>,
    @SerializedName("total") val total: Float
) {

}