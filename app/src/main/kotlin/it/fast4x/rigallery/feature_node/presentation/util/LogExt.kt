@file:Suppress("KotlinConstantConditions")

package it.fast4x.rigallery.feature_node.presentation.util

import android.util.Log

fun printInfo(message: Any) {
    Log.i("GalleryInfo", message.toString())
}

fun printDebug(message: Any) {
    printDebug(message.toString())
}

fun printDebug(message: String) {
    if (_root_ide_package_.it.fast4x.rigallery.BuildConfig.BUILD_TYPE != "release") {
        Log.d("GalleryInfo", message)
    }
}

fun printError(message: String) {
    Log.e("GalleryInfo", message)
}

fun printWarning(message: String) {
    Log.w("GalleryInfo", message)
}