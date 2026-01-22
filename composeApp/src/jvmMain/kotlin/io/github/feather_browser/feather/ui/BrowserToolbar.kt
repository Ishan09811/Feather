
package io.github.feather_browser.feather.ui

import java.awt.BorderLayout
import java.awt.Color
import java.awt.Cursor
import java.awt.Dimension
import java.awt.Font
import java.awt.KeyboardFocusManager
import java.awt.Point
import java.awt.event.MouseEvent
import java.awt.event.MouseAdapter
import java.awt.event.MouseMotionAdapter
import javax.swing.JFrame
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.JSeparator
import javax.swing.JTextField
import javax.swing.event.DocumentListener
import javax.swing.border.EmptyBorder
import javax.swing.event.DocumentEvent

class BrowserToolbar(
    initialUrl: String,
    onUrlChange: (String) -> Unit,
    onNavigate: () -> Unit,
    frame: JFrame
) : JPanel(BorderLayout()) {

    private var dragStartWindow: Point? = null
    private var dragStartScreen: Point? = null
    lateinit var textField: JTextField

    init {
        preferredSize = Dimension(0, 72)
        background = Color(0xFA, 0xFA, 0xFA)

        textField = JTextField(initialUrl).apply {
            font = Font("Segoe UI", Font.PLAIN, 14)
            border = EmptyBorder(10, 18, 10, 18)
            isOpaque = false
            background = Color(0, 0, 0, 0)
        }

        textField.addActionListener {
            onNavigate()
        }

        textField.document.addDocumentListener(object : DocumentListener {
            override fun insertUpdate(e: DocumentEvent?) = onUrlChange(textField.text)
            override fun removeUpdate(e: DocumentEvent?) = onUrlChange(textField.text)
            override fun changedUpdate(e: DocumentEvent?) = onUrlChange(textField.text)
        })

        val searchIcon = JLabel(drawableResourceToIcon("drawable/ic_search.svg"))

        val closeIcon = JLabel(drawableResourceToIcon("drawable/ic_close.svg")).apply {
            cursor = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)
            addMouseListener(object : MouseAdapter() {
                override fun mouseClicked(e: MouseEvent) {
                    textField.text = ""
                }
            })
        }

        val fieldPanel = RoundedPanel(40).apply {
            background = Color(0xF1, 0xF3, 0xF4)
            border = EmptyBorder(6, 12, 6, 12)
            preferredSize = Dimension(0, 44)
            add(searchIcon, BorderLayout.WEST)
            add(textField, BorderLayout.CENTER)
            add(closeIcon, BorderLayout.EAST)
        }

        val centerWrapper = JPanel(BorderLayout()).apply {
            isOpaque = false
            border = EmptyBorder(10, 12, 10, 12)
            add(fieldPanel, BorderLayout.CENTER)
        }

        add(centerWrapper, BorderLayout.CENTER)
        add(JSeparator(), BorderLayout.SOUTH)

        textField.addMouseListener(object : MouseAdapter() {
            override fun mousePressed(e: MouseEvent) {
                KeyboardFocusManager.getCurrentKeyboardFocusManager().clearGlobalFocusOwner()
                textField.requestFocusInWindow()
            }
        })

        addMouseListener(object : MouseAdapter() {
            override fun mousePressed(e: MouseEvent) {
                dragStartWindow = frame.location
                dragStartScreen = e.locationOnScreen
            }
        })

        addMouseMotionListener(object : MouseMotionAdapter() {
            override fun mouseDragged(e: MouseEvent) {
                val win = dragStartWindow ?: return
                val start = dragStartScreen ?: return

                val dx = e.xOnScreen - start.x
                val dy = e.yOnScreen - start.y

                frame.setLocation(win.x + dx, win.y + dy)
            }
        })
    }
}
