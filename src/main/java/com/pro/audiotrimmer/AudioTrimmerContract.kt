package com.pro.audiotrimmer

import java.io.File

internal interface AudioTrimmerContract {
    interface View {
        fun getSlidingWindowWidth(): Int
        fun setupSlidingWindow()
        fun setAudioProgress(progress: Float)
        fun setAudioSamples(samples: ShortArray)
        fun setTotalAudioLength(videoLength: Long)

        fun setStartEndTimer(left: Long, right: Long)
    }

    interface Presenter {
        fun onViewAttached(view: View)
        fun onViewDetached()

        fun setAudio(audio: File)
        fun setAudioSamples(samples: ShortArray)
        fun setMaxDuration(millis: Long)
        fun setMinDuration(millis: Long)
        fun setAudioProgress(millis: Long)
        fun setOnSelectedRangeChangedListener(listener: AudioTrimmerView.OnSelectedRangeChangedListener)

        fun setInitialStartEndTimer(start: Long, end: Long)

        fun isValidState(): Boolean
        fun show()

        fun getStringForTime(timeMs: Long): String?
    }
}