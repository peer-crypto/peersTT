package com.example.ptt.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class RockBottomViewModel : ViewModel() {
    // SAC pro Taucher @1 ATA (inkl. ggf. Stressfaktor, vom Nutzer selbst gewählt)
    var sacPerDiver by mutableStateOf("30") // als String für TextField

    // Zylindergröße (in Litern) und Tiefe (in Metern)
    var cylinderL by mutableStateOf("24")
    var depthM by mutableStateOf("50")

    // erste Switch-Tiefe (Meter)
    var switchDepthM by mutableStateOf("21")

    // Abgeleitet: Team-SAC
    val sacTeamLpm: Int
        get() = (sacPerDiver.toIntOrNull() ?: 0) * 2

    // Ein einzelner Deko-Stop (Tiefe + Dauer)
    data class DecoStop(var depthM: String = "", var minutes: String = "")

    // Liste der Stops als State
    val decoStops = androidx.compose.runtime.mutableStateListOf<DecoStop>()

    // Helper: Stop hinzufügen/entfernen/ändern
    fun addDecoStop(defaultDepth: String = "30", defaultMin: String = "2") {
        decoStops.add(DecoStop(defaultDepth, defaultMin))
    }

    fun removeDecoStop(index: Int) {
        if (index in decoStops.indices) decoStops.removeAt(index)
    }

    fun updateDecoStopDepth(index: Int, value: String) {
        if (index in decoStops.indices && (value.isEmpty() || value.all { it.isDigit() })) {
            decoStops[index] = decoStops[index].copy(depthM = value)
        }
    }

    fun updateDecoStopMinutes(index: Int, value: String) {
        if (index in decoStops.indices && (value.isEmpty() || value.all { it.isDigit() })) {
            decoStops[index] = decoStops[index].copy(minutes = value)
        }
    }

    // Validierung (nur „bis zum ersten Switch“):
    fun isStopInRange(depthStr: String): Boolean {
        val bottom = depthM.toIntOrNull() ?: return false
        val switch = switchDepthM.toIntOrNull() ?: return false
        val d = depthStr.toIntOrNull() ?: return false
        // Stop-Tiefe liegt zwischen Switch-Tiefe und Bottom (inklusive)
        return d in switch..bottom
    }

    // Abgeleitete, bereinigte Liste (für spätere Berechnung)
    val decoStopsParsed: List<Pair<Int, Int>>
        get() = decoStops.mapNotNull { s ->
            val d = s.depthM.toIntOrNull()
            val m = s.minutes.toIntOrNull()
            if (d != null && m != null && isStopInRange(s.depthM)) d to m else null
        }

}
