package com.example.ptt.domain.calc

import com.example.ptt.domain.model.Level
import com.example.ptt.domain.model.SettingsSnapshot

fun ata(depthM: Double) = 1.0 + depthM / 10.0
 fun moveGasLiters(fromM: Double, toM: Double, s: SettingsSnapshot): Pair<Double, Double> {
    if (fromM == toM) return 0.0 to 0.0
    val delta = kotlin.math.abs(toM - fromM)
    val rate  = if (toM < fromM) s.ascentRateMpm else s.descentRateMpm
    val time  = delta / rate
    val avg   = (fromM + toM) / 2.0
    val gas   = s.sacLpm * ata(avg) * time
    return gas to time
}

// Domain-Helfer: netto-Zeit aus Tiefe, Zieltiefe und Eingabezeit
fun netLevelMinutes(fromM: Double, toM: Double, inputMinutes: Double, s: SettingsSnapshot): Double {
    val (_, moveTime) = moveGasLiters(fromM, toM, s)
    return (inputMinutes - moveTime).coerceAtLeast(0.0)
}

// Verbrauchsberechnung nutzt den Helper
fun computeUsedLiters(levels: List<Level>, s: SettingsSnapshot): Double {
    var used = 0.0
    var last = 0.0
    for (l in levels) {
        val (moveGas, _) = moveGasLiters(last, l.depthM, s)
        used += moveGas

        val netMin = netLevelMinutes(last, l.depthM, l.durationMin, s) // einmaliger Helper-Call
        used += s.sacLpm * ata(l.depthM) * netMin

        last = l.depthM
    }
    return used
}
