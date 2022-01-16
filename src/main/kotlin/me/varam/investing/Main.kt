package me.varam.investing

import me.varam.investing.math.findMinimizationArgument
import kotlin.math.abs

const val yearlyStocksMultiplicationPercent: Double = 8.1
const val yearlySpendFundsPercent: Double = 3.0
const val inflationPercents: Double = 4.0
const val tariffPercent: Double = 0.3
const val taxPercent: Double = 13.0


fun main() {
    val wantedMonthlyIncome = 70000.0
    val monthlyInvest = 35000.0
    val years = 30
    println(calcNeededFundsWithInflation(years, wantedMonthlyIncome).toLong())
    println(calcMonthlyInvestByWantedIncomeAndYearsInvesting(years, wantedMonthlyIncome))
}

fun calcYearsInvestingByWantedIncomeAndMonthlyInvest(
    monthlyInvest: Double,
    wantedMonthlyIncome: Double
): Int {
    return findMinimizationArgument {
        val years = it.toInt()
        val neededFunds = calcNeededFundsWithInflation(years, wantedMonthlyIncome)
        val endSum = calcEndSum(years, monthlyInvest)
        abs(endSum - neededFunds)
    }.toInt()
}

fun calcMonthlyInvestByWantedIncomeAndYearsInvesting(
    years: Int,
    wantedMonthlyIncome: Double
): Double {
    val neededFunds = calcNeededFundsWithInflation(years, wantedMonthlyIncome)
    return findMinimizationArgument {
        abs(calcEndSum(years, it) - neededFunds)
    }
}

fun calcNeededFundsWithInflation(years: Int, wantedMonthlyIncomeWithoutInflation: Double): Double {
    val wantedMonthlyIncomeWithInflation = calcInflatedWantedMonthlyIncome(years, wantedMonthlyIncomeWithoutInflation)
    return calcNeededFunds(wantedMonthlyIncomeWithInflation)
}

fun calcInflatedWantedMonthlyIncome(years: Int, wantedMonthlyIncome: Double): Double {
    var income = wantedMonthlyIncome
    val inflationMultiplication = inflationPercents.percentToMult()
    for (i in 1..years) {
        income *= inflationMultiplication
    }
    return income
}

fun calcNeededFunds(wantedMonthlyIncome: Double): Double {
    val wantedIncomeWithTaxAndTariff = wantedMonthlyIncome / (taxPercent.percentToDec() * tariffPercent.percentToDec())
    val yearlyIncome = wantedIncomeWithTaxAndTariff * 12
    return yearlyIncome / (yearlySpendFundsPercent / 100.0)
}

fun calcEndSum(years: Int, monthlyInvest: Double): Double {
    var sum = 0.0
    val monthsInvesting = years * 12
    var inflatedInvest = monthlyInvest
    for (i in 1..monthsInvesting) {
        if (i % 12 == 0) inflatedInvest *= inflationPercents.percentToMult()
        sum = calcSumNextMonth(sum, inflatedInvest, yearlyStocksMultiplicationPercent)
    }
    return sum
}

fun calcSumNextMonth(currentSum: Double, monthlyInvest: Double, yearlyMultiplicationPercents: Double): Double {
    val monthlyMultiplication = (yearlyMultiplicationPercents / 12).percentToMult()
    return monthlyMultiplication * currentSum + monthlyMultiplication * (monthlyInvest * tariffPercent.percentToDec())
}


fun Double.percentToMult(): Double {
    return 1.0 + (this / 100.0)
}

fun Double.percentToDec(): Double {
    return 1.0 - (this / 100.0)
}
