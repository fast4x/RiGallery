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
import com.github.panpf.sketch.cache.MemoryCache
import com.github.panpf.sketch.decode.supportAnimatedHeif
import com.github.panpf.sketch.decode.supportAnimatedWebp
import com.github.panpf.sketch.decode.supportSvg
import com.github.panpf.sketch.decode.supportVideoFrame
import com.github.panpf.sketch.request.supportPauseLoadWhenScrolling
import com.github.panpf.sketch.request.supportSaveCellularTraffic
import com.github.panpf.sketch.util.appCacheDirectory
import dagger.hilt.android.HiltAndroidApp
import it.fast4x.rigallery.core.util.CaptureCrash
import okio.FileSystem
import javax.inject.Inject

@HiltAndroidApp
class GalleryApp : Application(), SingletonSketch.Factory, Configuration.Provider {

    override fun onCreate() {
        super.onCreate()

        /***** CRASH LOG ALWAYS ENABLED *****/
        val dir = filesDir.resolve("logs").also {
            if (it.exists()) return@also
            it.mkdir()
        }

        Thread.setDefaultUncaughtExceptionHandler(CaptureCrash(dir.absolutePath, "${BuildConfig.APPLICATION_NAME}_crash_log.txt"))
        /***** CRASH LOG ALWAYS ENABLED *****/
    }

    override fun createSketch(context: PlatformContext): Sketch = Sketch.Builder(this).apply {
        components {
            supportSaveCellularTraffic()
            supportPauseLoadWhenScrolling()
            supportSvg()
            supportVideoFrame()
            supportAnimatedWebp()
            supportAnimatedHeif()
            supportHeifDecoder()
            supportJxlDecoder()
            supportVaultDecoder()
        }
        val diskCache = DiskCache.Builder(context, FileSystem.SYSTEM)
            .directory(context.appCacheDirectory())
            .maxSize(1000 * 1024 * 1024)
            .build()

        val memoryCache = MemoryCache.Builder(context)
            .maxSizePercent(0.5)
            .build()

        memoryCache(memoryCache)

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