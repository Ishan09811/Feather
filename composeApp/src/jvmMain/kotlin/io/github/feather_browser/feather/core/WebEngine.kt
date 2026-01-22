
package io.github.feather_browser.feather.core

interface WebEngine {
    fun loadUrl(url: String)
    fun goBack()
    fun goForward()
    fun reload()
    fun dispose()
}
