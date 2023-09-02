package ch.heigvd.scanalyze

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


@Parcelize
data class Receipt(
    val userId: Int,
    val date: String,
    val scanDate: String,
    val shop: Shop,
    val shopBranch: String,
    val products: ArrayList<Product>?,
    var totalPrice: Double = 0.0,
    var imgFilePath: String = ""): Parcelable {

    init{
        totalPrice = getTotal()
    }

    private fun getTotal(): Double{
        if(products!= null)
            return products.sumOf { it.unitPrice.toDouble() }

        return 0.0
    }

    fun getReadableDate(strDate: String): String{
        val tmpDate = LocalDateTime.parse(strDate, DateTimeFormatter.ISO_LOCAL_DATE_TIME)
        return tmpDate.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))
    }

    override fun toString(): String {
        return """
            |user id: $userId
            |shop: $shop
            |date: $date
            |scan date: $scanDate
            |products:
            |--> $products
        """.trimMargin()
    }

}