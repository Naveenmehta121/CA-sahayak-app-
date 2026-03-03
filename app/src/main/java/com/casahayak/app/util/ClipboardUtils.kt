package com.casahayak.app.util

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context

/**
 * Clipboard utility for copying generated AI text.
 */
object ClipboardUtils {

    /**
     * Copies [text] to the system clipboard with a [label].
     */
    fun copyToClipboard(context: Context, text: String, label: String = "CA Sahayak") {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText(label, text)
        clipboard.setPrimaryClip(clip)
    }
}
