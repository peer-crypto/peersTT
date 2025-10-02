package com.example.ptt.viewmodel

import androidx.lifecycle.ViewModel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.ptt.domain.DeepstopCalculator


class DeepstopViewModel : ViewModel() {
    val options: List<Int> = (40..100 step 5).toList()


    var selectedDepth by mutableStateOf(options.first())
        private set


    val result: DeepstopCalculator.Result
        get() = DeepstopCalculator.compute(selectedDepth)


    fun onDepthSelected(value: Int) {
        selectedDepth = value
    }
}