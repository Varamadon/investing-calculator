package me.varam.investing

import kotlin.math.pow
import kotlin.math.round

const val mortgageRatePercent = 8.0
const val realEstateRatePercent = 6.0

fun main() {
    val propertyCost = 15_000_000.0
    val initialPayment = 3_000_000.0
    val years = 30

    val monthlyUpkeep = 30000.0
    val rent = 70000.0
    val income = 200_000.0

    val totalSumWithRent = calcTotalSumWithRent(years, income, rent, initialPayment)
    val totalSumWithMortgage = calcTotalSumWithMortgage(years, income, monthlyUpkeep, propertyCost, initialPayment)
    println("With rent:")
    println(totalSumWithRent.toLong())
    println("With mortgage:")
    println(totalSumWithMortgage.toLong())
    println("Percent difference mortgage to rent:")
    print((((totalSumWithMortgage / totalSumWithRent) - 1.0) * 100.0).round(2))
    println(" %")
}

fun calcTotalSumWithRent(
    years: Int,
    income: Double,
    monthlyRent: Double,
    initialPayment: Double
): Double {
    var inflatedIncome = income
    var inflatedMonthlyRent = monthlyRent
    var investSum = initialPayment

    val months = years * 12
    for (i in 1 .. months) {
        if (i % 12 == 0) {
            inflatedIncome *= inflationPercents.percentToMult()
            inflatedMonthlyRent *= inflationPercents.percentToMult()
        }
        val invest = inflatedIncome - inflatedMonthlyRent
        investSum = calcSumNextMonth(investSum, invest, yearlyStocksMultiplicationPercent)
    }
    return investSum
}

fun calcTotalSumWithMortgage(
    years: Int,
    income: Double,
    monthlyUpkeep: Double,
    initialPropertyCost: Double,
    initialPayment: Double
): Double {
    val totalLoan = initialPropertyCost - initialPayment
    val monthlyMortgagePayment = calcMonthlyMortgagePayment(years, totalLoan)
    var inflatedIncome = income
    var inflatedMonthlyUpkeep = monthlyUpkeep
    var propertyCost = initialPropertyCost
    var investSum = 0.0

    val months = years * 12
    for (i in 1 .. months) {
        if (i % 12 == 0) {
            inflatedIncome *= inflationPercents.percentToMult()
            inflatedMonthlyUpkeep *= inflationPercents.percentToMult()
        }
        propertyCost = calcPropertyCostNextMonth(propertyCost, realEstateRatePercent)
        val invest = inflatedIncome - monthlyMortgagePayment - inflatedMonthlyUpkeep
        investSum = calcSumNextMonth(investSum, invest, yearlyStocksMultiplicationPercent)
    }
    return propertyCost + investSum
}

fun calcPropertyCostNextMonth(propertyCost: Double, yearlyRealEstateRatePercent: Double): Double {
    val monthlyRealEstateRate = (yearlyRealEstateRatePercent / 12.0).percentToMult()
    return propertyCost * monthlyRealEstateRate
}

fun calcMonthlyMortgagePayment(years: Int, totalLoan: Double): Double {
    val monthlyMortgageRate = mortgageRatePercent / 1200.0
    val months = years * 12.0
    val result = (totalLoan * monthlyMortgageRate) / (1.0 - (1.0 + monthlyMortgageRate).pow(-1.0 * months))
    println("Mortgage payment:")
    println(result.toLong())
    return result
}

fun Double.round(decimals: Int): Double {
    var multiplier = 1.0
    repeat(decimals) { multiplier *= 10 }
    return round(this * multiplier) / multiplier
}
