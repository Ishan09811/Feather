
package io.github.feather_browser.feather.ui

import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.toAwtImage
import feather.composeapp.generated.resources.Res
import kotlinx.coroutines.runBlocking
import org.jetbrains.skia.Data
import org.jetbrains.skia.svg.SVGDOM
import javax.swing.ImageIcon

fun drawableResourceToIcon(
    path: String,
    size: Int = 24
): ImageIcon {

    val svgBytes: ByteArray = runBlocking {  Res.readBytes(path) }

    val svgDom = SVGDOM(Data.makeFromBytes(svgBytes))

    val bitmap = org.jetbrains.skia.Bitmap().apply {
        allocN32Pixels(size, size)
    }

    val canvas = org.jetbrains.skia.Canvas(bitmap)
    canvas.clear(org.jetbrains.skia.Color.TRANSPARENT)

    svgDom.setContainerSize(size.toFloat(), size.toFloat())
    svgDom.render(canvas)

    val image = bitmap.asImageBitmap().toAwtImage()
    return ImageIcon(image)
}
