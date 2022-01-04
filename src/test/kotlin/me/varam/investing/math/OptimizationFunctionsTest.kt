package me.varam.investing.math

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class OptimizationFunctionsTest {

    @Test
    fun findMinimizationArgumentTest() {
        val linearFunction = { x: Double -> x * 3 + 5}
        assertEquals(minimumPossibleArgument, findMinimizationArgument(linearFunction), delta)

        val squarePureFunction = { x: Double -> x * x }
        assertEquals(0.0, findMinimizationArgument(squarePureFunction), delta)

        val squareFunction = { x: Double -> x * x * 5 + x * 3 + 10 }
        assertEquals(0.0, findMinimizationArgument(squarePureFunction), delta)
    }
}
