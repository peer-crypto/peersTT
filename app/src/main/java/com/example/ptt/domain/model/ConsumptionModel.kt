package com.example.ptt.domain.model



data class Level(val depthM: Double, val durationMin: Double)

data class SettingsSnapshot(
    val sacLpm: Double,
    val ascentRateMpm: Double,
    val descentRateMpm: Double,
    val cylinderVolumeL: Double,
)

data class ConsumptionModel(
    val startBar: Double,
    val levels: List<Level>,
    val settings: SettingsSnapshot
)
