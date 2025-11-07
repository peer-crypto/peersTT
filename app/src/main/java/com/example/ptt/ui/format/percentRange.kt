package com.example.ptt.ui.format

fun percentRange(step: Int = 1): List<Int> =
    (0..100 step step).toList()
