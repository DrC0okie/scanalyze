package ch.heigvd.scanalyze.receipt

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
class JsonReceipt(
    @SerializedName("_id") val id: String,
    @SerializedName("user_id") val userId: String,
    @SerializedName("date") val date: String?,
    @SerializedName("shop_name") val shopName: String?,
    @SerializedName("shop_branch")val shopBranch: String?,
    @SerializedName("products")val products: Array<JsonProduct>?,
    @SerializedName("total") val total: Float?,
    var imgFilePath: String? = ""
) : Parcelable {

}