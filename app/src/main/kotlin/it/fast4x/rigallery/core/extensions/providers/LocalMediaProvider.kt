package it.fast4x.rigallery.core.extensions.providers

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.net.wifi.WifiManager
import android.util.Base64
import android.util.Log
import com.github.panpf.sketch.BitmapImage
import com.github.panpf.sketch.SingletonSketch
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.request.ImageResult
import kotlinx.coroutines.runBlocking
import java.io.BufferedReader
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStreamReader
import java.net.InetAddress
import java.net.ServerSocket
import java.net.Socket
import java.nio.ByteBuffer
import java.nio.ByteOrder


class LocalMediaProvider(private val activity: Activity) {
    fun stopMediaProvider() {
        isWebServerSunning = false
        try {
            if (httpServerSocket != null) {
                httpServerSocket!!.close()
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    fun startMediaProvider() {
        isWebServerSunning = true
        val webServerThread = Thread(Runnable {
            var socket: Socket
            var httpResponseThread: HttpResponseThread?
            try {

                httpServerSocket = ServerSocket(0)
                //println("LocalMediaProvider WebServerThread started listening ip ${httpServerSocket!!.localSocketAddress} on port ${httpServerSocket!!.localPort}")
                ipAddress = findIPAddress(activity)
                port = httpServerSocket!!.localPort.toString()
                println("LocalMediaProvider WebServerThread started listening ip $ipAddress on port ${httpServerSocket!!.localPort}")
                while (isWebServerSunning) {
                    socket = httpServerSocket!!.accept()
                    httpResponseThread = HttpResponseThread(socket)
                    httpResponseThread.start()
                }
            } catch (e: Exception) {
                println("LocalMediaProvider Exception ${e.message}")
                e.printStackTrace()
            }
        })
        webServerThread.start()
    }

    private inner class HttpResponseThread(var clientSocket: Socket) : Thread() {
        override fun run() {
            try {
                BufferedReader(InputStreamReader(clientSocket.getInputStream())).use { bufferedReader ->
                    clientSocket.getOutputStream().use { outputStream ->
                        val input = bufferedReader.readLine()
                        println("LocalMediaProvider HttpResponseThread received input $input")

                        if (input != null && !input.isEmpty() && input.contains("/") && input.contains(
                                " "
                            )
                        ) {
                            println("LocalMediaProvider HttpResponseThread received input process $input")
                            if (input.contains(imageDelimiter)) {
                                val imageId: String =
                                    input.substring(input.indexOf("/") + 1, input.lastIndexOf(" "))
                                        .trim { it <= ' ' }.split(
                                            imageDelimiter.toRegex()
                                        ).dropLastWhile { it.isEmpty() }.toTypedArray()[1]

                                if (imageId.isNotEmpty()) {
                                    println("LocalMediaProvider HttpResponseThread received input imageId $imageId")
//                                    val bitmap = BitmapFactory.decodeResource(
//                                        activity.resources,
//                                        imageId.toInt()
//                                    )

                                    val sketch = SingletonSketch.get(activity)
                                    val request = ImageRequest(activity,
                                       // "content://media/external/images/media/1000011309"
                                        imageId
                                    )
                                    var result: ImageResult? = null
                                    var bitmap: Bitmap? = null
                                    var bitmapBytes = ByteArrayOutputStream()
                                    runBlocking {
                                       result = sketch.execute(request)
                                        result.let {
                                            println("LocalMediaProvider HttpResponseThread received input imageId result $it")
                                            bitmap = (it.image as? BitmapImage)?.bitmap
                                            bitmap?.compress(Bitmap.CompressFormat.PNG, 100, bitmapBytes)
                                        }
                                        //println("LocalMediaProvider HttpResponseThread received input imageId result $result")
                                    }


                                    //val bitmap = (result?.image as? BitmapImage)?.bitmap

                                    //val imgFile = File(imageId)
                                    //if(imgFile.exists()){
                                    if (bitmap != null) {
                                        println("LocalMediaProvider HttpResponseThread received input imageId exists")
                                        //var bitmap = BitmapFactory.decodeFile(imgFile.absolutePath)



//                                            try {
//                                                bitmap.compress(Bitmap.CompressFormat.PNG, 100, bitmapBytes)
//                                            } catch (e: Exception) {
//                                                println("LocalMediaProvider HttpResponseThread received input bitmap ERROR $e")
//                                            }
//                                        val imgTag =
//                                            "<img src=\"data:image/png;base64,$bitmapBytes\" />"
                                        val imgTag = "<img src=\"data:image/png;base64,${
                                            Base64.encodeToString(
                                                bitmapBytes.toByteArray(),
                                                Base64.DEFAULT
                                            )
                                        }\" />"
                                        val body = """
        <!DOCTYPE html><html><head><title>Exemple</title></head><body><p>Server exemple.</p>$imgTag</body></html>
    """.trimIndent()
                                        outputStream.write("HTTP/1.1 200 OK\r\n".toByteArray())
                                        outputStream.write("Server: Apache/0.8.4\r\n".toByteArray())
                                        //outputStream.write(("Content-Length: " + bitmapBytes.toByteArray().size + "\r\n").toByteArray())
                                        //outputStream.write(("Content-Type: image/png").toByteArray())
                                        outputStream.write("\r\n".toByteArray())
                                        outputStream.write(bitmapBytes.toByteArray())
                                        //outputStream.write(body.toByteArray())



//                                        outputStream.write("<img src=\"data:image/png,base64:${
//                                            android.util.Base64.encodeToString(
//                                                bitmapBytes.toByteArray(),
//                                                android.util.Base64.DEFAULT
//                                            )
//                                        } />".toByteArray())
                                    }

//                                    val bitmap = try {
//                                        BitmapFactory.decodeFile(imageId)
//                                    } catch (e: Exception) {
//                                        println("LocalMediaProvider HttpResponseThread received input bitmap ERROR $e")
//                                        null
//                                    }
//
//
//                                    if (bitmap != null) {
//                                        println("LocalMediaProvider HttpResponseThread received input bitmap NOT NULL")
//                                        val bitmapBytes = ByteArrayOutputStream()
//                                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, bitmapBytes)
//
//                                        outputStream.write("HTTP/1.0 200 OK\r\n".toByteArray())
//                                        outputStream.write("Server: Apache/0.8.4\r\n".toByteArray())
//                                        outputStream.write(("Content-Length: " + bitmapBytes.toByteArray().size + "\r\n").toByteArray())
//                                        outputStream.write("\r\n".toByteArray())
//                                        outputStream.write(bitmapBytes.toByteArray())
//                                    }
                                }
                            }
                            if (input.contains(videoDelimiter)) {
                                val videoId: String =
                                    input.substring(input.indexOf("/") + 1, input.lastIndexOf(" "))
                                        .trim { it <= ' ' }.split(
                                            videoDelimiter.toRegex()
                                        ).dropLastWhile { it.isEmpty() }.toTypedArray()[1]

                                if (videoId.isNotEmpty()) {
                                    println("LocalMediaProvider HttpResponseThread received input videoId $videoId")
                                    outputStream.write("HTTP/1.0 200 OK\r\n".toByteArray())
                                    outputStream.write("Server: Apache/0.8.4\r\n".toByteArray())
                                    outputStream.write("\r\n".toByteArray())
                                }
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun ipAddress(): String? = ipAddress
    fun isWebServerSunning(): Boolean = isWebServerSunning
    fun baseUrl(): String? = "http://$ipAddress:$port"

    private fun findIPAddress(context: Context): String? {
        val wifiManager =
            context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        try {
            return if (wifiManager.connectionInfo != null) {
                val wifiInfo = wifiManager.connectionInfo
                InetAddress.getByAddress(
                    ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN)
                        .putInt(wifiInfo.ipAddress)
                        .array()
                ).hostAddress
            } else
                null
        } catch (e: Exception) {
            Log.e(LocalMediaProvider::class.java.simpleName, "Error finding IpAddress: ${e.message}", e)
        }
        return null
    }

    companion object {
        private var httpServerSocket: ServerSocket? = null
        private var isWebServerSunning = false
        private var ipAddress: String? = null
        private var port: String? = null
        const val imageDelimiter: String = "pic="
        const val videoDelimiter: String = "vid-"
    }
}
