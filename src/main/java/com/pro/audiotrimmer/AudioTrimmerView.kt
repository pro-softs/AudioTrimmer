package com.pro.audiotrimmer

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.util.Log
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.constraintlayout.widget.ConstraintLayout
import com.pro.audiotrimmer.databinding.LayoutAudioTrimmerBinding
import com.pro.audiotrimmer.slidingwindow.SlidingWindowView

import java.io.File
import java.util.*
import kotlin.math.roundToInt

class AudioTrimmerView @JvmOverloads constructor(
    context: Context,
    val attrs: AttributeSet? = null,
    defStyle: Int = 0
) : ConstraintLayout(context, attrs, defStyle), AudioTrimmerContract.View {

    private var formatBuilder: java.lang.StringBuilder? = null
    private var formatter: Formatter? = null
    private var isWavesInit = false

    @DrawableRes
    private var leftBarRes: Int = R.drawable.trimmer_left_bar
    @DrawableRes
    private var rightBarRes: Int = R.drawable.trimmer_right_bar
    @DrawableRes
    private var thumbRes: Int = R.drawable.seek_handle

    private var barWidth: Float = dpToPx(context, 10f)
    private var thumbWidth: Float = dpToPx(context, 1f)
    private var borderWidth: Float = 0f

    @ColorInt
    private var borderColor: Int = Color.parseColor("#222730")
    @ColorInt
    private var overlayColor: Int = Color.argb(120, 183, 191, 207)

    private var presenter: AudioTrimmerContract.Presenter? = null

    private lateinit var binding: LayoutAudioTrimmerBinding

    /* -------------------------------------------------------------------------------------------*/
    /* Initialize */
    init {
        inflate(context, R.layout.layout_audio_trimmer, this)
    }


    override fun onFinishInflate() {
        super.onFinishInflate()
        this.binding = LayoutAudioTrimmerBinding.bind(this)
        obtainAttributes(attrs)
        initViews()
    }

    private fun obtainAttributes(attrs: AttributeSet?) {
        attrs ?: return

        val array = resources.obtainAttributes(attrs, R.styleable.AudioTrimmerView)
        try {
            leftBarRes = array.getResourceId(R.styleable.AudioTrimmerView_atv_window_left_bar, leftBarRes)
            binding.slidingWindowView.leftBarRes = leftBarRes

            rightBarRes = array.getResourceId(R.styleable.AudioTrimmerView_atv_window_right_bar, rightBarRes)
            binding.slidingWindowView.rightBarRes = rightBarRes

            thumbRes = array.getResourceId(R.styleable.AudioTrimmerView_atv_window_thumb, thumbRes)
            binding.slidingWindowView.sliderThumbRes = thumbRes

            barWidth = array.getDimension(R.styleable.AudioTrimmerView_atv_window_bar_width, barWidth)
            binding.slidingWindowView.barWidth = barWidth

            thumbWidth = array.getDimension(R.styleable.AudioTrimmerView_atv_window_bar_width, thumbWidth)
            binding.slidingWindowView.sliderWidth = thumbWidth

            borderWidth = array.getDimension(R.styleable.AudioTrimmerView_atv_window_border_width, borderWidth)
            binding.slidingWindowView.borderWidth = borderWidth

            borderColor = array.getColor(R.styleable.AudioTrimmerView_atv_window_border_color, borderColor)
            binding.slidingWindowView.borderColor = borderColor

            overlayColor = array.getColor(R.styleable.AudioTrimmerView_atv_overlay_color, overlayColor)
            binding.slidingWindowView.overlayColor = overlayColor
        } finally {
            array.recycle()
        }
    }

    private fun initViews() {
        if(!isWavesInit) {
            binding.waveFormView.sample = shortArrayOf(5, 5)
            binding.waveFormView.isEnabled = false
            isWavesInit = true
        }
    }

    /* -------------------------------------------------------------------------------------------*/
    /* Attach / Detach */
    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        presenter = obtainAudioTrimmerPresenter()
            .apply { onViewAttached(this@AudioTrimmerView) }
        onPresenterCreated()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        presenter?.onViewDetached()
        presenter = null
    }

    private fun onPresenterCreated() {
        presenter?.let {
            binding.slidingWindowView.listener = presenter as SlidingWindowView.Listener
        }
    }

    /* -------------------------------------------------------------------------------------------*/
    /* Public APIs */
    fun setAudio(audio: File): AudioTrimmerView {
        presenter?.setAudio(audio)

        return this
    }

    fun setInitialTimes(start: Long, end: Long) {
        setStartEndTimer(start, end)
    }

    fun setMaxDuration(millis: Long): AudioTrimmerView {
        presenter?.setMaxDuration(millis)
        return this
    }

    fun setAudioProgress(progress: Long) {
        presenter?.setAudioProgress(progress)
    }

    fun setMinDuration(millis: Long): AudioTrimmerView {
        presenter?.setMinDuration(millis)
        return this
    }

    fun setOnSelectedRangeChangedListener(listener: OnSelectedRangeChangedListener): AudioTrimmerView {
        presenter?.setOnSelectedRangeChangedListener(listener)
        return this
    }

    fun setExtraDragSpace(spaceInPx: Float): AudioTrimmerView {
        binding.slidingWindowView.extraDragSpace = spaceInPx
        return this
    }

    fun show() {
        presenter?.show()
    }

    /* -------------------------------------------------------------------------------------------*/
    /* VideoTrimmerContract.View */
    override fun getSlidingWindowWidth(): Int {
        val screenWidth = resources.displayMetrics.widthPixels
        val margin = dpToPx(context, 11f)
        return screenWidth - 2 * (margin + barWidth).roundToInt()
    }

    override fun setupSlidingWindow() {
        binding.slidingWindowView.reset()
    }

    override fun setAudioProgress(progress: Float) {
        binding.slidingWindowView.setThumbPosition(progress)
    }

    override fun setAudioSamples(samples: ShortArray) {
        for (i in samples.indices) {
            samples[i] = (samples[i] % dpToPx(context, 10F)).toInt().toShort()
        }
        binding.waveFormView.sample = samples
        isWavesInit = true
    }

    override fun setTotalAudioLength(videoLength: Long) {
        binding.slidingWindowView.setTotalDuration(videoLength)
    }

    override fun setStartEndTimer(left: Long, right: Long) {
        "${context.getString(R.string.total)} ${presenter?.getStringForTime(right)}".also { binding.endTime.text = it }
    }

    /* -------------------------------------------------------------------------------------------*/
    /* Listener */
    interface OnSelectedRangeChangedListener {
        fun onSelectRangeStart()
        fun onSelectRange(startMillis: Long, endMillis: Long)
        fun onSelectRangeEnd(startMillis: Long, endMillis: Long)
        fun onProgressStart()
        fun onProgressEnd(millis: Long)
        fun onDragProgressBar(millis: Long)
    }
}