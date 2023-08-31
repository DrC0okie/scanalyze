package ch.heigvd.scanalyze

import android.os.Parcelable
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlinx.parcelize.Parcelize


@Parcelize
class Receipt(
    val userId: Int,
    private val date: LocalDate,
    private val scanDate: LocalDate,
    val shop: Shop,
    val shopBranch: String,
    val products: ArrayList<Product>?,
    val totalPrice: Double): Parcelable {

    fun getTotal(): Double{
        if(products!= null)
            return products.sumOf { it.unitPrice }

        return 0.0
    }

    fun getFormattedDate(): String = date.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))
    fun getFormattedScanDate(): String = scanDate.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))
}