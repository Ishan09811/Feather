
package io.github.feather_browser.feather.ui

import java.awt.BorderLayout
import java.awt.Graphics
import java.awt.Graphics2D
import java.awt.RenderingHints
import java.awt.geom.RoundRectangle2D
import javax.swing.JFrame
import javax.swing.JPanel

class RoundedPanel(private val radius: Int) : JPanel(BorderLayout()) {

    init {
        isOpaque = false
    }

    override fun paintComponent(g: Graphics) {
        val g2 = g.create() as Graphics2D
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)

        g2.color = background
        g2.fillRoundRect(0, 0, width, height, radius, radius)

        g2.dispose()
    }
}

fun applyRoundedCorners(frame: JFrame, radius: Int) {
    frame.shape = RoundRectangle2D.Double(
        0.0,
        0.0,
        frame.width.toDouble(),
        frame.height.toDouble(),
        radius.toDouble(),
        radius.toDouble()
    )
}

