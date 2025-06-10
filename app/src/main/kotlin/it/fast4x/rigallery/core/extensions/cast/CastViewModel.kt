package it.fast4x.rigallery.core.extensions.cast

import android.graphics.drawable.VectorDrawable
import android.net.Uri
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material.icons.outlined.LocalMovies
import androidx.compose.material.icons.outlined.Videocam
import androidx.compose.material.icons.outlined.Web
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.ViewModel
import androidx.mediarouter.media.MediaControlIntent
import androidx.mediarouter.media.MediaRouteSelector
import androidx.mediarouter.media.MediaRouter
import com.google.android.gms.cast.MediaInfo
import com.google.android.gms.cast.MediaLoadRequestData
import com.google.android.gms.cast.MediaMetadata
import com.google.android.gms.cast.MediaSeekOptions
import com.google.android.gms.cast.framework.CastContext
import com.google.android.gms.cast.framework.CastState
import com.google.android.gms.cast.framework.CastStateListener
import com.google.android.gms.cast.framework.media.RemoteMediaClient
import com.samsung.multiscreen.Error
import com.samsung.multiscreen.Player
import com.samsung.multiscreen.Result
import com.samsung.multiscreen.Search
import com.samsung.multiscreen.Service
import com.samsung.multiscreen.VideoPlayer
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

enum class CastDeviceState {
    SEARCHING,
    CONNECTING,
    CONNECTED

}

enum class PlayerState {
    IDLE,
    READY,
    BUFFERING
}

enum class CastMediaType {
    PHOTO,
    VIDEO;

    val icon: ImageVector
        get () = when(this) {
            PHOTO -> Icons.Outlined.Image
            VIDEO -> Icons.Outlined.Videocam
        }
}

enum class ServerType {
    WEB,
    LOCAL;

    val icon: ImageVector
        get () = when(this) {
            WEB -> Icons.Outlined.Web
            LOCAL -> Icons.Outlined.LocalMovies
        }
}

open class Device(val id: String, val name: String?, val description: String?, val enable: Boolean)
data class SamsungDevice(val service: Service) :
    Device(
        service.id,
        service.name,
        service.type+ " - " + service.version,
        !service.isStandbyService
    )
data class ChromeCastDevice(val route: MediaRouter.RouteInfo) :
    Device(
        route.id,
        route.name,
        route.description,
        route.isEnabled
    )

class CastViewModel(private val search: Search, private val castContext: CastContext, private var mediaRouter: MediaRouter) : ViewModel() {

    private var urlToPlay by mutableStateOf("")
    private var mediaType: CastMediaType? = CastMediaType.PHOTO

    var deviceState by mutableStateOf(CastDeviceState.SEARCHING)
    val deviceList = mutableStateListOf<Device>()
    var currentDevice by mutableStateOf<Device?>(null)

    var playerState by mutableStateOf(PlayerState.IDLE)

    var isPlaying by mutableStateOf(true)

    val duration = mutableIntStateOf(0)
    val currentTime = mutableIntStateOf(0)
    val progress = mutableFloatStateOf(0f)

    private var videoPlayer by mutableStateOf<VideoPlayer?>(null)
    private var remoteMediaClient by mutableStateOf<RemoteMediaClient?>(null)

    init {

        search.setOnServiceFoundListener(serviceFound())
        search.setOnServiceLostListener(serviceLost())

        castContext.addCastStateListener(castStateListener())

        //search.start()

        startSearch()
    }

    private fun serviceFound() = Search.OnServiceFoundListener { service ->
        println(service.name)

        if (!deviceList.any { it.id == service.id } && !service.isStandbyService)
            deviceList.add(SamsungDevice(service = service))
    }

    private fun serviceLost() = Search.OnServiceLostListener { service ->
        println(service.name)

        val deviceById = deviceList.find { it.id == service.id }
        deviceList.remove(deviceById)
    }

    fun startSearch(){
        search.start()

        val selector = MediaRouteSelector.Builder()
            // These are the framework-supported intents
            //.addControlCategory(MediaControlIntent.CATEGORY_LIVE_AUDIO)
            //.addControlCategory(MediaControlIntent.CATEGORY_LIVE_VIDEO)
            .addControlCategory(MediaControlIntent.CATEGORY_REMOTE_PLAYBACK)
            .build()

        mediaRouter.addCallback(selector, mediaRouterCallBack(), MediaRouter.CALLBACK_FLAG_PERFORM_ACTIVE_SCAN)


        deviceState = CastDeviceState.SEARCHING
    }

    fun stopSearch(){
        search.stop()

        mediaRouter.removeCallback(mediaRouterCallBack())
    }

    fun connect(device: Device) {
        currentDevice = device

        deviceState = CastDeviceState.CONNECTING
        stopSearch()

        when(device) {
            is SamsungDevice -> {
                videoPlayer = device.service.createVideoPlayer("RiCast")
                videoPlayer?.addOnMessageListener(videoPlayerListener())
//                videoPlayer?.playContent(Uri.parse(urlToPlay), "AndroidCast", Uri.parse(""), object : com.samsung.multiscreen.Result<Boolean> {
//                    override fun onSuccess(p0: Boolean?) {
//                        //result(true, null)
//                        deviceState = CastDeviceState.CONNECTED
//                    }
//
//                    override fun onError(p0: Error?) {
//                        startSearch()
//                    }
//
//                })
            }
            is ChromeCastDevice -> {
                mediaRouter.selectRoute(device.route)
            }
        }
    }


    fun prepareCastPLayer(url: String, type: CastMediaType) {
        urlToPlay = url
        mediaType = type
        //currentDevice = device

        //deviceState = CastDeviceState.CONNECTING
        //stopSearch()

        when(currentDevice) {
            is SamsungDevice -> {
                videoPlayer = (currentDevice as SamsungDevice).service.createVideoPlayer("RiCast")
                videoPlayer?.addOnMessageListener(videoPlayerListener())
                videoPlayer?.playContent(Uri.parse(urlToPlay), "AndroidCast", Uri.parse(""), object : Result<Boolean> {
                    override fun onSuccess(p0: Boolean?) {
                        //result(true, null)
                        deviceState = CastDeviceState.CONNECTED
                    }

                    override fun onError(p0: Error?) {
                        startSearch()
                    }

                })
            }
            is ChromeCastDevice -> {
                mediaRouter.selectRoute((currentDevice as ChromeCastDevice).route)
            }
        }
    }

    fun play() {
        videoPlayer?.play()
        remoteMediaClient?.play()
    }

    fun pause(){
        videoPlayer?.pause()
        remoteMediaClient?.pause()
    }

    fun rewind(){
        videoPlayer?.rewind()

        remoteMediaClient?.let {
            val rewindTime = it.approximateStreamPosition - 10000
            val mso = MediaSeekOptions.Builder().setPosition(rewindTime).build()
            it.seek(mso)
        }
    }

    fun forward(){
        videoPlayer?.forward()

        remoteMediaClient?.let {
            val forwardTime = it.approximateStreamPosition + 10000
            val mso = MediaSeekOptions.Builder().setPosition(forwardTime).build()
            it.seek(mso)
        }
    }

    fun seek(time: Int){
        videoPlayer?.seekTo(time, TimeUnit.MILLISECONDS)

        remoteMediaClient?.let {
            val mso = MediaSeekOptions.Builder().setPosition(time.toLong()).build()
            it.seek(mso)
        }
    }

    fun stop(){
        videoPlayer?.stop()
        videoPlayer?.disconnect()

        remoteMediaClient?.stop()
        mediaRouter.unselect(MediaRouter.UNSELECT_REASON_STOPPED)

        startSearch()

        resetAll()
    }

    private fun resetAll() {
        videoPlayer = null
        remoteMediaClient= null

        playerState = PlayerState.IDLE
        isPlaying = false

        duration.intValue = 0
        currentTime.intValue = 0
        progress.floatValue = 0f
    }

    fun disconnect(){
        videoPlayer?.disconnect()
    }

    private fun videoPlayerListener() = object : VideoPlayer.OnVideoPlayerListener {
        override fun onBufferingStart() {
            println("onBufferingStart")
            //onBuffering(true)
            playerState = PlayerState.BUFFERING

        }

        override fun onBufferingComplete() {
            println("onBufferingComplete")
            playerState = PlayerState.READY
            /*if (playWhenReady.value) {
                play()
            } else {
                pause()
            }*/
        }

        override fun onBufferingProgress(progress: Int) {
        }

        override fun onCurrentPlayTime(currentProgress: Int) {
            println("onCurrentPlayTime")
            if (currentTime.intValue != currentProgress) {
                currentTime.intValue = currentProgress

                if (currentProgress > 0) {

                    val current = currentProgress.toFloat()
                    val dur = duration.intValue.toFloat()

                    println((current / dur) * 100)

                    progress.floatValue = (current / dur)
                }
            }
        }

        override fun onStreamingStarted(dur: Int) {
            println("onStreamingStarted $dur")
            duration.intValue = dur
            playerState = PlayerState.READY
        }

        override fun onStreamCompleted() {
            println("onStreamingStarted")
            videoPlayer?.stop()
        }

        override fun onPlayerInitialized() {
            println("onPlayerInitialized")
        }

        override fun onPlayerChange(p0: String?) {
            println("onPlayerChange")
        }

        override fun onPlay() {
            println("onPlay")
            isPlaying = true
        }

        override fun onPause() {
            println("onPause")
            isPlaying = false
        }

        override fun onStop() {
            println("onStop")
            isPlaying = false
        }

        override fun onForward() {
        }

        override fun onRewind() {
        }

        override fun onMute() {
        }

        override fun onUnMute() {
        }

        override fun onNext() {
        }

        override fun onPrevious() {
        }

        override fun onControlStatus(p0: Int, p1: Boolean?, p2: Player.RepeatMode?) {
        }

        override fun onVolumeChange(p0: Int) {
        }

        override fun onAddToList(p0: JSONObject?) {
        }

        override fun onRemoveFromList(p0: JSONObject?) {
        }

        override fun onClearList() {
        }

        override fun onGetList(p0: JSONArray?) {
        }

        override fun onRepeat(p0: Player.RepeatMode?) {
        }

        override fun onCurrentPlaying(p0: JSONObject?, p1: String?) {
        }

        override fun onApplicationResume() {
        }

        override fun onApplicationSuspend() {
        }

        override fun onError(p0: Error?) {
        }

    }

    /**
     * Chromecast
     */

    private fun castStateListener() = CastStateListener {
        when(it) {
            CastState.CONNECTED -> {
                deviceState = CastDeviceState.CONNECTED

                val castSession = castContext.sessionManager.currentCastSession

                remoteMediaClient = castSession?.remoteMediaClient
                remoteMediaClient?.stop()

                remoteMediaClient?.registerCallback(mediaClientCallback())

                val metadata = MediaMetadata(
                    when(mediaType){
                        CastMediaType.PHOTO -> MediaMetadata.MEDIA_TYPE_PHOTO
                        CastMediaType.VIDEO -> MediaMetadata.MEDIA_TYPE_MOVIE
                        else -> MediaMetadata.MEDIA_TYPE_PHOTO
                    }
                ).apply {
                    putString(MediaMetadata.KEY_TITLE, urlToPlay)
                }


                val mediaInfo = MediaInfo.Builder(urlToPlay)
                    .setStreamType(
                        when(mediaType){
                            CastMediaType.PHOTO -> MediaInfo.STREAM_TYPE_NONE
                            CastMediaType.VIDEO -> MediaInfo.STREAM_TYPE_BUFFERED
                            else -> MediaInfo.STREAM_TYPE_NONE
                        }
                    )
                    .setMetadata(metadata)


                val mediaLoadRequestData = MediaLoadRequestData.Builder()
                    .setMediaInfo(mediaInfo.build())
                    .setAutoplay(true)

                remoteMediaClient?.load(mediaLoadRequestData.build())

                stopSearch()
            }
            CastState.NOT_CONNECTED -> {
                remoteMediaClient?.unregisterCallback(mediaClientCallback())
                remoteMediaClient = null
                startSearch()
            }
        }
    }
    private fun mediaRouterCallBack() =  object: MediaRouter.Callback() {
        override fun onRouteAdded(router: MediaRouter, route: MediaRouter.RouteInfo) {
            if (!deviceList.any { it.id == route.id } && route.isEnabled)
                deviceList.add(ChromeCastDevice(route = route))
        }

        override fun onRouteRemoved(router: MediaRouter, route: MediaRouter.RouteInfo) {
            val deviceById = deviceList.find { it.id == route.id }
            deviceList.remove(deviceById)
        }

        /*override fun onRouteChanged(router: MediaRouter, route: MediaRouter.RouteInfo) {
            val old = deviceList.find { it.id == route.id }
            deviceList[deviceList.indexOf(old)] = ChromeCastDevice(route = route)
        }*/

        /*override fun onRouteSelected(
            router: MediaRouter,
            selectedRoute: MediaRouter.RouteInfo,
            reason: Int,
            requestedRoute: MediaRouter.RouteInfo
        ) {
            mediaRouter = router

        }

        override fun onRouteUnselected(
            router: MediaRouter,
            route: MediaRouter.RouteInfo,
            reason: Int
        ) {
            super.onRouteUnselected(router, route, reason)
        }*/
    }

    private fun mediaClientCallback() = object: RemoteMediaClient.Callback() {
        override fun onStatusUpdated() {
            if (remoteMediaClient == null) return

            remoteMediaClient?.let {
                isPlaying = it.isPlaying

                playerState = if (it.isBuffering) PlayerState.BUFFERING else PlayerState.READY

                it.streamDuration
                it.addProgressListener({ position, duration ->

                    if (duration <= 0) return@addProgressListener

                    this@CastViewModel.duration.intValue = duration.toInt()
                    this@CastViewModel.currentTime.intValue = position.toInt()

                    if (position > 0) {
                        val current = position.toFloat()
                        val dur = duration.toFloat()
                        progress.floatValue = (current / dur)
                    }
                }, 500)

                Unit
            }
        }
    }
}