package com.example.ptt.domain

import kotlin.math.ceil

object RockBottomCalculator {

    // ---- Eingabe- & Ergebnis-Typen ----

    // Ein einzelner Stop
    data class Stop(val depthM: Int, val minutes: Int)

    // Variablen
    data class Inputs(
        val bottomDepthM: Int,
        val switchDepthM: Int,
        val sacPerDiverLpm: Int,    // pro Taucher @1 ATA (inkl. Stress)
        val cylinderVolumeL: Int,   // z. B. 12
        val divers: Int = 2,
        val ascentRateMpm: Int = 10,
        val stopsBeforeSwitch: List<Stop>
    )

    // Ein Stopsegment incl. Aufstieg dorthin mit der sich daraus ergebenden Gasmenge
    data class Segment(val label: String, val gasL: Int)

    // Die Gesamtberechnung
    data class Result(
        val totalGasL: Int,
        val requiredBar: Int,
        val segments: List<Segment>
    )

    // ---- Phase 0: Druck-Helfer ----
    // Gibt den Druck auf der übergebenen Tiefe zurück
    private fun ata(depthM: Double): Double = (depthM / 10.0) + 1

    // ---- Phase 1: Legs (Phasen) bauen – nur Struktur----
    sealed interface Leg {
        data class Move(val fromM: Int, val toM: Int) : Leg   // Auf-/Abstieg (von → nach)
        data class Hold(val atM: Int, val minutes: Int) : Leg   // Stopp (bei Tiefe)
    }

    private fun buildLegs(bottomM: Int, switchM: Int, rawStops: List<Stop>): List<Leg> {
        require(bottomM >= switchM) { "Bottom depth must be >= switch depth" }

        // defensive: nur Stops im Korridor, tief → flach
        val stops = rawStops
            .filter { it.depthM in switchM..bottomM }
            .sortedByDescending { it.depthM }


        val legs = mutableListOf<Leg>()
        var current = bottomM

        for (s in stops) {
            if (s.depthM < current) {
                legs += Leg.Move(fromM = current, toM = s.depthM)
            }
            if (s.minutes > 0) {
                legs += Leg.Hold(atM = s.depthM, minutes = s.minutes)
            }
            current = s.depthM
        }

        // letzter Aufstieg bis zur Switch-Tiefe
        if (switchM < current) {
            legs += Leg.Move(fromM = current, toM = switchM)
        }

        return legs
    }

    // ---- Phase 2: Legs auswerten – Physik/Mathematik ----
    private data class Params(
        val teamSacLpm: Int,
        val ascentRateMpm: Int,
        val cylinderL: Int
    )

    private fun evaluate(legs: List<Leg>, p: Params): Result {
        val segs = mutableListOf<Segment>()
        var total = 0.0

        fun add(label: String, gas: Double) {
            val g = ceil(gas).toInt()           // konservativ je Segment
            if (g > 0) segs += Segment(label, g)
            total += gas                        // ungerundet summieren
        }

        legs.forEach { leg ->
            when (leg) {
                is Leg.Move -> {
                    val delta = (leg.fromM - leg.toM).toDouble()
                    val timeMin = if (p.ascentRateMpm > 0) delta / p.ascentRateMpm else 0.0
                    val avgM = (leg.fromM + leg.toM) / 2.0
                    val gas = p.teamSacLpm * ata(avgM) * timeMin
                    // Ausgabe je nach Move- Richtung anpassen
                    val direction = if (leg.toM < leg.fromM) "Ascent" else "Descent"
                    add("$direction ${leg.fromM}→${leg.toM} m", gas)
                }

                is Leg.Hold -> {
                    val gas = p.teamSacLpm * ata(leg.atM.toDouble()) * leg.minutes
                    add("Stop @ ${leg.atM} m (${leg.minutes} min)", gas)
                }
            }
        }

        val totalL = ceil(total).toInt()                              // am Ende einmal runden
        val bar = ceil(totalL / p.cylinderL.toDouble()).toInt()

        return Result(
            totalGasL = totalL,
            requiredBar = bar,
            segments = segs
        )
    }

    // ---- Öffentliche API (gleich geblieben) ----
    fun computeUntilSwitch(inputs: Inputs): Result {
        val legs = buildLegs(
            bottomM = inputs.bottomDepthM,
            switchM = inputs.switchDepthM,
            rawStops = inputs.stopsBeforeSwitch
        )
        val params = Params(
            teamSacLpm = inputs.sacPerDiverLpm * inputs.divers,
            ascentRateMpm = inputs.ascentRateMpm,
            cylinderL = inputs.cylinderVolumeL
        )
        return evaluate(legs, params)
    }

    // (Optional) öffentlich machen, falls du die reine Herleitung zeigen willst:
    fun buildLegsPublic(inputs: Inputs): List<Leg> =
        buildLegs(inputs.bottomDepthM, inputs.switchDepthM, inputs.stopsBeforeSwitch)
}
