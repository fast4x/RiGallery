package it.fast4x.rigallery.core.util.ext

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.ColorSpace
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.ImageBitmap
import androidx.palette.graphics.Palette
import com.github.panpf.sketch.BitmapImage
import com.github.panpf.sketch.SingletonSketch
import com.github.panpf.sketch.decode.BitmapColorSpace
import com.github.panpf.sketch.request.ImageRequest


suspend fun dominantColorInImage(context: Context, uri: String): Int {

    val bitmap = getBitmap(context, uri)

    if (bitmap == null) return 0

    val dominantColor = Palette
        .from(bitmap)
        .maximumColorCount(8)
        .generate()
        .getDominantColor(0)
        //.getVibrantColor(0)

    val hsv = FloatArray(3)
    Color.colorToHSV(dominantColor, hsv)
//    println("dominantColorInImage Base color: ${hsv[0]}")
    val hsl = Color.HSVToColor(floatArrayOf(hsv[0], .7f, .7f))

    return if (hsv[0].toInt() == 0) 0 else hsl


}

suspend fun getDominantColor(context: Context, uri: String): Int {
    val bitmap = getBitmap(context, uri)

    if (bitmap == null) {
        return Color.TRANSPARENT
    }
    val width = bitmap.getWidth()
    val height = bitmap.getHeight()
    val size = width * height
    val pixels: IntArray? = IntArray(size)
    //Bitmap bitmap2 = bitmap.copy(Bitmap.Config.ARGB_4444, false);
    bitmap.getPixels(pixels!!, 0, width, 0, 0, width, height)
    var color: Int
    var r = 0
    var g = 0
    var b = 0
    var a: Int
    var count = 0
    for (i in pixels.indices) {
        color = pixels[i]
        a = Color.alpha(color)
        if (a > 0) {
            r += Color.red(color)
            g += Color.green(color)
            b += Color.blue(color)
            count++
        }
    }
    r /= count
    g /= count
    b /= count
    r = (r shl 16) and 0x00FF0000
    g = (g shl 8) and 0x0000FF00
    b = b and 0x000000FF
    color = -0x1000000 or r or g or b
    return color
}

suspend fun getBitmap(context: Context, uri: String): Bitmap? {
    val sketch = SingletonSketch.get(context)
    val request = ImageRequest(context, uri.toString()) {
        crossfade()
        resizeOnDraw()
        colorSpace(BitmapColorSpace(ColorSpace.Named.SRGB))
    }
    val result = sketch.execute(request)
    val bitmap = (result.image as? BitmapImage)?.bitmap

    return bitmap
}

