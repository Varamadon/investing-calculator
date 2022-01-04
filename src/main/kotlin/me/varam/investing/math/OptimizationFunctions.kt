package me.varam.investing.math

import kotlin.math.abs

const val delta: Double = 0.01
const val goldenSection: Double = 1.618
const val minimumPossibleArgument: Double = -100_000_000.0
const val maximumPossibleArgument: Double = 100_000_000.0

fun findMinimizationArgument(minimizedFunction: (Double) -> Double): Double {
    return minimizationHelper(minimizedFunction, minimumPossibleArgument, maximumPossibleArgument)
}

private fun minimizationHelper(minimizedFunction: (Double) -> Double, low: Double, high: Double): Double {
    if (abs(high - low) < delta) {
        return (high + low) / 2.0
    }
    if (low > high) throw OptimizationException("Low must be less then high!")
    val step = (high - low) / goldenSection
    val x1 = high - step
    val x2 = low + step
    val y1 = minimizedFunction(x1)
    val y2 = minimizedFunction(x2)
    return if (y1 >= y2) {
        minimizationHelper(minimizedFunction, x1, high)
    } else {
        minimizationHelper(minimizedFunction, low, x2)
    }
}

class OptimizationException(message: String) : RuntimeException(message)
