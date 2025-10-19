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

fun computeUsedLiters(levels: List<Level>, s: SettingsSnapshot): Double {
    var used = 0.0
    var last = 0.0
    for (l in levels) {
        val (moveGas, _) = moveGasLiters(last, l.depthM, s)
        used += moveGas
        used += s.sacLpm * ata(l.depthM) * l.durationMin
        last = l.depthM
    }
    return used
}
