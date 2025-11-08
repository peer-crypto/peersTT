package com.example.ptt.domain.model


sealed class Recommendation {
    data class Single(val gas: GasMix): Recommendation()                // direkter Zielmix ok
    data class TwoStep(val first: GasMix, val second: GasMix): Recommendation() // via Intermediate
    object NoFeasible: Recommendation()
}
