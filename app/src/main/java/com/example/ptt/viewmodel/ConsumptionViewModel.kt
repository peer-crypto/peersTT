package com.example.ptt.viewmodel

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel


// DOMAIN
import com.example.ptt.domain.model.ConsumptionModel
import com.example.ptt.domain.model.Level
import com.example.ptt.domain.model.SettingsSnapshot
import com.example.ptt.domain.ConsumptionCalculator
import com.example.ptt.domain.settings.SettingsRepository
import com.example.ptt.domain.calc.*

// Utils (für Parsing)
import com.example.ptt.ui.input.toDoubleOrNullDe


class ConsumptionViewModel : ViewModel() {   // erbt von ViewModel

    // UI-Feld (Strings, weil TextField)
    var fillingPressureBar by mutableStateOf("200")


    // Liste der Levels (Domain-Typ)
    val levels = mutableStateListOf<Level>()


    // Hieraus wird ein ConsumptionModel erzeugt, dass durch den CalculateBottom berechnet wird
    var lastBuiltModel by mutableStateOf<ConsumptionModel?>(null)
        private set

    // Defaultwerte bei leerer Liste
    object Defaults {
        const val DEPTH_M  = 6.0
        const val MINUTES  = 10.0
    }
    // true, wenn die Defaults vorgeschlagen werden sollen
    fun shouldPrefillDefaults(): Boolean = levels.isEmpty()

    fun defaultDepthStrOrEmpty(): String =
        if (shouldPrefillDefaults()) Defaults.DEPTH_M.toInt().toString() else ""

    fun defaultMinutesStrOrEmpty(): String =
        if (shouldPrefillDefaults()) Defaults.MINUTES.toInt().toString() else ""

    fun setLastBuilt(model: ConsumptionModel?) {
        lastBuiltModel = model
    }

    // Stringwerte parsen
    fun buildConsumptionModelOrNull(): ConsumptionModel? {
        val snap = settingsSnapshot ?: return null
        val start = fillingPressureBar.toDoubleOrNullDe() ?: return null
        if (levels.isEmpty()) return null

        // Falls deine vm.levels bereits Domain-Level (Double, Double) sind:
        return ConsumptionModel(
            startBar = start,
            levels = levels.toList(),
            settings = snap
        )
    }

    // Snapshot der Werte aus Settings erzeugen
    var settingsSnapshot by mutableStateOf<SettingsSnapshot?>(null); private set
    private val settingsFlow = SettingsRepository.settings

    init {
        refreshSnapshot()
    }

    fun refreshSnapshot() {
        val s = settingsFlow.value
        settingsSnapshot = SettingsSnapshot(
            sacLpm = s.sacPerDiver,
            ascentRateMpm = s.ascentRateMpm.toDouble(),
            descentRateMpm = s.descentRateMpm.toDouble(),
            cylinderVolumeL = s.cylinderL.toDouble(),
        )
    }

    // Berechnet die Nettozeit des eingegebenen Levels
    private fun netLevelMinutes(fromM: Double, toM: Double, inputMinutes: Double, s: SettingsSnapshot
    ): Double {
        val (_, moveTime) = moveGasLiters(fromM, toM, s)
        return (inputMinutes - moveTime).coerceAtLeast(0.0)
    }

    // Prüft, ob ein weiterer Level (inkl. Move) noch bis rbBar passt
    sealed class Fit {
        data object Full : Fit()
        data object Rejected : Fit()
    }

    fun canAddAnotherLevel(depthM: Double, durationMin: Double): Fit {
        val s = settingsSnapshot ?: return Fit.Rejected
        val start = fillingPressureBar.toDoubleOrNullDe() ?: return Fit.Rejected
        if (start <= 0.0) return Fit.Rejected
        if (depthM < 0.0 || durationMin < 0.0) return Fit.Rejected  // 0 ist erlaubt

        // Bisheriger Verbrauch
        val usedSoFarL = computeUsedLiters(levels, s)
        val remainingL = start * s.cylinderVolumeL - usedSoFarL
        if (remainingL < 0.0) return Fit.Rejected

        // Move
        val lastDepth = levels.lastOrNull()?.depthM ?: 0.0
        val (moveL, /* timeMove */) = moveGasLiters(lastDepth, depthM, s)

        // Netto-Levelzeit (Eingabe minus Move-Zeit, nie < 0)
        val netMin = netLevelMinutes(lastDepth, depthM, durationMin, s)

        // Level-Verbrauch mit NETTO-Minuten
        val levelGasNet = s.sacLpm * (1.0 + depthM / 10.0) * netMin

        // Puffer (0,00000000001) gegen Double-Zickereien
        val epsilon = 1e-9
        return if (moveL + levelGasNet <= remainingL + epsilon) Fit.Full else Fit.Rejected
    }


    fun addLevel(depthM: Double, durationMin: Double) {
        when (canAddAnotherLevel(depthM, durationMin)) {
            Fit.Full -> {
                val s = settingsSnapshot ?: return
                val lastDepth = levels.lastOrNull()?.depthM ?: 0.0
                levels += Level(depthM, durationMin)   // Brutto anzeigen
            }
            Fit.Rejected -> { /* UI markiert rot */ }
        }
    }

    fun removeLevel(index: Int) {
        if (index in levels.indices) levels.removeAt(index)
    }

    // In domain berechnen
    var lastSummary by mutableStateOf<ConsumptionCalculator.ConsumptionSummary?>(null)
        private set

    fun buildAndSummarize(): Boolean {
        val model = buildConsumptionModelOrNull() ?: return false
        lastBuiltModel = model
        lastSummary = ConsumptionCalculator.summarize(model)
        return true
    }
}



