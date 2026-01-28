
package io.github.feather_browser.feather

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.formdev.flatlaf.FlatDarkLaf
import com.formdev.flatlaf.FlatLaf
import com.formdev.flatlaf.FlatLightLaf
import io.github.feather_browser.feather.ui.swing.ThemeManager.isDarkMode
import io.github.feather_browser.feather.webview.WebViewEngine
import java.awt.BorderLayout
import java.awt.GraphicsEnvironment
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.awt.event.WindowAdapter
import java.awt.event.WindowEvent
import javax.swing.JFrame
import javax.swing.JLabel
import javax.swing.SwingUtilities
import javax.swing.UIManager
import io.github.feather_browser.feather.ui.compose.BrowserToolbar as ComposeBrowserToolbar
import io.github.feather_browser.feather.ui.swing.BrowserToolbar as SwingBrowserToolbar


fun main() {
    lateinit var frame: JFrame
    lateinit var loadingLabel: JLabel
    val device = GraphicsEnvironment.getLocalGraphicsEnvironment().defaultScreenDevice
    val isComposeEnabled = true

    val onBuildProgress: (String) -> Unit = { progress ->
        SwingUtilities.invokeLater {
            loadingLabel.text = progress
            loadingLabel.repaint()
        }
    }

    val onFullscreenModeChange: (Boolean) -> Unit = { fullscreen ->
        SwingUtilities.invokeLater {
            if (fullscreen) {
                frame.dispose()
                frame.isUndecorated = true
                frame.contentPane.getComponent(0).isVisible = false
                device.fullScreenWindow = frame
                frame.isVisible = true
            } else {
                device.fullScreenWindow = null
                frame.dispose()
                frame.isUndecorated = false
                frame.contentPane.getComponent(0).isVisible = true
                frame.isVisible = true
            }
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
        val engine = WebViewEngine(onBuildProgress, onFullscreenModeChange)

        SwingUtilities.invokeLater {
            var currentUrl by mutableStateOf("https://google.com")

            // TODO: implement settings
            val toolbar = if (isComposeEnabled) {
                ComposeBrowserToolbar(
                    onBackgroundColorChange = {
                        if (it != UIManager.getColor("Panel.background")) {
                            UIManager.put("Panel.background", it)
                            FlatLaf.updateUI()
                        }
                    },
                    frame = frame,
                    initialUrl = currentUrl,
                    onUrlChange = { url ->
                        currentUrl = url
                    },
                    onNavigate = { engine.loadUrl(currentUrl) },
                    onBack = { engine.goBack() },
                    onForward = { engine.goForward() }
                )
            } else {
                SwingBrowserToolbar(
                    initialUrl = currentUrl,
                    onUrlChange = { url ->
                        currentUrl = url
                    },
                    onNavigate = { engine.loadUrl(currentUrl) },
                    onBack = { engine.goBack() },
                    onForward = { engine.goForward() }
                )
            }

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
                        // TODO
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
