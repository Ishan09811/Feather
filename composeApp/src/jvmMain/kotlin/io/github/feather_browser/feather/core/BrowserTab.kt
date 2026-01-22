package io.github.feather_browser.feather.core

data class BrowserTab(
    val id: Int,
    val engine: WebEngine,
    var url: String,
    var isActive: Boolean = true
)
