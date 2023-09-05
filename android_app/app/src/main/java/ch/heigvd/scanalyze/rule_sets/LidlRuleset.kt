package ch.heigvd.scanalyze.rule_sets

import ch.heigvd.scanalyze.Shop

object LidlRuleset: Ruleset() {
    override val shop = Shop.LIDL
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