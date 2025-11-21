package com.reactnativepdfkit

import android.content.res.AssetManager
import android.util.Base64
import android.view.animation.AlphaAnimation
import android.view.animation.DecelerateInterpolator
import com.facebook.react.bridge.Arguments
import com.facebook.react.bridge.ReactContext
import com.facebook.react.bridge.ReadableMap
import com.facebook.react.bridge.WritableMap
import com.facebook.react.uimanager.ThemedReactContext
import com.facebook.react.uimanager.events.RCTEventEmitter
import com.github.barteksc.pdfviewer.listener.OnErrorListener
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener
import com.github.barteksc.pdfviewer.listener.OnPageChangeListener
import com.github.barteksc.pdfviewer.listener.OnPageScrollListener
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.io.InputStream

class PDFView(private val context: ThemedReactContext) :
    com.github.barteksc.pdfviewer.PDFView(context, null),
    OnErrorListener,
    OnPageChangeListener,
    OnPageScrollListener,
    OnLoadCompleteListener {

    private var resource: String? = null
    private var downloadedFile: File? = null
    private var downloadTask: AsyncDownload? = null
    private var resourceType: String? = null
    private var configurator: Configurator? = null
    private var sourceChanged = true
    private var urlProps: ReadableMap? = null
    private var fadeInDuration = 0
    private var enableAnnotations = false
    private var lastPositionOffset = 0f

    override fun loadComplete(numberOfPages: Int) {
        val fadeIn = AlphaAnimation(0f, 1f)
        fadeIn.interpolator = DecelerateInterpolator()
        fadeIn.duration = fadeInDuration.toLong()
        alpha = 1f
        startAnimation(fadeIn)

        reactNativeMessageEvent(EVENT_ON_LOAD, null)
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        clipToOutline = true
    }

    override fun onError(t: Throwable) {
        reactNativeMessageEvent(EVENT_ON_ERROR, "error: ${t.message}")
    }

    override fun onPageChanged(page: Int, pageCount: Int) {
        val event = Arguments.createMap()
        event.putInt("page", page)
        event.putInt("pageCount", pageCount)
        reactNativeEvent(EVENT_ON_PAGE_CHANGED, event)
    }

    override fun onPageScrolled(page: Int, positionOffset: Float) {
        if (lastPositionOffset != positionOffset && (positionOffset == 0f || positionOffset == 1f)) {
            // Only 0 and 1 are currently supported
            lastPositionOffset = positionOffset
            val event = Arguments.createMap()
            event.putDouble("offset", positionOffset.toDouble())
            reactNativeEvent(EVENT_ON_SCROLLED, event)
        }
    }

    private fun reactNativeMessageEvent(eventName: String, message: String?) {
        val event = Arguments.createMap()
        event.putString("message", message)
        reactNativeEvent(eventName, event)
    }

    private fun reactNativeEvent(eventName: String, event: WritableMap) {
        val reactContext = context as ReactContext
        reactContext
            .getJSModule(RCTEventEmitter::class.java)
            .receiveEvent(id, eventName, event)
    }

    private fun setupAndLoad() {
        lastPositionOffset = 0f
        alpha = 0f
        configurator?.let {
            it.defaultPage(0)
                .swipeHorizontal(false)
                .onLoad(this)
                .onError(this)
                .onPageChange(this)
                .onPageScroll(this)
                .spacing(10)
                .enableAnnotationRendering(enableAnnotations)
                .load()
        }
        sourceChanged = false
    }

    private fun renderFromFile(filePath: String) {
        try {
            val input: InputStream = if (filePath.startsWith("/")) {
                // absolute path, using FS
                FileInputStream(File(filePath))
            } else {
                // from assets
                val assetManager: AssetManager = context.assets
                assetManager.open(filePath, AssetManager.ACCESS_BUFFER)
            }

            configurator = fromStream(input)
            setupAndLoad()
        } catch (e: IOException) {
            onError(e)
        }
    }

    private fun renderFromBase64() {
        try {
            val bytes = Base64.decode(resource, 0)
            configurator = fromBytes(bytes)
            setupAndLoad()
        } catch (e: IllegalArgumentException) {
            onError(IOException(Errors.E_INVALID_BASE64.code))
        }
    }

    private fun renderFromUrl() {
        val dir = context.cacheDir
        downloadedFile = try {
            File.createTempFile("pdfDocument", "pdf", dir)
        } catch (e: IOException) {
            onError(e)
            return
        }

        downloadTask = AsyncDownload(
            context,
            resource!!,
            downloadedFile!!,
            urlProps,
            object : AsyncDownload.TaskCompleted {
                override fun onComplete(ex: Exception?) {
                    if (ex == null) {
                        renderFromFile(downloadedFile!!.absolutePath)
                    } else {
                        cleanDownloadedFile()
                        onError(ex)
                    }
                }
            }
        )
        downloadTask?.execute()
    }

    fun render() {
        cleanup()

        if (resource == null) {
            onError(IOException(Errors.E_NO_RESOURCE.code))
            return
        }

        if (resourceType == null) {
            onError(IOException(Errors.E_NO_RESOURCE_TYPE.code))
            return
        }

        if (!sourceChanged) {
            return
        }

        when (resourceType) {
            "url" -> renderFromUrl()
            "base64" -> renderFromBase64()
            "file" -> renderFromFile(resource!!)
            else -> onError(IOException("${Errors.E_INVALID_RESOURCE_TYPE.code}$resourceType"))
        }
    }

    private fun cleanup() {
        downloadTask?.cancel(true)
        cleanDownloadedFile()
    }

    private fun cleanDownloadedFile() {
        downloadedFile?.let {
            if (!it.delete()) {
                onError(IOException(Errors.E_DELETE_FILE.code))
            }
            downloadedFile = null
        }
    }

    private fun isDifferent(str1: String?, str2: String?): Boolean {
        if (str1 == null || str2 == null) {
            return true
        }
        return str1 != str2
    }

    fun setResource(resource: String?) {
        if (isDifferent(resource, this.resource)) {
            sourceChanged = true
        }
        this.resource = resource
    }

    fun setResourceType(resourceType: String?) {
        if (isDifferent(resourceType, this.resourceType)) {
            sourceChanged = true
        }
        this.resourceType = resourceType
    }

    fun onDrop() {
        cleanup()
        sourceChanged = true
    }

    fun setUrlProps(props: ReadableMap?) {
        urlProps = props
    }

    fun setFadeInDuration(duration: Int) {
        fadeInDuration = duration
    }

    fun setEnableAnnotations(enableAnnotations: Boolean) {
        this.enableAnnotations = enableAnnotations
    }

    fun reload() {
        sourceChanged = true
        render()
    }

    companion object {
        const val EVENT_ON_LOAD = "onLoad"
        const val EVENT_ON_ERROR = "onError"
        const val EVENT_ON_PAGE_CHANGED = "onPageChanged"
        const val EVENT_ON_SCROLLED = "onScrolled"
    }
}
