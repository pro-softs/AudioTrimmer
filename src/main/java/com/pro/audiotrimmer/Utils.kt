package com.pro.audiotrimmer

import android.content.Context
import android.media.MediaMetadataRetriever

internal fun dpToPx(context: Context, dp: Float): Float {
    val density = context.resources.displayMetrics.density
    return dp * density
}

internal fun extractAudioLength(audioPath: String): Long {
    val retriever = try {
        MediaMetadataRetriever()
            .apply { setDataSource(audioPath) }
    } catch (e: IllegalArgumentException) {
        return 0L
    }

    val length = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
    retriever.release()

    return length?.toLong() ?: 0L
}