package com.example.ptt.ui.input

// Eingaben Parsen incl , durch . ersetzen
fun String.toDoubleOrNullDe(): Double? =
    replace(',', '.').toDoubleOrNull()