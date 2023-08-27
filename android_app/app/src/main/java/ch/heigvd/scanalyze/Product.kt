package ch.heigvd.scanalyze

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Product(val id: Int,
    val abbreviatedName: String,
    val quantity: Double,
    val unitPrice: Double,
    val discount: Double = 0.0): Parcelable {
}