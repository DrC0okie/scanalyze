package ch.heigvd.scanalyze

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Product(
    val id: Int,
    val abbreviatedName: String,
    val quantity: Float,
    val unitPrice: Float,
    val discount: Float = 0.0f
) : Parcelable {

    fun getTotal(): Float {
        return quantity * unitPrice - discount
    }
}