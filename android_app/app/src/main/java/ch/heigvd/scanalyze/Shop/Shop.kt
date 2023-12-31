package ch.heigvd.scanalyze.Shop

import ch.heigvd.scanalyze.R

private const val MIGROS_NAME = "Migros"
private const val COOP_NAME = "Coop"
private const val ALDI_NAME = "Aldi"
private const val LIDL_NAME = "Lidl"

enum class Shop(val shopName: String, val resourceImage: Int) {
    UNKNOWN("undefined", R.drawable.question),
    MIGROS(MIGROS_NAME, R.drawable.migros),
    COOP(COOP_NAME, R.drawable.coop),
    ALDI(ALDI_NAME, R.drawable.aldi),
    LIDL(LIDL_NAME, R.drawable.lidl);

    object func{
        fun fromString(name: String?): Shop {
            if (name != null) {
                return when (name.lowercase().filter { !it.isWhitespace() }) {
                    MIGROS.name.lowercase() -> MIGROS
                    COOP.name.lowercase() -> COOP
                    ALDI.name.lowercase() -> ALDI
                    LIDL.name.lowercase() -> LIDL
                    else -> UNKNOWN
                }
            }
            return UNKNOWN
        }
    }
}