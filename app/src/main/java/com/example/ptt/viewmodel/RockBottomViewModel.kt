package com.example.ptt.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.ptt.domain.RockBottomCalculator

class RockBottomViewModel : ViewModel() {
    // SAC pro Taucher @1 ATA (inkl. ggf. Stressfaktor, vom Nutzer selbst gewählt)
    var sacPerDiver by mutableStateOf("30") // als String für TextField

    // Zylindergröße (in Litern) und Tiefe (in Metern)
    var cylinderL by mutableStateOf("24")
    var depthM by mutableStateOf("50")

    // erste Switch-Tiefe (Meter)
    var switchDepthM by mutableStateOf("21")

    // Ein einzelner Deko-Stop (Tiefe + Dauer)
    data class DecoStop(var depthM: String = "", var minutes: String = "")

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
        return if (half % 3 == 0) half else half - (half % 3)
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
        val next = if (index < decoStops.lastIndex) decoStops[index + 1].depthM.toIntOrNull() else null
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
    var calcSegments by mutableStateOf<List<RockBottomCalculator.Segment>>(emptyList())
        private set

    fun calculateRockBottom() {

        if (hasInvalidStops) {
            calcGasL = null; calcBar = null; calcSegments = emptyList()
            return
        }
        // Parsing & einfache Validierung
        val sac = sacPerDiver.toIntOrNull()
        val cyl = cylinderL.toIntOrNull()
        val bottom = depthM.toIntOrNull()
        val switch = switchDepthM.toIntOrNull()

        if (sac == null || cyl == null || bottom == null || switch == null) {
            calcGasL = null; calcBar = null; calcSegments = emptyList(); return
        }
        if (bottom < switch) {
            calcGasL = null; calcBar = null; calcSegments = emptyList(); return
        }

        val stops = decoStops.mapNotNull {
            val d = it.depthM.toIntOrNull()
            val m = it.minutes.toIntOrNull()
            if (d != null && m != null) RockBottomCalculator.Stop(d, m) else null
        }

        val res = RockBottomCalculator.computeUntilSwitch(
            RockBottomCalculator.Inputs(
                bottomDepthM = bottom,
                switchDepthM = switch,
                sacPerDiverLpm = sac,
                cylinderVolumeL = cyl,
                stopsBeforeSwitch = stops
            )
        )
        calcGasL = res.totalGasL
        calcBar = res.requiredBar
        calcSegments = res.segments
    }
}


