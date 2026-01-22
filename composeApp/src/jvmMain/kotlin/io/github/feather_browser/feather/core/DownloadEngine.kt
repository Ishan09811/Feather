package io.github.feather_browser.feather.core

interface DownloadEngine {
    fun start(url: String, fileName: String)
    fun pause()
    fun resume()
}
