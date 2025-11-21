package com.reactnativepdfkit

import android.content.Context
import android.net.Uri
import android.os.AsyncTask
import com.facebook.react.bridge.ReadableMap
import com.facebook.react.bridge.ReadableType
import java.io.BufferedInputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.net.HttpURLConnection
import java.net.URL

@Suppress("DEPRECATION") // AsyncTask is deprecated but kept for compatibility
class AsyncDownload(
    private val context: Context,
    private val url: String,
    private val file: File,
    private val urlProps: ReadableMap?,
    private val listener: TaskCompleted
) : AsyncTask<Void, Void, Void>() {

    private var exception: Exception? = null

    override fun onPreExecute() {
        super.onPreExecute()
        exception = null
    }

    private fun copyAndFlush(input: InputStream, output: OutputStream) {
        val buffer = ByteArray(BUFF_SIZE)
        var count: Int
        while (input.read(buffer).also { count = it } != -1) {
            output.write(buffer, 0, count)
        }
        output.flush()
    }

    private fun handleContentUri(uri: Uri): Void? {
        try {
            context.contentResolver.openInputStream(uri)?.use { input ->
                FileOutputStream(file).use { output ->
                    copyAndFlush(input, output)
                }
            }
        } catch (e: Exception) {
            exception = e
        }
        return null
    }

    override fun doInBackground(vararg params: Void?): Void? {
        val uri = Uri.parse(url)
        val scheme = uri.scheme

        if (url.isEmpty() || scheme == null) {
            exception = IOException("Invalid or empty url provided")
            return null
        }

        if (scheme.equals(CONTENT, ignoreCase = true)) {
            return handleContentUri(uri)
        }

        try {
            val urlObj = URL(url)
            val protocol = urlObj.protocol
            if (!protocol.equals(HTTP, ignoreCase = true) && !protocol.equals(HTTPS, ignoreCase = true)) {
                exception = IOException("Protocol \"$protocol\" is not supported")
                return null
            }

            val connection = urlObj.openConnection() as HttpURLConnection
            enrichWithUrlProps(connection)
            connection.connect()

            BufferedInputStream(connection.inputStream, BUFF_SIZE).use { input ->
                FileOutputStream(file).use { output ->
                    copyAndFlush(input, output)
                }
            }
        } catch (e: Exception) {
            exception = e
        }

        return null
    }

    override fun onPostExecute(result: Void?) {
        listener.onComplete(exception)
    }

    private fun enrichWithUrlProps(connection: HttpURLConnection) {
        if (urlProps == null) return

        setRequestMethod(connection)
        setRequestHeaders(connection)
        setRequestBody(connection)
    }

    private fun setRequestMethod(connection: HttpURLConnection) {
        var method = "GET"

        if (urlProps?.hasKey(PROP_METHOD) == true) {
            if (urlProps.getType(PROP_METHOD) != ReadableType.String) {
                throw IOException("Invalid method type. String is expected")
            }
            method = urlProps.getString(PROP_METHOD) ?: "GET"
        }

        connection.requestMethod = method
    }

    private fun setRequestHeaders(connection: HttpURLConnection) {
        if (urlProps?.hasKey(PROP_HEADERS) != true) return

        val headers = urlProps.getMap(PROP_HEADERS) ?: return
        val iterator = headers.keySetIterator()

        while (iterator.hasNextKey()) {
            val key = iterator.nextKey()
            if (headers.getType(key) == ReadableType.String) {
                connection.setRequestProperty(key, headers.getString(key))
            } else {
                throw IOException("Invalid header key type. String is expected for $key")
            }
        }
    }

    private fun setRequestBody(connection: HttpURLConnection) {
        if (urlProps?.hasKey(PROP_BODY) != true) return

        if (urlProps.getType(PROP_BODY) != ReadableType.String) {
            throw IOException("Invalid body type. String is expected")
        }

        val body = urlProps.getString(PROP_BODY) ?: return

        if (body.toByteArray().isNotEmpty()) {
            connection.doOutput = true
            connection.setRequestProperty("Content-Length", body.toByteArray().size.toString())
            connection.outputStream.use { writer ->
                writer.write(body.toByteArray())
                writer.flush()
            }
        }
    }

    interface TaskCompleted {
        fun onComplete(ex: Exception?)
    }

    companion object {
        const val HTTP = "http"
        const val HTTPS = "https"
        const val CONTENT = "content"
        private const val BUFF_SIZE = 8192
        private const val PROP_METHOD = "method"
        private const val PROP_BODY = "body"
        private const val PROP_HEADERS = "headers"
    }
}
