package com.example.ptt.viewmodel

import androidx.lifecycle.ViewModel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.ptt.domain.RockbottomCalculator
import com.example.ptt.domain.settings.Settings
import com.example.ptt.domain.settings.SettingsRepository
import kotlinx.coroutines.flow.StateFlow


class RockbottomViewModel : ViewModel() {   // erbt von ViewModel
    // UI-Felder (Strings, weil TextField)
    var sacPerDiver   by mutableStateOf("")
    var stressFactor  by mutableStateOf("")
    var ascentRateMpm by mutableStateOf("")
    var cylinderL     by mutableStateOf("")
    var depthM        by mutableStateOf("50")   // bleibt Pflicht (oder Settings nutzen)
    var switchDepthM  by mutableStateOf("21")
    var delayMin      by mutableStateOf("")

    // Globale Settings lesen
    val settingsFlow: StateFlow<Settings> = SettingsRepository.settings


    // Ein einzelner Deko-Stop (Tiefe + Dauer)
    data class DecoStop(var depthM: String = "",
                        var minutes: String = "")

    // Liste der Stops als State
    val decoStops = androidx.compose.runtime.mutableStateListOf<DecoStop>()

    // Helper: Stop hinzufügen/entfernen/ändern
    fun addDecoStop() {
        if (!canAddAnotherStop()) return

        val bottom = bottomDepth() ?: return
        val sw = switchDepth() ?: return

        val defaultDepth = if (decoStops.isEmpty()) {
            firstStopDepth(bottom)
        } else {
            val last = decoStops.last().depthM.toIntOrNull() ?: bottom
            nextStopAfter(last)
        }

        // Sicherheitsnetz (sollte durch canAddAnotherStop() schon gewährleistet sein)
        if (defaultDepth <= sw || defaultDepth > bottom) return

        decoStops.add(DecoStop(depthM = defaultDepth.toString(), minutes = "1"))
    }

    // Helper: Stops aufsteigend
    private fun bottomDepth(): Int? = depthM.toIntOrNull()
    private fun switchDepth(): Int? = switchDepthM.toIntOrNull()


    // Erster Stop Hälfte der Bottom-Tiefe, bzw. deren erste größere Zahl die durch 3 teilbar ist

    private fun firstStopDepth(bottom: Int): Int {
        val half = bottom / 2
        return if (half % 3 == 0) half else (half - (half % 3)) + 3
    }

    // Nächster 3 m flacher, auf 3er-Raster ausrichten
    private fun nextStopAfter(lastDepth: Int): Int {
        val base = lastDepth - 3
        return base - (base % 3)
    }

    // weiterer Stop möglich?
    fun canAddAnotherStop(): Boolean {
        if (decoStops.size >= 8) return false
        val bottom = bottomDepth() ?: return false
        val sw = switchDepth() ?: return false

        val candidate = if (decoStops.isEmpty()) {
            firstStopDepth(bottom)
        } else {
            val last = decoStops.last().depthM.toIntOrNull() ?: return false
            nextStopAfter(last)
        }

        // Nur falls nächster Stop tiefer als Switch-Tiefe
        return candidate in (sw + 1)..bottom
    }

    private fun isDepthInRange(depth: Int): Boolean {
        val bottom = bottomDepth() ?: return false
        val sw = switchDepth() ?: return false
        return depth in sw..bottom
    }

    //Gültig, wenn: nicht leer, Zahl, im Bereich, und monotone Reihenfolge (tief -> flach).
    fun isStopDepthValidAt(index: Int): Boolean {
        if (index !in decoStops.indices) return false
        val self = decoStops[index].depthM.toIntOrNull() ?: return false
        if (!isDepthInRange(self)) return false

        val prev = if (index > 0) decoStops[index - 1].depthM.toIntOrNull() else null
        val next =
            if (index < decoStops.lastIndex) decoStops[index + 1].depthM.toIntOrNull() else null
        val okPrev = prev?.let { self <= it } ?: true
        val okNext = next?.let { self >= it } ?: true
        return okPrev && okNext
    }

    // Minuten sind gültig, wenn: nicht leer, Zahl (>=0).
    fun isStopMinutesValidAt(index: Int): Boolean {
        if (index !in decoStops.indices) return false
        return decoStops[index].minutes.toIntOrNull() != null
    }

    //Mindestens ein Stop ist ungültig? → true
    val hasInvalidStops: Boolean
        get() = decoStops.indices.any { !isStopDepthValidAt(it) || !isStopMinutesValidAt(it) }

    fun removeDecoStop(index: Int) {
        if (index in decoStops.indices) decoStops.removeAt(index)
    }

    // Eingaben: immer übernehmen (wenn nur Ziffern/leer)
    fun updateDecoStopDepth(index: Int, value: String) {
        if (index !in decoStops.indices) return
        if (value.isEmpty() || value.all(Char::isDigit)) {
            decoStops[index] = decoStops[index].copy(depthM = value)
        }
    }


    fun updateDecoStopMinutes(index: Int, value: String) {
        if (index !in decoStops.indices) return
        if (value.isEmpty() || value.all(Char::isDigit)) {
            decoStops[index] = decoStops[index].copy(minutes = value)
        }
    }

    // Ergebniszustand für die UI
    var calcGasL by mutableStateOf<Int?>(null)
        private set
    var calcBar by mutableStateOf<Int?>(null)
        private set
    var calcSegments by mutableStateOf<List<RockbottomCalculator.Segment>>(emptyList())
        private set



        fun calculateRockbottom() {

            if (hasInvalidStops) {
                calcGasL = null; calcBar = null; calcSegments = emptyList()
                return
            }

            fun parseDoubleOrNullIfNotBlank(txt: String): Double? =
                txt.trim().takeIf { it.isNotBlank() }?.replace(',', '.')?.toDoubleOrNull()

            fun parseIntOrNullIfNotBlank(txt: String): Int? =
                txt.trim().takeIf { it.isNotBlank() }?.toIntOrNull()

            val s = SettingsRepository.settings.value

            // Stringeingaben Parsen
            val sac = parseDoubleOrNullIfNotBlank(sacPerDiver) ?: s.sacPerDiver
            val cyl = parseDoubleOrNullIfNotBlank(cylinderL) ?: s.cylinderL
            val rate = parseIntOrNullIfNotBlank(ascentRateMpm) ?: s.ascentRateMpm
            val delay = parseIntOrNullIfNotBlank(delayMin) ?: s.delayMin
            val stress = parseDoubleOrNullIfNotBlank(stressFactor) ?: s.stressFactor
            val bottom = parseIntOrNullIfNotBlank(depthM) ?: return
            val switch = parseIntOrNullIfNotBlank(switchDepthM) ?: return


            val stops = decoStops.mapNotNull {
                val d = it.depthM.toIntOrNull()
                val m = it.minutes.toIntOrNull()
                if (d != null && m != null) RockbottomCalculator.Stop(d, m) else null
            }

            val res = RockbottomCalculator.computeUntilSwitch(
                RockbottomCalculator.Inputs(
                    bottomDepthM = bottom,
                    switchDepthM = switch,
                    sacPerDiverLpm = sac,      // Double
                    stressFactor = stress,   // Double
                    cylinderL = cyl,      // noch Int !!!
                    ascentRateMpm = rate,     // Int
                    delayMin = delay,    // Int
                    stopsBeforeSwitch = stops
                )
            )

            calcGasL = kotlin.math.ceil(res.totalGasL).toInt()
            calcBar = kotlin.math.ceil(res.requiredBar).toInt()
            calcSegments = res.segments
        }

    // Variablenumformung für DetailsScreen
    fun effectiveCylinderL(): Double {
        val s = SettingsRepository.settings.value
        return cylinderL.trim().toDoubleOrNull() ?: s.cylinderL
    }

    fun effectiveDelayMin(): Int {
        val s = SettingsRepository.settings.value
        return delayMin.trim().toIntOrNull() ?: s.delayMin
    }

    fun effectiveSac(): Double {
        val s = SettingsRepository.settings.value
        val parsed = sacPerDiver
            .trim()
            .replace(',', '.')      // "14,5" -> "14.5"
            .toDoubleOrNull()
        return parsed ?: s.sacPerDiver.toDouble()  // falls s.sacPerDiver schon Double ist: ohne .toDouble()
    }

    fun effectivStressFactor    (): Double {
        val s = SettingsRepository.settings.value
        val parsed = stressFactor
            .trim()
            .replace(',', '.')
            .toDoubleOrNull()
        return parsed ?: s.stressFactor.toDouble()
    }

        // kleine Helper (optional)
        fun Double.roundToIntSafely(): Int =
            kotlin.math.ceil(this).toInt()
    }


