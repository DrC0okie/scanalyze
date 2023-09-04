package ch.heigvd.scanalyze.receipt

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Product(
    val abbreviatedName: String,
    val quantity: Float,
    val unitPrice: Float,
    val discount: Float = 0.0f,
    val total: Float = 0.0f
) : Parcelable