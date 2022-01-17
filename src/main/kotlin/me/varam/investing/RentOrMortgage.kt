package me.varam.investing

import kotlin.math.pow

const val mortgageRatePercent = 5.0
const val realEstateRatePercent = 5.0

fun main() {
    val propertyCost = 12_000_000.0
    val initialPayment = 2_500_000.0
    val years = 30

    val monthlyUpkeep = 25000.0

    println(calcMonthlyMortgagePayment(30, 9_500_000.0))
}



fun calcMonthlyMortgagePayment(years: Int, totalLoan: Double): Double {
    val monthlyMortgageRate = mortgageRatePercent / 1200.0
    val months = years * 12.0
    return (totalLoan * monthlyMortgageRate) / (1.0 - (1.0 + monthlyMortgageRate).pow(-1.0 * months))
}
