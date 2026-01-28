package io.github.feather_browser.feather.ui.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.awt.ComposePanel
import androidx.compose.ui.awt.awtEventOrNull
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import feather.composeapp.generated.resources.Res
import feather.composeapp.generated.resources.ic_arrow_back
import feather.composeapp.generated.resources.ic_arrow_forward
import feather.composeapp.generated.resources.ic_close
import feather.composeapp.generated.resources.ic_search
import org.jetbrains.compose.resources.painterResource
import java.awt.Color
import java.awt.Point
import java.awt.event.MouseEvent
import javax.swing.JFrame


@OptIn(ExperimentalComposeUiApi::class)
fun BrowserToolbar(
    onBackgroundColorChange: (Color) -> Unit,
    frame: JFrame,
    initialUrl: String,
    onUrlChange: (String) -> Unit,
    onNavigate: () -> Unit,
    onBack: () -> Unit,
    onForward: () -> Unit
): ComposePanel {
    return ComposePanel().apply {
        setContent {
            val handler = remember { DragHandler(frame) }
            FeatherTheme {
                onBackgroundColorChange(Color(MaterialTheme.colorScheme.background.toArgb()))
                Box(
                    modifier = Modifier.onPointerEvent(PointerEventType.Press) { event ->
                        val awt = event.awtEventOrNull ?: return@onPointerEvent
                        handler.start(awt)
                    }
                        .onPointerEvent(PointerEventType.Move) { event ->
                            val awt = event.awtEventOrNull ?: return@onPointerEvent
                            if (awt.id == MouseEvent.MOUSE_DRAGGED) {
                                handler.drag(awt)
                            }
                        }
                        .onPointerEvent(PointerEventType.Release) {
                            handler.end()
                        }
                ) {
                    BrowserToolbarContent(
                        initialUrl = initialUrl,
                        onUrlChange = onUrlChange,
                        onNavigate = onNavigate,
                        onBack = onBack,
                        onForward = onForward
                    )
                }
            }
        }
    }
}

@Composable
fun BrowserToolbarContent(
    modifier: Modifier = Modifier,
    initialUrl: String,
    onUrlChange: (String) -> Unit,
    onNavigate: () -> Unit,
    onBack: () -> Unit,
    onForward: () -> Unit
) {
    var url by remember { mutableStateOf(initialUrl) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        TabsRow()

        Spacer(Modifier.height(6.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            NavButtons(
                onBack = onBack,
                onForward = onForward
            )

            Spacer(Modifier.width(8.dp))

            UrlField(
                value = url,
                onValueChange = {
                    url = it
                    onUrlChange(it)
                },
                onNavigate = onNavigate,
                onClear = { url = "" }
            )
        }
    }
}

@Composable
fun UrlField(
    value: String,
    onValueChange: (String) -> Unit,
    onNavigate: () -> Unit,
    onClear: () -> Unit
) {
    Box(
        modifier = Modifier
            .height(42.dp)
            .fillMaxWidth(0.9f)
            .clip(RoundedCornerShape(20.dp))
            .background(MaterialTheme.colorScheme.surface)
            .border(
                1.dp,
                MaterialTheme.colorScheme.outlineVariant,
                RoundedCornerShape(20.dp)
            )
            .padding(horizontal = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(Res.drawable.ic_search),
                contentDescription = "Search",
                modifier = Modifier.size(18.dp),
                tint = MaterialTheme.colorScheme.onBackground
            )

            Spacer(Modifier.width(8.dp))

            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                singleLine = true,
                modifier = Modifier
                    .weight(1f)
                    .onKeyEvent {
                        if (it.key == Key.Enter) {
                            onNavigate()
                            true
                        } else false
                    },
                textStyle = TextStyle(
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
            )

            if (value.isNotEmpty()) {
                Spacer(Modifier.width(8.dp))
                Icon(
                    painterResource(Res.drawable.ic_close),
                    contentDescription = "Clear",
                    modifier = Modifier
                        .size(18.dp)
                        .clickable { onClear() },
                    tint = MaterialTheme.colorScheme.onBackground
                )
            }
        }
    }
}

@Composable
fun NavButtons(
    onBack: () -> Unit,
    onForward: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onBack) {
            Icon(
                painterResource(Res.drawable.ic_arrow_back),
                contentDescription = "Back",
                tint = MaterialTheme.colorScheme.onBackground
            )
        }

        IconButton(onClick = onForward) {
            Icon(
                painterResource(Res.drawable.ic_arrow_forward),
                contentDescription = "Forward",
                tint = MaterialTheme.colorScheme.onBackground
            )
        }
    }
}

@Composable
fun TabsRow() {
    Row(
        modifier = Modifier.height(28.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Tab("Tab 1")
        Spacer(Modifier.width(8.dp))
        Tab("+")
    }
}

@Composable
fun Tab(title: String) {
    Box(
        modifier = Modifier
            .height(24.dp)
            .width(120.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .clickable { }
            .padding(horizontal = 10.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Text(title, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

class DragHandler(private val frame: JFrame) {
    private var startMouse: Point? = null
    private var startWindow: Point? = null

    fun start(e: MouseEvent) {
        startMouse = e.locationOnScreen
        startWindow = frame.location
    }

    fun drag(e: MouseEvent) {
        val sm = startMouse ?: return
        val sw = startWindow ?: return

        val dx = e.locationOnScreen.x - sm.x
        val dy = e.locationOnScreen.y - sm.y

        frame.setLocation(sw.x + dx, sw.y + dy)
    }

    fun end() {
        startMouse = null
        startWindow = null
    }
}