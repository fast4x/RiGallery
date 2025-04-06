package it.fast4x.rigallery.core.presentation.components.util

import android.content.Context
import android.content.pm.PackageManager

fun Context.permissionGranted(list: List<String>): Boolean {
    var granted = true
    list.forEach {
        if (checkSelfPermission(it) != PackageManager.PERMISSION_GRANTED) granted = false
    }
    return granted
}