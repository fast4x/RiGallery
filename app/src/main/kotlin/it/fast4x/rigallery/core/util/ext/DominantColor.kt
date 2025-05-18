package it.fast4x.rigallery.core.util.ext

import android.content.Context
import android.graphics.Color
import android.graphics.ColorSpace
import androidx.core.graphics.toColor
import androidx.core.graphics.toColorLong
import androidx.palette.graphics.Palette
import com.github.panpf.sketch.BitmapImage
import com.github.panpf.sketch.SingletonSketch
import com.github.panpf.sketch.decode.BitmapColorSpace
import com.github.panpf.sketch.request.ImageRequest

suspend fun dominantColorInImage(context: Context, uri: String): Int {
    val sketch = SingletonSketch.get(context)
    val request = ImageRequest(context, uri.toString()) {
        crossfade()
        resizeOnDraw()
        colorSpace(BitmapColorSpace(ColorSpace.Named.SRGB))
    }
    val result = sketch.execute(request)
    val bitmap = (result.image as? BitmapImage)?.bitmap

    if (bitmap == null) return 0

    val dominantColor = Palette
        .from(bitmap)
        .maximumColorCount(8)
        .generate()
        .getDominantColor(0)

    val hsv = FloatArray(3)
    Color.colorToHSV(dominantColor, hsv)
    val hsl = Color.HSVToColor(floatArrayOf(hsv[0], 1f, 1f))

    return hsl

}