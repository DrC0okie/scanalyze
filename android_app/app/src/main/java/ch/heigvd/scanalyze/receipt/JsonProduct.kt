package ch.heigvd.scanalyze.receipt

data class JsonProduct(
    val product_id: String,
    val product_name: String,
    val quantity: Float,
    val unit_price: Float,
    val discount_amount: Float
) {
}