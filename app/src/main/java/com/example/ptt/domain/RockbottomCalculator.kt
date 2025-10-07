package com.example.ptt.domain

import kotlin.collections.plusAssign
import kotlin.math.ceil

object RockbottomCalculator {

    // ---- Eingabe- & Ergebnis-Typen ----

    // Ein einzelner Stop
    data class Stop(val depthM: Int, val minutes: Int)

    // Variablen
    data class Inputs(
        val bottomDepthM: Int,
        val switchDepthM: Int,
        val sacPerDiverLpm: Int,    // pro Taucher @1 ATA (inkl. Stress)
        val cylinderVolumeL: Int,   // z. B. 12
        val divers: Int = 2,        // Anzahl Taucher zur Verbrauchsberechnung
        val ascentRateMpm: Int,
        val delayMin: Int=0,  // Verzögerung bis Aufstieg
        val stopsBeforeSwitch: List<Stop>
    )

    // Ein Stopsegment incl. Aufstieg dorthin mit der sich daraus ergebenden Gasmenge
    data class Segment(
        val label: String,
        val gasL: Int,
        val formula: String)

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

    private fun buildLegs(delayMin: Int, bottomM: Int, switchM: Int, rawStops: List<Stop>): List<Leg> {
        require(bottomM >= switchM) { "Bottom depth must be >= switch depth" }

        // defensive: nur Stops im Korridor, tief → flach
        val stops = rawStops
            .filter { it.depthM in switchM..bottomM }
            .sortedByDescending { it.depthM }


        val legs = mutableListOf<Leg>()
        var currentDepth = bottomM

        // optionaler Verzögerungs-Abschnitt vor dem Aufstieg
        if (delayMin > 0) {
            legs += Leg.Hold(atM = currentDepth, minutes = delayMin)

            for (s in stops) {
                if (s.depthM < currentDepth) {
                    legs += Leg.Move(fromM = currentDepth, toM = s.depthM)
                }
                if (s.minutes > 0) {
                    legs += Leg.Hold(atM = s.depthM, minutes = s.minutes)
                }
                currentDepth = s.depthM
            }

            // letzter Aufstieg bis zur Switch-Tiefe
            if (switchM < currentDepth) {
                legs += Leg.Move(fromM = currentDepth, toM = switchM)
            }
        }
            return legs

    }
        // ---- Phase 2: Legs auswerten – Physik/Mathematik ----
        private data class Params(
            val teamSacLpm: Int,
            val ascentRateMpm: Int,
            val cylinderL: Int,
        )

        private fun evaluate(legs: List<Leg>, p: Params): Result {
            val segs = mutableListOf<Segment>()
            var total = 0.0

            //Überladene Funktion zur Umwandlung einer Zahl in einen String mit zwei Nachkommastellen und , als Dezimalzeichen
            // Für Double-Werte
            fun fmt(d: Double, decimals: Int = 1): String =
                String.format(java.util.Locale.GERMAN, "%.${decimals}f", d)

            // Überladung für Int-Werte
            fun fmt(d: Int, decimals: Int = 0): String =
                fmt(d.toDouble(), decimals)


            fun addSeg(label: String, gas: Double, formula: String) {
                val g = kotlin.math.ceil(gas).toInt()   // Anzeige aufrunden
                if (g > 0) segs += Segment(label = label, gasL = g, formula = formula)
                total += gas                            // ungerundet summieren
            }

            legs.forEach { leg ->
                when (leg) {
                    is Leg.Move -> {
                        val deltaM = (leg.fromM - leg.toM).toDouble()
                        val minutes = if (p.ascentRateMpm > 0)
                            kotlin.math.abs(deltaM) / p.ascentRateMpm
                        else 0.0

                        val avgM = (leg.fromM + leg.toM) / 2.0
                        val ata = ata(avgM) // z.B. 1 + avgM/10
                        val gas = p.teamSacLpm * ata * minutes

                        val direction = if (leg.toM < leg.fromM) "Ascent" else "Descent"
                        val label = "$direction ${leg.fromM}→${leg.toM} m"
                        val formula = "${fmt(p.teamSacLpm,0)} × ${fmt(ata)} × ${fmt(minutes)}"

                        addSeg(label, gas, formula)
                    }
                    is Leg.Hold -> {
                        val ata = ata(leg.atM.toDouble())
                        val minutes = leg.minutes.toDouble()
                        val gas = p.teamSacLpm * ata * minutes

                        val label = "Stop @ ${leg.atM} m (${leg.minutes} min)"
                        val formula = "${fmt(p.teamSacLpm,0)} × ${fmt(ata)} × ${fmt(minutes)}"

                        addSeg(label, gas, formula)
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


        // ---- Öffentliche API ----
        fun computeUntilSwitch(inputs: Inputs): Result {
            val legs = buildLegs(
                bottomM = inputs.bottomDepthM,
                switchM = inputs.switchDepthM,
                rawStops = inputs.stopsBeforeSwitch, // das ist die Stop-Liste
                delayMin = inputs.delayMin
            )
            val params = Params(
                teamSacLpm = inputs.sacPerDiverLpm * inputs.divers,
                ascentRateMpm = inputs.ascentRateMpm,
                cylinderL = inputs.cylinderVolumeL,
            )

            return evaluate(legs, params)
        }
    }
