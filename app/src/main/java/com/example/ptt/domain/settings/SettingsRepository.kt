package com.example.ptt.domain.settings

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

object SettingsRepository {

    private val _settings = MutableStateFlow(Settings())
    val settings = _settings.asStateFlow()

    fun update(transform: (Settings) -> Settings) {
        _settings.value = transform(_settings.value)
    }
}
