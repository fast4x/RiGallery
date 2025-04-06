/*
 * SPDX-FileCopyrightText: 2023 IacobIacob01
 * SPDX-License-Identifier: Apache-2.0
 */

package it.fast4x.rigallery.feature_node.presentation.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import it.fast4x.rigallery.R

fun Context.launchMap(lat: Double, lang: Double) {
    startActivity(
        Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse("geo:0,0?q=$lat,$lang(${getString(R.string.media_location)})")
        }
    )
}