package com.pro.audiotrimmer

import android.os.Handler
import android.os.Looper
import com.pro.audiotrimmer.slidingwindow.SlidingWindowView
import java.io.File
import java.util.*
import kotlin.math.min
import kotlin.math.roundToLong

internal class AudioTrimmerPresenter : AudioTrimmerContract.Presenter,
    SlidingWindowView.Listener {

    private var view: AudioTrimmerContract.View? = null

    private var audio: File? = null
    private var maxDuration = 30_000L
    private var minDuration = 3_000L

    private var onSelectedRangeChangedListener: AudioTrimmerView.OnSelectedRangeChangedListener? = null

    private var audioLength = 0L
    private var audioWindowLength = 0L

    private var rawStartMillis = 0L
    private var rawEndMillis = 0L
    private var rawProgressMillis = 0L

    private val startMillis
        get() = min(rawStartMillis, audioLength)
    private val endMillis
        get() = min(rawEndMillis, audioLength)
    private val progressMillis
        get() = min(rawProgressMillis, audioLength)

    /* -------------------------------------------------------------------------------------------*/
    /* Presenter */

    override fun onViewAttached(view: AudioTrimmerContract.View) {
        this.view = view
    }

    override fun onViewDetached() {
        this.view = null
    }

    /* -------------------------------------------------------------------------------------------*/
    /* Builder */
    override fun setAudio(audio: File) {
        this.audio = audio
    }

    override fun setAudioSamples(samples: ShortArray) {
        view?.setAudioSamples(samples)
    }

    override fun setMaxDuration(millis: Long) {
        this.maxDuration = millis
    }

    override fun setMinDuration(millis: Long) {
        this.minDuration = millis
    }

    override fun setAudioProgress(millis: Long) {
        val progress = millis / audioWindowLength.toFloat()
        view?.setAudioProgress(progress)
    }

    override fun setOnSelectedRangeChangedListener(listener: AudioTrimmerView.OnSelectedRangeChangedListener) {
        this.onSelectedRangeChangedListener = listener
    }

    override fun isValidState(): Boolean {
        return audio != null
                && maxDuration > 0L
                && minDuration > 0L
                && maxDuration >= minDuration
    }

    override fun show() {
        if (!isValidState()) {
            return
        }

        val audio = this.audio ?: return
        audioLength = extractAudioLength(audio.path)

        if (audioLength < minDuration) {
            // TODO
            return
        }

        view?.setStartEndTimer(0, audioLength)

        audioWindowLength = min(
            audioLength,
            maxDuration
        )

        val windowWidth = view?.getSlidingWindowWidth() ?: return

        rawStartMillis = 0L
        rawEndMillis = audioWindowLength
        rawProgressMillis = 0L

        view?.setupSlidingWindow()
        view?.setTotalAudioLength(audioWindowLength)

        onSelectedRangeChangedListener?.onSelectRangeEnd(rawStartMillis, rawEndMillis)
        onSelectedRangeChangedListener?.onProgressEnd(rawProgressMillis)
    }

    override fun getStringForTime(timeMs: Long): String? {
        val totalSeconds = (timeMs + 500) / 1000
        val seconds = totalSeconds % 60
        val minutes = totalSeconds / 60 % 60
        val hours = totalSeconds / 3600

        return if (hours > 0) String.format(Locale.ENGLISH, "%d:%02d:%02d", hours, minutes, seconds)
        else String.format(Locale.ENGLISH, "%02d:%02d", minutes, seconds)
    }

    /* -------------------------------------------------------------------------------------------*/
    /* SlidingWindowView.Listener */
    override fun onDragRangeBarStart() {
        onSelectedRangeChangedListener?.onSelectRangeStart()
    }

    override fun onDragRangeBar(left: Float, right: Float): Boolean {
        calculateSelectedArea(left, right)
        val duration = rawEndMillis - rawStartMillis

        if (duration < minDuration) {
            return false
        }

        view?.setStartEndTimer(rawStartMillis, rawEndMillis)


        onSelectedRangeChangedListener?.onSelectRange(rawStartMillis, rawEndMillis)

        return true
    }

    override fun onDragRangeBarEnd(left: Float, right: Float) {
        calculateSelectedArea(left, right)

        onSelectedRangeChangedListener?.onSelectRangeEnd(rawStartMillis, rawEndMillis)
    }

    override fun setInitialStartEndTimer(start: Long, end: Long) {
        view?.setStartEndTimer(start, end)
    }

    override fun onProgressEnd(progress: Float) {
        calculateDraggedPosition(progress)
        onSelectedRangeChangedListener?.onProgressEnd(rawProgressMillis)
    }

    override fun onProgressStart() {
        onSelectedRangeChangedListener?.onProgressStart()
    }

    override fun onDragProgressBar(progress: Float): Boolean {
        calculateDraggedPosition(progress)
        onSelectedRangeChangedListener?.onDragProgressBar(rawProgressMillis)

        return true
    }

    /* -------------------------------------------------------------------------------------------*/
    /* Internal helpers */
    private fun calculateSelectedArea(left: Float, right: Float) {
        rawStartMillis = (left * audioWindowLength).roundToLong()
        rawEndMillis = (right * audioWindowLength).roundToLong()
    }

    private fun calculateDraggedPosition(progress: Float) {
        rawProgressMillis = (progress * audioWindowLength).roundToLong()
    }
}