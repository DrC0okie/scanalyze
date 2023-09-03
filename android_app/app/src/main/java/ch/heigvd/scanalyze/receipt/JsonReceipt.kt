package ch.heigvd.scanalyze.receipt

class JsonReceipt(
    val _id: String,
    val user_id: String,
    val date: String,
    val shop_name: String,
    val shop_branch: String,
    val products: Array<JsonProduct>,
    val total: Double
) {

}