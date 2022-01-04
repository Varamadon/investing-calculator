package me.varam.investing

import me.varam.investing.math.findMinimizationArgument
import kotlin.math.abs

const val yearlyHighRiskMultiplicationPercents: Double = 12.0
const val yearlyLowRiskMultiplicationPercents: Double = 8.0
const val inflationPercents: Double = 4.0
const val tariffPercent: Double = 0.3
const val taxPercent: Double = 13.0


fun main() {
    val wantedMonthlyIncome = 70000.0
    val monthlyInvest = 25000.0
    val years = 20
    val yearsByMonthlyInvest = calcYearsInvestingByWantedIncomeAndMonthlyInvest(monthlyInvest, wantedMonthlyIncome)
    println(yearsByMonthlyInvest)
    val monthlyInvestByYears = calcMonthlyInvestByWantedIncomeAndYearsInvesting(years, wantedMonthlyIncome)
    println(monthlyInvestByYears)
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
    val yearlyMultiplication = yearlyLowRiskMultiplicationPercents.percentToMult()
    val inflationMultiplication = inflationPercents.percentToMult()
    return (wantedMonthlyIncome * 12.0) / ((yearlyMultiplication - inflationMultiplication) * taxPercent.percentToDec() * tariffPercent.percentToDec())
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
    val monthlyMultiplication = (yearlyMultiplicationPercents / 12).percentToMult()
    return monthlyMultiplication * currentSum + monthlyMultiplication * (monthlyInvest * tariffPercent.percentToDec())
}

fun getMultiplicationPercentsByMonthsLeft(monthsLeft: Int): Double {
    val deductionMonths = 5 * 12
    return if (monthsLeft >= deductionMonths) {
        yearlyHighRiskMultiplicationPercents
    } else {
        val deductValue =
            (yearlyHighRiskMultiplicationPercents - yearlyLowRiskMultiplicationPercents) / deductionMonths.toDouble()
        yearlyLowRiskMultiplicationPercents + deductValue * monthsLeft
    }
}

fun Double.percentToMult(): Double {
    return 1.0 + (this / 100.0)
}

fun Double.percentToDec(): Double {
    return 1.0 - (this / 100.0)
}
