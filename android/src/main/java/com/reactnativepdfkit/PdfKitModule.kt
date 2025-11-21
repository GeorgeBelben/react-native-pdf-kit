package com.reactnativepdfkit

import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReactContextBaseJavaModule

// This module is kept for potential future native methods
// The PDF functionality is primarily handled by the PDFView component
class PdfKitModule(reactContext: ReactApplicationContext) :
  ReactContextBaseJavaModule(reactContext) {

  override fun getName(): String {
    return NAME
  }

  companion object {
    const val NAME = "PdfKit"
  }
}
