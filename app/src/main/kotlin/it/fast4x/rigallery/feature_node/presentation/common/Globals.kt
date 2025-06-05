package it.fast4x.rigallery.feature_node.presentation.common

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

open class Globals @Inject constructor(
    @ApplicationContext val context: Context
) {
    val appPackageName = context.packageName
    val appVersionName = context.packageManager.getPackageInfo(appPackageName, 0).versionName
    //val appVersionCode = context.packageManager.getPackageInfo(appPackageName, 0).versionCode
    val appContext = context
}