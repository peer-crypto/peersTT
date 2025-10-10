package com.example.ptt.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.ptt.domain.settings.SettingsRepository
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch


class ConsumptionViewModel: ViewModel() {   // erbt von ViewModel

    // --- Domain: Datenmodelle ---

    data class SettingsSnapshot(
        val sacLpm: Double,
        val ascentRateMpm: Double,
        val descentRateMpm: Double,
        val cylinderVolumeL: Double
    )

    data class Level(
        val depthM: Double,
        val durationMin: Double
    )

    data class ConsumptionModel(
        val startBar: Double,
        val rbBar: Double,
        val levels: List<Level>,
        val settings: SettingsSnapshot
    )

    sealed class Leg {
        data class LevelLeg(val depthM: Double,
                            val durationMin:
                            Double) : Leg()

        data class MoveLeg(val fromM: Double,
                           val toM: Double,
                           val timeMin:
                           Double) : Leg()
    }

    data class IntegrationResult(
        val legsUntilRB: List<Leg>,      // inkl. letztem „partial“-Leg
        val reachedRB: Boolean,          // true, wenn exakt an rbBar gestoppt
        val timeToRBMin: Double,         // aufsummierte Minuten bis RB (oder Gesamtzeit, falls nicht erreicht)
        val consumedLiters: Double,      // bis RB (oder total)
        val consumedBar: Double,         // = liters / cylinderVolumeL
        val marginBar: Double?,          // falls RB nicht erreicht
        val marginLiters: Double?        // falls RB nicht erreicht
    )
 // Snapshot der Werte aus Settings erzeugen
    var settingsSnapshot by mutableStateOf<SettingsSnapshot?>(null); private set
    private val settingsFlow = SettingsRepository.settings

    init { refreshSnapshot() }

    fun refreshSnapshot() = viewModelScope.launch {
        val s = settingsFlow.value   // oder settingsFlow.first()
        settingsSnapshot = SettingsSnapshot(
            sacLpm = s.sacPerDiver,
            ascentRateMpm = s.ascentRateMpm.toDouble(),
            descentRateMpm = s.descentRateMpm.toDouble(),
            cylinderVolumeL = s.cylinderL.toDouble()
        )
    }

}



