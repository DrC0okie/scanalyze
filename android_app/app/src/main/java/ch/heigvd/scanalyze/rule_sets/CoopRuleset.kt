package ch.heigvd.scanalyze.rule_sets

object CoopRuleset: Ruleset {
    override val shopName = "coop"
    override val minLengthForPrice = 4
    override val maxLengthForPrice = 6
}