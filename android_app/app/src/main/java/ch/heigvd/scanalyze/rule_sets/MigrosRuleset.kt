package ch.heigvd.scanalyze.rule_sets

import ch.heigvd.scanalyze.Shop.Shop

object MigrosRuleset: Ruleset() {
    override val shop = Shop.MIGROS
    override val minLengthLine = 4
    override val pricePattern = """^\s?\d{0,2}[.,]\d{2}\s?$""".toRegex()
    override val quantityPattern = """^\s?\d{1,2}\s?$""".toRegex()
    override val weightPattern= """^[0-9][0-9.,]{0,2}\d{3}$""".toRegex()
    override val ediblePattern = """^\s?1\s?$""".toRegex()
}