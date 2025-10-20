package com.example.ptt.domain.settings

data class Settings(
    val sacPerDiver: Double = 15.0,
    val stressFactor: Double = 2.0,
    val ascentRateMpm: Int = 15,
    val descentRateMpm: Int = 20,
    val cylinderL: Double = 24.0,
    val delayMin: Int = 2
)
