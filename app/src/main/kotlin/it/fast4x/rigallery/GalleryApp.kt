/*
 * SPDX-FileCopyrightText: 2023 IacobIacob01
 * SPDX-License-Identifier: Apache-2.0
 */

package it.fast4x.rigallery

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import it.fast4x.rigallery.core.decoder.supportHeifDecoder
import it.fast4x.rigallery.core.decoder.supportJxlDecoder
import it.fast4x.rigallery.core.decoder.supportVaultDecoder
import com.github.panpf.sketch.PlatformContext
import com.github.panpf.sketch.SingletonSketch
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.cache.DiskCache
import com.github.panpf.sketch.decode.supportAnimatedHeif
import com.github.panpf.sketch.decode.supportAnimatedWebp
import com.github.panpf.sketch.decode.supportSvg
import com.github.panpf.sketch.decode.supportVideoFrame
import com.github.panpf.sketch.request.supportPauseLoadWhenScrolling
import com.github.panpf.sketch.request.supportSaveCellularTraffic
import com.github.panpf.sketch.util.appCacheDirectory
import dagger.hilt.android.HiltAndroidApp
import it.fast4x.rigallery.core.util.isAtLeastAndroid11
import okio.FileSystem
import javax.inject.Inject

@HiltAndroidApp
class GalleryApp : Application(), SingletonSketch.Factory, Configuration.Provider {

    override fun createSketch(context: PlatformContext): Sketch = Sketch.Builder(this).apply {
        components {
            supportSaveCellularTraffic()
            supportPauseLoadWhenScrolling()
            supportSvg()
            supportVideoFrame()
            supportAnimatedWebp()
            if (isAtLeastAndroid11)
                supportAnimatedHeif()
            supportHeifDecoder()
            supportJxlDecoder()
            supportVaultDecoder()
        }
        val diskCache = DiskCache.Builder(context, FileSystem.SYSTEM)
            .directory(context.appCacheDirectory())
            .maxSize(150 * 1024 * 1024).build()

        resultCache(diskCache)
        downloadCache(diskCache)
    }.build()

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()

}