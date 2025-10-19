package com.example.ptt.domain

import com.example.ptt.domain.calc.moveGasLiters
import com.example.ptt.domain.calc.*
import com.example.ptt.domain.model.ConsumptionModel
import com.example.ptt.domain.model.SettingsSnapshot

object ConsumptionCalculator {

    data class ConsumptionSummary(
        val usedLiters: Double,
        val usedBar: Double,
        val remainingBar: Double
    )

    // Details: Legs
    sealed class Leg {
        data class MoveLeg(
            val fromM: Double,
            val toM: Double,
            val timeMin: Double,
            val gasL: Double,
            val avgAta: Double
        ) : Leg()

        data class LevelLeg(
            val depthM: Double,
            val minutes: Double,
            val gasL: Double,
            val ata: Double
        ) : Leg()
    }

    data class ConsumptionDetails(
        val legs: List<Leg>,
        val summary: ConsumptionSummary
    )

    fun summarize(model: ConsumptionModel): ConsumptionSummary {
        val usedL = computeUsedLiters(model.levels, model.settings)
        val usedBar = usedL / model.settings.cylinderVolumeL
        val remainingBar = model.startBar - usedBar
        return ConsumptionSummary(usedL, usedBar, remainingBar)
    }

    /**
     * Erzeugt eine Folge von Legs: Move(0->d1), Level@d1, Move(d1->d2), Level@d2, ...
     */
    fun deriveDetails(model: ConsumptionModel): ConsumptionDetails {
        val s = model.settings
        val legs = mutableListOf<Leg>()
        var last = 0.0

        for (lvl in model.levels) {
            // Move last -> lvl.depthM
            val (moveGas, moveTime) = moveGasLiters(last, lvl.depthM, s)
            if (moveTime > 0.0 || moveGas > 0.0) {
                val avgAta = ata((last + lvl.depthM) / 2.0)
                val rate   = if (lvl.depthM < last) s.ascentRateMpm else s.descentRateMpm

                legs += Leg.MoveLeg(
                    fromM = last,
                    toM = lvl.depthM,
                    timeMin = moveTime,
                    gasL = moveGas,
                    avgAta = avgAta

                )
            }

            // Level
            val levelAta = ata(lvl.depthM)
            val levelGas = s.sacLpm * levelAta * lvl.durationMin
            legs += Leg.LevelLeg(
                depthM = lvl.depthM,
                minutes = lvl.durationMin,
                gasL = levelGas,
                ata = levelAta
            )

            last = lvl.depthM
        }

        val summary = summarize(model)
        return ConsumptionDetails(legs = legs, summary = summary)
    }

}
