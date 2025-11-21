package com.reactnativepdfkit

import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReadableArray
import com.facebook.react.bridge.ReadableMap
import com.facebook.react.common.MapBuilder
import com.facebook.react.uimanager.SimpleViewManager
import com.facebook.react.uimanager.ThemedReactContext
import com.facebook.react.uimanager.annotations.ReactProp

class PDFViewManager(private val context: ReactApplicationContext) :
    SimpleViewManager<PDFView>() {

    override fun getName(): String = REACT_CLASS

    override fun getExportedCustomBubblingEventTypeConstants(): Map<String, Any> {
        return MapBuilder.builder<String, Any>()
            .put(
                PDFView.EVENT_ON_LOAD,
                MapBuilder.of(
                    "phasedRegistrationNames",
                    MapBuilder.of(EVENT_BUBBLED, PDFView.EVENT_ON_LOAD)
                )
            )
            .put(
                PDFView.EVENT_ON_ERROR,
                MapBuilder.of(
                    "phasedRegistrationNames",
                    MapBuilder.of(EVENT_BUBBLED, PDFView.EVENT_ON_ERROR)
                )
            )
            .put(
                PDFView.EVENT_ON_PAGE_CHANGED,
                MapBuilder.of(
                    "phasedRegistrationNames",
                    MapBuilder.of(EVENT_BUBBLED, PDFView.EVENT_ON_PAGE_CHANGED)
                )
            )
            .put(
                PDFView.EVENT_ON_SCROLLED,
                MapBuilder.of(
                    "phasedRegistrationNames",
                    MapBuilder.of(EVENT_BUBBLED, PDFView.EVENT_ON_SCROLLED)
                )
            )
            .build()
    }

    override fun createViewInstance(context: ThemedReactContext): PDFView {
        return PDFView(context)
    }

    override fun onDropViewInstance(pdfView: PDFView) {
        pdfView.onDrop()
    }

    @ReactProp(name = "resource")
    fun setResource(pdfView: PDFView, resource: String?) {
        pdfView.setResource(resource)
    }

    @ReactProp(name = "resourceType")
    fun setResourceType(pdfView: PDFView, resourceType: String?) {
        pdfView.setResourceType(resourceType)
    }

    @ReactProp(name = "fileFrom")
    fun setFileFrom(pdfView: PDFView, fileFrom: String?) {
        // iOS specific, ignoring
    }

    @ReactProp(name = "textEncoding")
    fun setTextEncoding(pdfView: PDFView, textEncoding: String?) {
        // iOS specific, ignoring
    }

    @ReactProp(name = "urlProps")
    fun setUrlProps(pdfView: PDFView, props: ReadableMap?) {
        pdfView.setUrlProps(props)
    }

    @ReactProp(name = "fadeInDuration")
    fun setFadeInDuration(pdfView: PDFView, duration: Int) {
        pdfView.setFadeInDuration(duration)
    }

    @ReactProp(name = "enableAnnotations")
    fun setEnableAnnotations(pdfView: PDFView, enableAnnotations: Boolean) {
        pdfView.setEnableAnnotations(enableAnnotations)
    }

    override fun onAfterUpdateTransaction(pdfView: PDFView) {
        super.onAfterUpdateTransaction(pdfView)
        pdfView.render()
    }

    override fun getCommandsMap(): Map<String, Int> {
        return MapBuilder.of("reload", COMMAND_RELOAD)
    }

    override fun receiveCommand(view: PDFView, command: Int, args: ReadableArray?) {
        when (command) {
            COMMAND_RELOAD -> view.reload()
        }
    }

    companion object {
        private const val REACT_CLASS = "PDFView"
        private const val EVENT_BUBBLED = "bubbled"
        private const val COMMAND_RELOAD = 1
    }
}
