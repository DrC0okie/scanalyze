package ch.heigvd.scanalyze.rule_sets

import ch.heigvd.scanalyze.Shop.Shop

object AldiRuleset: Ruleset() {
    override val shop = Shop.ALDI
    override val minLengthLine: Int
        get() = TODO("Not yet implemented")
    override val pricePattern: Regex
        get() = TODO("Not yet implemented")
    override val quantityPattern: Regex
        get() = TODO("Not yet implemented")
    override val weightPattern: Regex
        get() = TODO("Not yet implemented")
    override val ediblePattern: Regex
        get() = TODO("Not yet implemented")

}