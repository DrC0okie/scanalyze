package ch.heigvd.scanalyze.rule_sets

interface Ruleset {
    val shopName: String
    val minLengthForPrice: Int
    val maxLengthForPrice: Int
}