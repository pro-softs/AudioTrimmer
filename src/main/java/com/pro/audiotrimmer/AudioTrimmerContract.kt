package com.pro.audiotrimmer

import java.io.File

internal interface AudioTrimmerContract {
    interface View {
        fun getSlidingWindowWidth(): Int
        fun setupSlidingWindow()
        fun setAudioProgress(progress: Float)
        fun setAudioSamples(samples: ShortArray)
    }

    interface Presenter {
        fun onViewAttached(view: View)
        fun onViewDetached()

        fun setAudio(audio: File)
        fun setSamples(samples: ShortArray)
        fun setMaxDuration(millis: Long)
        fun setMinDuration(millis: Long)
        fun setAudioProgress(millis: Long)
        fun setOnSelectedRangeChangedListener(listener: AudioTrimmerView.OnSelectedRangeChangedListener)

        fun isValidState(): Boolean
        fun show()
    }
}