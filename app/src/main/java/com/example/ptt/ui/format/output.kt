package com.example.ptt.ui.format

import java.util.Locale

// --- Basis-Formatter für Double ---
fun fmt0(v: Double) = String.format(Locale.getDefault(), "%.0f", v)
fun fmt1(v: Double) = String.format(Locale.getDefault(), "%.1f", v)