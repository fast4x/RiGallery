/*
 * SPDX-FileCopyrightText: 2023 IacobIacob01
 * SPDX-License-Identifier: Apache-2.0
 */

package it.fast4x.rigallery.feature_node.domain.util

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Serializable
@Parcelize
sealed class OrderType : Parcelable {
    @Serializable
    @Parcelize
    data object Ascending : OrderType()

    @Serializable
    @Parcelize
    data object Descending : OrderType()
}
