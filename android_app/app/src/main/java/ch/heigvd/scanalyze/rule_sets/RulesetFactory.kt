package ch.heigvd.scanalyze.rule_sets

import ch.heigvd.scanalyze.Shop.Shop
import ch.heigvd.scanalyze.ocr.TextElement
import java.lang.RuntimeException

object RulesetFactory {

    fun create(lines: List<List<TextElement>>): Ruleset {
        for (line in lines) {
            for (element in line) {
                val ruleset = getFromString(element.text)
                if (ruleset != null) {
                    return ruleset
                }
            }
        }
        throw RuntimeException("Shop not found in the text")
    }

    private fun getFromString(name: String): Ruleset? {
        return when (name.lowercase().filter { !it.isWhitespace() }) {
            Shop.MIGROS.name.lowercase() -> MigrosRuleset
            Shop.COOP.name.lowercase() -> CoopRuleset
            Shop.ALDI.name.lowercase() -> AldiRuleset
            Shop.LIDL.name.lowercase() -> LidlRuleset
            else -> null
        }
    }
}
