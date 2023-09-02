package ch.heigvd.scanalyze

private const val MIGROS_NAME = "Migros"
private const val COOP_NAME = "Coop"
private const val ALDI_NAME = "Aldi"
private const val LIDL_NAME = "Lidl"
enum class Shop(val shopName: String, val resourceImage: Int) {
    UNKNOWN("undefined",R.drawable.question),
    MIGROS(MIGROS_NAME, R.drawable.migros),
    COOP(COOP_NAME, R.drawable.coop),
    ALDI(ALDI_NAME, R.drawable.aldi),
    LIDL(LIDL_NAME, R.drawable.lidl);
}
