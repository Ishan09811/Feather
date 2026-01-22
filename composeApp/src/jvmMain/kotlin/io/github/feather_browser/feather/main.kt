
package io.github.feather_browser.feather

import io.github.feather_browser.feather.ui.BrowserToolbar
import io.github.feather_browser.feather.webview.WebViewEngine
import java.awt.BorderLayout
import java.awt.event.MouseEvent
import java.awt.event.WindowAdapter
import java.awt.event.WindowEvent
import java.awt.event.MouseAdapter
import javax.swing.JFrame
import javax.swing.SwingUtilities

fun main() {
    SwingUtilities.invokeLater {
        val frame = JFrame("Feather")
        frame.isUndecorated = true
        frame.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
        frame.layout = BorderLayout()

        val engine = WebViewEngine()

        var currentUrl = "https://google.com"

        val toolbar = BrowserToolbar(
            initialUrl = "https://google.com",
            onUrlChange = { url ->
                currentUrl = url
            },
            onNavigate = { engine.loadUrl(currentUrl) },
            frame = frame
        )

        val browserPanel = engine.createBrowser(currentUrl)

        frame.add(toolbar, BorderLayout.NORTH)
        frame.add(browserPanel, BorderLayout.CENTER)

        frame.setSize(1200, 800)
        frame.setLocationRelativeTo(null)

        engine.browser.uiComponent.addMouseListener(object : MouseAdapter() {
            override fun mousePressed(e: MouseEvent) {
                toolbar.textField.caret.isVisible = false
            }
        })

        engine.setOnUrlChanged { url ->
            toolbar.textField.text = url
        }

        frame.isVisible = true
        frame.revalidate()
        frame.repaint()

        println("Frame children:")
        frame.contentPane.components.forEach {
            println(" - ${it::class.java.name} bounds=${it.bounds}")
        }

        frame.addWindowListener(object : WindowAdapter() {
            override fun windowClosing(e: WindowEvent?) {
                engine.dispose()
            }
        })
    }
}