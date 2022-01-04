package me.varam.investing

import me.varam.investing.math.findMinimizationArgument
import kotlin.math.abs

const val yearlyStockMultiplicationPercents: Double = 13.5
const val yearlyBondsMultiplicationPercents: Double = 6.0
const val inflationPercents: Double = 2.0
const val tariff: Double = 0.003
const val tax: Double = 0.13


fun main() {
    val wantedMonthlyIncome = 1000.0
    val approxMonthlyInvest = 1330.0
    val approxYears = calcYearsInvestingByWantedIncomeAndMonthlyInvest(approxMonthlyInvest, wantedMonthlyIncome)
    println(approxYears)
    val monthlyInvestByApproxYears = calcMonthlyInvestByWantedIncomeAndYearsInvesting(approxYears, wantedMonthlyIncome)
    println(monthlyInvestByApproxYears)
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
    val inflationMultiplication = 1.0 + inflationPercents / 100.0
    for (i in 1..years) {
        income *= inflationMultiplication
    }
    return income
}

fun calcNeededFunds(wantedMonthlyIncome: Double): Double {
    val yearlyBondsMultiplication = 1.0 + (yearlyBondsMultiplicationPercents / 100.0)
    val inflationMultiplication = 1.0 + (inflationPercents / 100.0)
    return (wantedMonthlyIncome * 12.0) / ((yearlyBondsMultiplication - inflationMultiplication) * (1.0 - tax) * (1.0 - tariff))
}

fun calcEndSum(years: Int, monthlyInvest: Double): Double {
    var sum = 0.0
    val monthsInvesting = years * 12
    for (i in 1..monthsInvesting) {
        val monthsLeft = monthsInvesting - i
        val yearlyMultiplicationPercents = getMultiplicationPercentsByMonthsLeft(monthsLeft)
        sum = calcSumNextMonth(sum, monthlyInvest, yearlyMultiplicationPercents)
    }
    return sum
}

fun calcSumNextMonth(currentSum: Double, monthlyInvest: Double, yearlyMultiplicationPercents: Double): Double {
    val monthlyMultiplication = 1.0 + (yearlyMultiplicationPercents / 12) / 100
    return monthlyMultiplication * currentSum + monthlyMultiplication * (monthlyInvest * (1.0 - tariff))
}

fun getMultiplicationPercentsByMonthsLeft(monthsLeft: Int): Double {
    val deductionMonths = 5 * 12
    return if (monthsLeft >= deductionMonths) {
        yearlyStockMultiplicationPercents
    } else {
        val deductValue =
            (yearlyStockMultiplicationPercents - yearlyBondsMultiplicationPercents) / deductionMonths.toDouble()
        yearlyBondsMultiplicationPercents + deductValue * monthsLeft
    }
}
