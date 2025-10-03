package com.example.ptt.domain

import kotlin.math.ceil

object RockBottomCalculator {

    // Deko-Stop (Tiefe & Minuten)
    data class Stop(val depthM: Int, val minutes: Int)

    data class Inputs(
        val bottomDepthM: Int,
        val switchDepthM: Int,
        val sacPerDiverLpm: Int,          // SAC pro Taucher @1 ATA (inkl. Stress)
        val cylinderVolumeL: Int,          // z. B. 12
        val divers: Int = 2,               // immer 2
        val ascentRateMpm: Int = 10,       // m/min (fix, vorerst)
        val stopsBeforeSwitch: List<Stop>  // nur Stops >= switchDepth und <= bottom
    )

    data class Segment(val label: String, val gasL: Int)
    data class Result(
        val totalGasL: Int,
        val requiredBar: Int,
        val segments: List<Segment>
    )

    private fun ata(depthM: Double): Double = 1.0 + depthM / 10.0

    fun computeUntilSwitch(inputs: Inputs): Result {
        require(inputs.bottomDepthM >= inputs.switchDepthM) {
            "Bottom depth must be >= switch depth"
        }
        val teamSac = inputs.sacPerDiverLpm * inputs.divers  // Gesamt-SAC aller Taucher

        // Stops sortieren, obwohl bei Eingabe bereits richtige Reihenfolge erzwungen wird. Sicher ist sicher
        val stops = inputs.stopsBeforeSwitch
            .filter { it.depthM in inputs.switchDepthM..inputs.bottomDepthM }
            .sortedByDescending { it.depthM }

        val segs = mutableListOf<Segment>()
        var currentDepth = inputs.bottomDepthM
        var total = 0.0

        fun addAscent(toDepth: Int, label: String) {
            if (toDepth >= currentDepth) return
            val delta = (currentDepth - toDepth).toDouble()
            val timeMin = if (inputs.ascentRateMpm > 0) delta / inputs.ascentRateMpm else 0.0
            val avgDepth = (currentDepth + toDepth) / 2.0
            val gas = teamSac * ata(avgDepth) * timeMin
            val g = ceil(gas).toInt()
            if (g > 0) segs += Segment(label, g)
            total += gas
            currentDepth = toDepth
        }
        fun addStop(depth: Int, minutes: Int, label: String) {
            if (minutes <= 0) return
            val gas = teamSac * ata(depth.toDouble()) * minutes
            val g = ceil(gas).toInt()
            if (g > 0) segs += Segment(label, g)
            total += gas
        }

        // Ascente & Stops bis zum Switch
        stops.forEachIndexed { i, s ->
            addAscent(s.depthM, label = "Ascent to ${s.depthM} m")
            addStop(s.depthM, s.minutes, label = "Stop @ ${s.depthM} m (${s.minutes} min)")
        }

        // Letzter Ascent bis zur Switch-Tiefe
        addAscent(inputs.switchDepthM, label = "Ascent to switch ${inputs.switchDepthM} m")

        val totalL = ceil(total).toInt()
        val bar = ceil(totalL / inputs.cylinderVolumeL.toDouble()).toInt()

        return Result(
            totalGasL = totalL,
            requiredBar = bar,
            segments = segs
        )
    }
}

