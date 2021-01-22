package com.ojam.audiotrimmer

interface SeekBarOnProgressChanged {

    fun onProgressChanged(waveformSeekBar: WaveformSeekBar, progress: Int, fromUser: Boolean)
}