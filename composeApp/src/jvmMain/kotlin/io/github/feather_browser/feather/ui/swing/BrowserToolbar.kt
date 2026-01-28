package io.github.feather_browser.feather.ui.swing

import java.awt.BorderLayout
import java.awt.Color
import java.awt.Cursor
import java.awt.Dimension
import java.awt.Font
import java.awt.KeyboardFocusManager
import java.awt.event.MouseEvent
import java.awt.event.MouseAdapter
import java.awt.event.ComponentAdapter
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.JSeparator
import javax.swing.JTextField
import javax.swing.event.DocumentListener
import javax.swing.border.EmptyBorder
import javax.swing.event.DocumentEvent
import com.formdev.flatlaf.extras.FlatSVGIcon
import feather.composeapp.generated.resources.Res
import kotlinx.coroutines.runBlocking
import java.awt.FlowLayout
import java.awt.GridBagLayout
import java.awt.event.ComponentEvent
import javax.swing.Box
import javax.swing.BoxLayout
import javax.swing.UIManager
import kotlin.math.roundToInt

class BrowserToolbar(
    initialUrl: String,
    onUrlChange: (String) -> Unit,
    onNavigate: () -> Unit,
    onBack: () -> Unit,
    onForward: () -> Unit
) : JPanel(BorderLayout()) {
    var textField: JTextField

    init {
        layout = BoxLayout(this, BoxLayout.Y_AXIS)
        maximumSize = Dimension(0, 100)

        val tabsRow = JPanel(FlowLayout(FlowLayout.LEFT, 8, 0)).apply {
            isOpaque = false
            border = EmptyBorder(6, 12, 0, 12)
            preferredSize = Dimension(0, 28)
        }

        tabsRow.add(tab("Tab 1"))
        tabsRow.add(tab("+"))

        textField = JTextField(initialUrl).apply {
            font = Font("Segoe UI", Font.PLAIN, 14)
            border = EmptyBorder(0, 6, 0, 6)
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

        val backIcon = runBlocking {
            JLabel(
                FlatSVGIcon(
                    Res.readBytes(
                        "drawable/ic_arrow_back.svg"
                    ).inputStream()
                ).derive(
                    ThemeManager.ICON_SIZE, ThemeManager.ICON_SIZE
                )
            ).apply {
                cursor = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)
                addMouseListener(object : MouseAdapter() {
                    override fun mouseClicked(e: MouseEvent) {
                        onBack()
                    }
                })
            }
        }

        val forwardIcon = runBlocking {
            JLabel(
                FlatSVGIcon(
                    Res.readBytes(
                        "drawable/ic_arrow_forward.svg"
                    ).inputStream()
                ).derive(
                    ThemeManager.ICON_SIZE, ThemeManager.ICON_SIZE
                )
            ).apply {
                cursor = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)
                addMouseListener(object : MouseAdapter() {
                    override fun mouseClicked(e: MouseEvent) {
                        onForward()
                    }
                })
            }
        }

        val navPanel = JPanel(FlowLayout(FlowLayout.LEFT, 6, 0)).apply {
            border = EmptyBorder(20, 10, 20, 10)
            isOpaque = false
            add(backIcon)
            add(forwardIcon)
        }

        val searchIcon = runBlocking {
            JLabel(
                FlatSVGIcon(
                    Res.readBytes(
                        "drawable/ic_search.svg"
                    ).inputStream()
                ).derive(
                    ThemeManager.ICON_SIZE, ThemeManager.ICON_SIZE
                )
            )
        }

        val closeIcon = runBlocking {
            JLabel(FlatSVGIcon(Res.readBytes("drawable/ic_close.svg").inputStream()).derive(ThemeManager.ICON_SIZE,ThemeManager.ICON_SIZE)).apply {
                cursor = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)
                addMouseListener(object : MouseAdapter() {
                    override fun mouseClicked(e: MouseEvent) {
                        textField.text = ""
                    }
                })
            }
        }

        val fieldPanel = RoundedPanel(80).apply {
            background = UIManager.getColor("TextField.background")
            layout = BorderLayout(4, 0)
            isOpaque = false
            border = EmptyBorder(4, 8, 4, 8)
            preferredSize = Dimension(this@BrowserToolbar.width, 42)

            add(searchIcon, BorderLayout.WEST)
            add(textField, BorderLayout.CENTER)
            add(closeIcon, BorderLayout.EAST)
        }

        addComponentListener(object : ComponentAdapter() {
            override fun componentResized(e: ComponentEvent) {
                val available = (width / 1.1).roundToInt()
                fieldPanel.preferredSize = Dimension(available, 42)
                fieldPanel.revalidate()
            }
        })

        val fieldWrapper = JPanel(GridBagLayout()).apply {
            isOpaque = false
            add(fieldPanel)
        }

        val leftGroup = JPanel().apply {
            layout = BoxLayout(this, BoxLayout.X_AXIS)
            isOpaque = false

            add(navPanel)
            add(Box.createHorizontalStrut(8))
            add(fieldWrapper)
        }

        val centerWrapper = JPanel(BorderLayout(8, 0)).apply {
            isOpaque = false
            border = EmptyBorder(2, 12, 6, 12)
            add(leftGroup, BorderLayout.WEST)
        }

        add(tabsRow)
        add(centerWrapper)
        add(JSeparator(), BorderLayout.SOUTH)

        textField.addMouseListener(object : MouseAdapter() {
            override fun mousePressed(e: MouseEvent) {
                KeyboardFocusManager.getCurrentKeyboardFocusManager().clearGlobalFocusOwner()
                textField.requestFocusInWindow()
            }
        })
    }

    fun tab(title: String) = JLabel(title).apply {
        preferredSize = Dimension(120, 20)
        font = Font("Segoe UI", Font.PLAIN, 13)
        border = EmptyBorder(4, 10, 4, 10)
        cursor = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)
    }
}