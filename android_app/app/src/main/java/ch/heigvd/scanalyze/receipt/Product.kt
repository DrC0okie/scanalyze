package ch.heigvd.scanalyze.receipt

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Product(
    @SerializedName("product_name")val name: String,
    @SerializedName("quantity")val quantity: Float,
    @SerializedName("unit_price")val unitPrice: Float,
    @SerializedName("discount_amount")val discount: Float,
    @SerializedName("total_price")val totalPrice: Float
): Parcelable