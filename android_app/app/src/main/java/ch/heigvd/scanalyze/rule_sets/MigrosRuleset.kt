package ch.heigvd.scanalyze.rule_sets

import ch.heigvd.scanalyze.Shop

object MigrosRuleset: Ruleset() {
    override val shop = Shop.MIGROS
    override val minLengthLine = 4
    override val pricePattern = """^\s?\d[.,]\d{2}\s?$""".toRegex()
    override val quantityPattern = """^\s?\d{1,2}\s?$""".toRegex()
    override val weightPattern= """^(?=.*[.,])[0-9][0-9.,][0-9.,]\d{0,3}$""".toRegex()
    override val ediblePattern = """^\s?1\s?$""".toRegex()
}