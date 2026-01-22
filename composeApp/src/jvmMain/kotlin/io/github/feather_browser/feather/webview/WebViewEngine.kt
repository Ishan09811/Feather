
package io.github.feather_browser.feather.webview

import io.github.feather_browser.feather.core.WebEngine
import me.friwi.jcefmaven.CefAppBuilder
import org.cef.CefApp
import org.cef.CefClient
import org.cef.browser.CefBrowser
import org.cef.browser.CefFrame
import org.cef.handler.CefDisplayHandlerAdapter
import java.awt.BorderLayout
import javax.swing.JPanel


class WebViewEngine : WebEngine {

    private val cefApp: CefApp
    private val client: CefClient
    lateinit var browser: CefBrowser
    private lateinit var panel: JPanel

    private var onUrlChanged: ((String) -> Unit)? = null

    init {
        val builder = CefAppBuilder()
        builder.setInstallDir(java.io.File("jcef-runtime"))
        builder.cefSettings.windowless_rendering_enabled = false

        cefApp = builder.build()
        client = cefApp.createClient()

        client.addDisplayHandler(object : CefDisplayHandlerAdapter() {
            override fun onAddressChange(
                browser: CefBrowser,
                frame: CefFrame,
                url: String
            ) {
                onUrlChanged?.invoke(url)
            }
        })
    }

    fun setOnUrlChanged(callback: (String) -> Unit) {
        onUrlChanged = callback
    }

    fun createBrowser(initialUrl: String): JPanel {
        println("createBrowser() called with ${initialUrl}")
        if (::panel.isInitialized) return panel

        browser = client.createBrowser(initialUrl, false, false)
        val ui = browser.uiComponent

        println("Browser UI component: ${ui::class.java.name}")
        println("isDisplayable=${ui.isDisplayable} isVisible=${ui.isVisible}")

        panel = JPanel(BorderLayout()).apply {
            add(ui, BorderLayout.CENTER)
        }

        return panel
    }

    override fun loadUrl(url: String) {
        if (::browser.isInitialized) {
            browser.loadURL(url)
        }
    }

    override fun dispose() {
        if (::browser.isInitialized) browser.close(true)
        cefApp.dispose()
    }

    override fun goBack() {
        browser.goBack()
    }

    override fun goForward() {
        browser.goForward()
    }

    override fun reload() {
        browser.reload()
    }
}
