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


    fun setLastBuilt(model: ConsumptionModel?) {
        lastBuiltModel = model
    }

    // Wird benötigt, um die Stringwerte zu Parsen
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
            cylinderVolumeL = s.cylinderL.toDouble()
        )
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
        if (depthM < 0.0 || durationMin <= 0.0) return Fit.Rejected

        // Bisheriger Verbrauch
        val usedSoFarL = computeUsedLiters(levels, s)
        val remainingL = start * s.cylinderVolumeL - usedSoFarL
        if (remainingL < 0.0) return Fit.Rejected



// Move (Domain-Helper)
        val lastDepth = levels.lastOrNull()?.depthM ?: 0.0
        val (moveL, timeMove) = moveGasLiters(lastDepth, depthM, s)
        // Level-Verbrauch am Ziel
        val levelL = s.sacLpm * (1.0 + depthM / 10.0) * durationMin

        return if (moveL + levelL <= remainingL + 1e-9) Fit.Full else Fit.Rejected // Puffer (0,00000000001) gegen Double-Zickereien
    }

    // Im ConsumptionViewModel (nutzt Domain-Typen + computeUsedLiters aus der Domain)
    fun canAddLevelAt(index: Int, depthM: Double, minutes: Double): Fit {
        val s = settingsSnapshot ?: return Fit.Rejected
        val start = fillingPressureBar.toDoubleOrNullDe() ?: return Fit.Rejected

        // Guards
        if (start <= 0.0) return Fit.Rejected
        if (depthM < 0.0 || minutes <= 0.0) return Fit.Rejected

        // Außerhalb der Liste? → wie "Anhängen"
        if (index < 0 || index > levels.size) {
            return canAddAnotherLevel(depthM, minutes)
        }

        // 1) Verbrauch bis vor index (0..index-1)
        val prior: List<Level> = levels.take(index)

        // 2) Kandidat an Position index einsetzen
        val candidateList: List<Level> = buildList {
            addAll(prior)
            add(Level(depthM, minutes))
        }

        // 3) Verbrauch dieser Teil-Liste
        val usedLiters = computeUsedLiters(candidateList, s)
        val usedBar = usedLiters / s.cylinderVolumeL
        val remainingBar = start - usedBar

        return if (remainingBar >= 0.0) Fit.Full else Fit.Rejected
    }

    fun addLevel(depthM: Double, durationMin: Double) {
        when (canAddAnotherLevel(depthM, durationMin)) {
            Fit.Full -> levels += Level(depthM, durationMin)
            Fit.Rejected -> { /* UI markiert rot */
            }
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



