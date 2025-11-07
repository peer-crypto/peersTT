package com.example.ptt.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.ptt.ui.input.toDoubleOrNullDe
import com.example.ptt.domain.model.GasMix
import com.example.ptt.domain.model.HeJumpResult
import com.example.ptt.domain.HeJumpCalculator

class HeJumpViewModel : ViewModel() {

    var fromO2Pct by mutableStateOf("18"); private set
    var fromHePct by mutableStateOf("35"); private set
    var toO2Pct   by mutableStateOf("50"); private set
    var toHePct   by mutableStateOf("0");  private set

    val fromN2Pct: String get() = pctClamp(100.0 - (fromO2Pct.toDoubleOrNullDe() ?: 0.0) - (fromHePct.toDoubleOrNullDe() ?: 0.0))
    val toN2Pct:   String get() = pctClamp(100.0 - (toO2Pct.toDoubleOrNullDe() ?: 0.0) - (toHePct.toDoubleOrNullDe() ?: 0.0))

    var result: HeJumpResult? by mutableStateOf(null); private set
    var isValid by mutableStateOf(false);             private set
    var errorMsg by mutableStateOf<String?>(null);    private set

    // Neu: zeigt an, dass Eingaben geändert wurden und neu berechnet werden muss
    var needsRecalc by mutableStateOf(true);          private set


    // needsRecalc bleibt true → User drückt später Calculate
    fun applyToMix(o2Pct: Int, hePct: Int) {
        updateTo(o2Pct = o2Pct.toString(), hePct = hePct.toString())

    }


    // needsRecalc bleibt true → User drückt später Calculate
    fun applyToMixAndCalculate(o2Pct: Int, hePct: Int) {
        updateTo(o2Pct = o2Pct.toString(), hePct = hePct.toString())
        calculate()          // direkt rechnen (du willst ja sofort das Ergebnis)
    }



    fun updateFrom(o2Pct: String? = null, hePct: String? = null) {
        o2Pct?.let { fromO2Pct = it }
        hePct?.let { fromHePct = it }
        // Ergebnis verwerfen, bis "Calculate" gedrückt wird
        markDirty()
    }

    fun updateTo(o2Pct: String? = null, hePct: String? = null) {
        o2Pct?.let { toO2Pct = it }
        hePct?.let { toHePct = it }
        markDirty()
    }

    fun calculate() {
        recompute()
        needsRecalc = false
    }

    fun buildDetailsPayload(): HeJumpResult? = result

    private fun markDirty() {
        needsRecalc = true
        result = null
        isValid = false
        errorMsg = null
    }

    private fun recompute() {
        errorMsg = null
        isValid = false
        result = null

        val fO2_from_pct = fromO2Pct.toDoubleOrNullDe()
        val fHe_from_pct = fromHePct.toDoubleOrNullDe()
        val fO2_to_pct   = toO2Pct.toDoubleOrNullDe()
        val fHe_to_pct   = toHePct.toDoubleOrNullDe()

        if (fO2_from_pct == null || fHe_from_pct == null || fO2_to_pct == null || fHe_to_pct == null) {
            errorMsg = "Bitte gültige Prozentwerte eingeben."
            return
        }
        if (!inRangePct(fO2_from_pct) || !inRangePct(fHe_from_pct) ||
            !inRangePct(fO2_to_pct)   || !inRangePct(fHe_to_pct)) {
            errorMsg = "Werte müssen zwischen 0 und 100% liegen."
            return
        }
        val sumFrom = fO2_from_pct + fHe_from_pct
        val sumTo   = fO2_to_pct + fHe_to_pct
        if (sumFrom > 100.0 + 1e-9 || sumTo > 100.0 + 1e-9) {
            errorMsg = "Summe O₂ + He darf 100% nicht überschreiten."
            return
        }

        val from = GasMix(fO2 = fO2_from_pct / 100.0, fHe = fHe_from_pct / 100.0)
        val to   = GasMix(fO2 = fO2_to_pct   / 100.0, fHe = fHe_to_pct   / 100.0)
        if (!from.isValid() || !to.isValid()) {
            errorMsg = "Gasmix ungültig (negative Anteile oder O₂+He > 1)."
            return
        }

        result = HeJumpCalculator.checkOC(from, to)
        isValid = true
    }

    private fun inRangePct(x: Double) = x in 0.0..100.0
    private fun pctClamp(x: Double): String = x.coerceIn(0.0, 100.0).toInt().toString()
}
