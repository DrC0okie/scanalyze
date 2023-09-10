package ch.heigvd.scanalyze.receipt

import android.os.Parcelable
import ch.heigvd.scanalyze.api.HttpMethod
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
class Receipt(
    @SerializedName("_id") val id: String,
    @SerializedName("user_id") val userId: String?,
    @SerializedName("date") val date: String?,
    @SerializedName("shop_name") var shopName: String?,
    @SerializedName("shop_branch")val shopBranch: String?,
    @SerializedName("products")val products: Array<Product>?,
    @SerializedName("total") val total: Float?,
    @Transient var imgFilePath: String? = "",
    @Transient var httpMethod: HttpMethod? = HttpMethod.GET
) : Parcelable