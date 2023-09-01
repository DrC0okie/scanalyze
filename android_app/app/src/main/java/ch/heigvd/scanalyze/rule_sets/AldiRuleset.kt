package ch.heigvd.scanalyze.rule_sets

object AldiRuleset: Ruleset {
    override val shopName = "aldi"
    override val minLengthForPrice = 4
    override val maxLengthForPrice = 6
}