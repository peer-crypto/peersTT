package com.example.ptt.domain

import kotlin.math.roundToInt


/**
 * Encapsulates all domain logic for deep stop calculation.
 * Keep this pure (no Compose), so it’s easy to unit test.
 */
object DeepstopCalculator {
    data class Result(
        val maxPressure: Int,
        val stopPressure: Int,
        val deepstop: Int
    )


    fun compute(selectedDepthMeters: Int): Result {
        val maxPressure = selectedDepthMeters + 10
        val stopPressure = (maxPressure / 1.35).roundToInt()
        val deepstop = stopPressure - 10
        return Result(maxPressure, stopPressure, deepstop)
    }
}