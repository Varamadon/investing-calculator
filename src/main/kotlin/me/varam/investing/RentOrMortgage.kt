package me.varam.investing

import kotlin.math.pow

const val mortgageRatePercent = 5.0
const val realEstateRatePercent = 6.0

fun main() {
    val propertyCost = 12_000_000.0
    val initialPayment = 2_500_000.0
    val years = 30

    val monthlyUpkeep = 30000.0
    val rent = 50000.0
    val income = 200_000.0

    println("With rent:")
    println(calcTotalSumWithRent(years, income, rent, initialPayment).toLong())
    println("With mortgage")
    println(calcTotalSumWithMortgage(years, income, monthlyUpkeep, propertyCost, initialPayment).toLong())
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
    return (totalLoan * monthlyMortgageRate) / (1.0 - (1.0 + monthlyMortgageRate).pow(-1.0 * months))
}
