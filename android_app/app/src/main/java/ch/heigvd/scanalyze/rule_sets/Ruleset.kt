package ch.heigvd.scanalyze.rule_sets

import ch.heigvd.scanalyze.Shop
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

abstract class Ruleset {
    protected abstract val minLengthLine: Int
    protected abstract val pricePattern: Regex
    protected abstract val quantityPattern: Regex
    protected abstract val weightPattern: Regex
    protected abstract val ediblePattern: Regex
    abstract val shop: Shop
    private val productNamePattern = """^\s?\S{2,12}\s?$""".toRegex()
    private val datePattern = """^\s?\d{2}\.\d{2}\.\d{4}\s?$""".toRegex()
    private val dateFormat: DateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")
    private val dateIsoFormat: DateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME

    fun isMinLineSize(size: Int) = size > minLengthLine

    fun isProduct(text: String) = productNamePattern.matches(text)

    fun isPrice(text: String) = pricePattern.matches(text)

    fun isQuantity(text: String) = quantityPattern.matches(text) || weightPattern.matches(text)

    fun isEdible(text: String) = ediblePattern.matches(text)

    fun isDate(text: String) = datePattern.matches(text)

    fun getDate(date: String?): String {
        if(date.isNullOrEmpty()) return ""
        
        return LocalDate.parse(date, dateFormat).atStartOfDay().format(dateIsoFormat)
    }

    fun getDateTimeNow(): String = LocalDateTime.now().format(dateIsoFormat)

    // This is the standard order in the prices list
    open fun getQuantity(prices: List<Float>) = prices[0]

    open fun getUnitPrice(prices: List<Float>) = prices[1]

    open fun getDiscount(prices: List<Float>): Float{
        if(prices.size == 3){
            return 0.0f
        }
        return prices[2]
    }

    open fun getTotalPrice(prices: List<Float>): Float{
        if(prices.size == 3){
            return prices[2]
        }
        return prices[3]
    }

}

