package com.ojam.audiotrimmer

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.constraintlayout.widget.ConstraintLayout
import com.ojam.audiotrimmer.slidingwindow.SlidingWindowView
import kotlinx.android.synthetic.main.layout_audio_trimmer.view.*
import java.io.File
import java.util.*
import kotlin.math.roundToInt

class AudioTrimmerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : ConstraintLayout(context, attrs, defStyle), AudioTrimmerContract.View {

    private var formatBuilder: java.lang.StringBuilder? = null
    private var formatter: Formatter? = null

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
    private var borderColor: Int = Color.BLACK
    @ColorInt
    private var overlayColor: Int = Color.argb(120, 183, 191, 207)

    private var presenter: AudioTrimmerContract.Presenter? = null

    /* -------------------------------------------------------------------------------------------*/
    /* Initialize */
    init {
        inflate(context, R.layout.layout_audio_trimmer, this)
        obtainAttributes(attrs)
        initViews()
    }

    private fun obtainAttributes(attrs: AttributeSet?) {
        attrs ?: return

        val array = resources.obtainAttributes(attrs, R.styleable.AudioTrimmerView)
        try {
            leftBarRes = array.getResourceId(R.styleable.AudioTrimmerView_atv_window_left_bar, leftBarRes)
            slidingWindowView.leftBarRes = leftBarRes

            rightBarRes = array.getResourceId(R.styleable.AudioTrimmerView_atv_window_right_bar, rightBarRes)
            slidingWindowView.rightBarRes = rightBarRes

            thumbRes = array.getResourceId(R.styleable.AudioTrimmerView_atv_window_thumb, thumbRes)
            slidingWindowView.sliderThumbRes = thumbRes

            barWidth = array.getDimension(R.styleable.AudioTrimmerView_atv_window_bar_width, barWidth)
            slidingWindowView.barWidth = barWidth

            thumbWidth = array.getDimension(R.styleable.AudioTrimmerView_atv_window_bar_width, thumbWidth)
            slidingWindowView.sliderWidth = thumbWidth

            borderWidth = array.getDimension(R.styleable.AudioTrimmerView_atv_window_border_width, borderWidth)
            slidingWindowView.borderWidth = borderWidth

            borderColor = array.getColor(R.styleable.AudioTrimmerView_atv_window_border_color, borderColor)
            slidingWindowView.borderColor = borderColor

            overlayColor = array.getColor(R.styleable.AudioTrimmerView_atv_overlay_color, overlayColor)
            slidingWindowView.overlayColor = overlayColor
        } finally {
            array.recycle()
        }
    }

    private fun initViews() {
        waveFormView.sample = shortArrayOf(5, 5)
        waveFormView.isEnabled = false
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
            slidingWindowView.listener = presenter as SlidingWindowView.Listener
        }
    }

    /* -------------------------------------------------------------------------------------------*/
    /* Public APIs */
    fun setAudio(audio: File): AudioTrimmerView {
        presenter?.setAudio(audio)
        return this
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
        slidingWindowView.extraDragSpace = spaceInPx
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
        slidingWindowView.reset()
    }

    override fun setAudioProgress(progress: Float) {
        slidingWindowView.setThumbPosition(progress)
    }

    override fun setAudioSamples(samples: ShortArray) {
        for (i in samples.indices) {
            samples[i] = (samples[i] % dpToPx(context, 50F)).toInt().toShort()
        }
        waveFormView.sample = samples;
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