
package io.github.feather_browser.feather

import com.formdev.flatlaf.FlatDarkLaf
import com.formdev.flatlaf.FlatLaf
import io.github.feather_browser.feather.ui.compose.BrowserToolbar as ComposeBrowserToolbar
import io.github.feather_browser.feather.ui.swing.BrowserToolbar as SwingBrowserToolbar
import io.github.feather_browser.feather.webview.WebViewEngine
import java.awt.BorderLayout
import java.awt.event.MouseEvent
import java.awt.event.WindowAdapter
import java.awt.event.WindowEvent
import java.awt.event.MouseAdapter
import javax.swing.JFrame
import javax.swing.JLabel
import javax.swing.SwingUtilities
import com.formdev.flatlaf.FlatLightLaf
import io.github.feather_browser.feather.ui.swing.ThemeManager.isDarkMode

fun main() {
    lateinit var frame: JFrame
    lateinit var loadingLabel: JLabel
    val onBuildProgress: (String) -> Unit = { progress ->
        SwingUtilities.invokeLater {
            loadingLabel.text = progress
            loadingLabel.repaint()
        }
    }

    SwingUtilities.invokeLater {
        if (isDarkMode()) {
            FlatLaf.setup(FlatDarkLaf())
        } else {
            FlatLaf.setup(FlatLightLaf())
        }
        FlatLaf.updateUILater()
        frame = JFrame("Feather")
        frame.isUndecorated = false
        frame.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
        frame.layout = BorderLayout()

        loadingLabel = JLabel("Loading…", JLabel.CENTER)
        frame.add(loadingLabel, BorderLayout.CENTER)

        frame.setSize(1200, 800)
        frame.setLocationRelativeTo(null)
        frame.isVisible = true
    }

    Thread {
        val engine = WebViewEngine(onBuildProgress)

        SwingUtilities.invokeLater {
            var currentUrl = "https://google.com"

            // TODO: Fix Compose BrowserToolbar ui
            /*val toolbar = ComposeBrowserToolbar(
                frame = frame,
                initialUrl = currentUrl,
                onUrlChange = { url ->
                    currentUrl = url
                },
                onNavigate = { engine.loadUrl(currentUrl) },
                onBack = { engine.goBack() },
                onForward = { engine.goForward() }
            )*/

            val toolbar = SwingBrowserToolbar(
                initialUrl = currentUrl,
                onUrlChange = { url ->
                    currentUrl = url
                },
                onNavigate = { engine.loadUrl(currentUrl) },
                onBack = { engine.goBack() },
                onForward = { engine.goForward() }
            )

            val browserPanel = engine.createBrowser(currentUrl)

            frame.contentPane.removeAll()
            frame.rootPane.putClientProperty("FlatLaf.fullWindowContent", true)
            frame.add(toolbar, BorderLayout.NORTH)
            frame.add(browserPanel, BorderLayout.CENTER)

            frame.setSize(1200, 800)
            frame.setLocationRelativeTo(null)

            engine.browser.uiComponent.addMouseListener(object : MouseAdapter() {
                override fun mousePressed(e: MouseEvent) {
                    if (toolbar is SwingBrowserToolbar) {
                        toolbar.textField.caret.isVisible = false
                    } else {
                        // TODO: Fix Compose BrowserToolbar ui
                    }
                }
            })

            engine.setOnUrlChanged { url ->
                if (toolbar is SwingBrowserToolbar) toolbar.textField.text = url else currentUrl = url
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
    }.start()
}
