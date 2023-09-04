package ch.heigvd.scanalyze.receipt

import android.os.Parcelable
import ch.heigvd.scanalyze.Shop.Shop
import com.google.gson.Gson
import kotlinx.parcelize.Parcelize
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Parcelize
data class Receipt(
    val userId: String,
    val date: String,
    val scanDate: String,
    val shop: Shop,
    val shopBranch: String,
    val products: ArrayList<Product>?,
    var totalPrice: Double = 0.0,
    var imgFilePath: String = ""
) : Parcelable {

    init {
        totalPrice = getTotal()
    }

    private fun getTotal(): Double {
        if (products != null)
            return products.sumOf { it.unitPrice.toDouble() }

        return 0.0
    }

    override fun toString(): String {
        return """
            |user id: $userId
            |shop: $shop
            |date: $date
            |scan date: $scanDate
            |products:
            |$products
        """.trimMargin()
    }



    fun toJson(): String {
        // Early exit if products is null or empty
        val productList = products ?: return ""
        if (productList.isEmpty()) return ""

        // Transform the list of Product to JsonProduct
        val jsonProducts = productList.map { p ->
            JsonProduct(p.abbreviatedName, p.quantity, p.unitPrice, p.discount, p.total)
        }.toTypedArray()

        // Create the JsonReceipt object
        val receipt = JsonReceipt(
            userId,
            date,
            shop.shopName.lowercase(),
            "",
            jsonProducts,
            0.0
        )

        // Create the wrapper map
        val wrapper = mapOf("receipt" to receipt)

        // Serialize the JsonWrapper object to JSON
        return Gson().toJson(wrapper)
    }
}