package com.example.ptt.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.ptt.domain.settings.SettingsRepository
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.compose.runtime.mutableStateListOf
import kotlinx.coroutines.launch
import com.example.ptt.ui.input.toDoubleOrNullDe


class ConsumptionViewModel : ViewModel() {   // erbt von ViewModel

    // --- Domain: Datenmodelle ---

    // UI-Feld (Strings, weil TextField)
    var fillingPressureBar by mutableStateOf("200")

    data class SettingsSnapshot(
        val sacLpm: Double,
        val ascentRateMpm: Double,
        val descentRateMpm: Double,
        val cylinderVolumeL: Double
    )

    // Ein Level (Tiefe + Dauer)
    data class Level(
        val depthM: Double,
        val durationMin: Double
    )

    // Liste der Level als State
    val levels = mutableStateListOf<Level>()

    // Hieraus wird ein ConsumptionModel erzeugt, dass durch den CalculateBottom berechnet wird
    var lastBuiltModel by mutableStateOf<ConsumptionModel?>(null)


    fun setLastBuilt(model: ConsumptionModel?) {
        lastBuiltModel = model
    }

    // Wird benötigt, um die Stringwerte zu Parsen
    fun buildConsumptionModelOrNull(): ConsumptionModel? {
        val start = fillingPressureBar.toDoubleOrNullDe() ?: return null
        val snap = settingsSnapshot ?: return null
        if (levels.isEmpty()) return null

        return ConsumptionModel(
            startBar = start,
            levels = levels.toList(),   // Domain-Level: List<Level(Double, Double)>
            settings = snap
        )
    }

    // domain/model/ConsumptionModel.kt
    data class ConsumptionModel(
        val startBar: Double,
        val levels: List<Level>,
        val settings: SettingsSnapshot
    )


    sealed class Leg {
        data class LevelLeg(
            val depthM: Double,
            val durationMin:
            Double
        ) : Leg()

        data class MoveLeg(
            val fromM: Double,
            val toM: Double,
            val timeMin:
            Double
        ) : Leg()
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

    init {
        refreshSnapshot()
    }

    fun refreshSnapshot() = viewModelScope.launch {
        val s = settingsFlow.value   // oder settingsFlow.first()
        settingsSnapshot = SettingsSnapshot(
            sacLpm = s.sacPerDiver,
            ascentRateMpm = s.ascentRateMpm.toDouble(),
            descentRateMpm = s.descentRateMpm.toDouble(),
            cylinderVolumeL = s.cylinderL.toDouble()
        )
    }

    // Prüft, ob ein weiterer Level (inkl. Move) noch bis rbBar passt
    sealed class Fit {
        data object Full : Fit()
        data object Rejected : Fit()
    }


    fun addLevel(depthM: Double, durationMin: Double) {
        when (canAddAnotherLevel(depthM, durationMin)) {
            is Fit.Full -> levels += Level(depthM, durationMin)
            is Fit.Rejected -> {
                // UI: Feld rot markieren + optional Grund anzeigen
                // z.B. lastRejectReason = fit.reason
            }
        }
    }


    fun canAddAnotherLevel(depthM: Double, durationMin: Double): Fit {

        return Fit.Full

    }

    // Prüft Inline-Änderung an Position `index` (ohne Gründe, nur pass/fail)
    fun canAddLevelAt(index: Int, depthM: Double, minutes: Double): Fit {
        val s = settingsSnapshot ?: return Fit.Rejected
        val start = fillingPressureBar.toDoubleOrNullDe() ?: return Fit.Rejected

        if (start <= 0.0) return Fit.Rejected
        if (depthM < 0.0 || minutes <= 0.0) return Fit.Rejected

        // 1) Verbrauch bis vor index (bestehende Domain-Levels)
        val prior: List<Level> = levels.take(index)

        // 2) Kandidaten an Position index einsetzen
        val candidateList: List<Level> = buildList {
            addAll(prior)
            add(Level(depthM, minutes))
        }

        // 3) Verbrauch dieser Teil-Liste berechnen
        val usedLiters = computeUsedLiters(candidateList, s)
        val usedBar = usedLiters / s.cylinderVolumeL
        val remaining = start - usedBar

        return if (remaining >= 0.0) Fit.Full else Fit.Rejected
    }
    fun removeLevel(index: Int) {
        levels.removeAt(index)
    }

    private fun ata(depthM: Double) = 1.0 + depthM / 10.0

    // Verbrauch für eine Strecke (Move) von A nach B in Litern + Zeit in Minuten
    private fun moveGasLiters(fromM: Double, toM: Double, s: SettingsSnapshot): Pair<Double, Double> {
        if (fromM == toM) return 0.0 to 0.0
        val delta = kotlin.math.abs(toM - fromM)
        val rate  = if (toM < fromM) s.ascentRateMpm else s.descentRateMpm
        val time  = delta / rate
        val avgM  = (fromM + toM) / 2.0
        val gas   = s.sacLpm * ata(avgM) * time
        return gas to time
    }

    fun computeUsedLiters(levels: List<Level>, s: SettingsSnapshot): Double {
        var used = 0.0
        var lastDepth = 0.0  // Start an Oberfläche

        for (lvl in levels) {
            // Move lastDepth -> lvl.depthM
            val (moveGas, _) = moveGasLiters(lastDepth, lvl.depthM, s)
            used += moveGas

            // Level @ lvl.depthM
            used += s.sacLpm * ata(lvl.depthM) * lvl.durationMin

            lastDepth = lvl.depthM
        }
        return used
    }

}



