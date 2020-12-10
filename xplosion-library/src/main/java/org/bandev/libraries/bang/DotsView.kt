package org.bandev.libraries.bang

import android.animation.ArgbEvaluator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.util.Property
import android.view.View
import kotlin.math.cos
import kotlin.math.sin

/**
 * Created by Miroslaw Stanek on 20.12.2015.
 */
class DotsView : View {
    private val circlePaints = arrayOfNulls<Paint>(4)
    private var colors = intArrayOf(COLOR_1, COLOR_2, COLOR_3, COLOR_4)
    private var centerX = 0
    private var centerY = 0
    private var maxOuterDotsRadius = 0f
    private var maxInnerDotsRadius = 0f
    private var maxDotSize = 0f
    private var currentProgress = 0f
    private var currentRadius1 = 0f
    private var currentDotSize1 = 0f
    private var currentDotSize2 = 0f
    private var currentRadius2 = 0f
    private val argbEvaluator = ArgbEvaluator()

    constructor(context: Context?) : super(context) {
        init()
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        init()
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
    }

    private fun init() {
        setWillNotDraw(false)
        for (i in circlePaints.indices) {
            circlePaints[i] = Paint(Paint.ANTI_ALIAS_FLAG)
            circlePaints[i]!!.style = Paint.Style.FILL
        }
    }

    fun setColors(colors: IntArray) {
        if (colors.size < 4) {
            throw RuntimeException("the count of dot colors must not be less 4")
        }
        this.colors = colors
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        centerX = w / 2
        centerY = h / 2
        maxDotSize = Utils.dp2px(context, 1.1f).toFloat()
        maxOuterDotsRadius = w / 2 - maxDotSize * 2
        maxInnerDotsRadius = 0.8f * maxOuterDotsRadius
    }

    override fun onDraw(canvas: Canvas) {
        if (currentProgress == 0f) {
            canvas.drawColor(Color.TRANSPARENT)
            return
        }
        drawOuterDotsFrame(canvas)
        drawInnerDotsFrame(canvas)
    }

    private fun drawOuterDotsFrame(canvas: Canvas) {
        for (i in 0 until DOTS_COUNT) {
            val cX = (centerX + currentRadius1 * cos(i * OUTER_DOTS_POSITION_ANGLE * Math.PI / 180)).toInt()
            val cY = (centerY + currentRadius1 * sin(i * OUTER_DOTS_POSITION_ANGLE * Math.PI / 180)).toInt()
            canvas.drawCircle(cX.toFloat(), cY.toFloat(), currentDotSize1, circlePaints[i % circlePaints.size]!!)
        }
    }

    private fun drawInnerDotsFrame(canvas: Canvas) {
        for (i in 0 until DOTS_COUNT) {
            val cX = (centerX + currentRadius2 * cos((i * OUTER_DOTS_POSITION_ANGLE - 10) * Math.PI / 180)).toInt()
            val cY = (centerY + currentRadius2 * sin((i * OUTER_DOTS_POSITION_ANGLE - 10) * Math.PI / 180)).toInt()
            canvas.drawCircle(cX.toFloat(), cY.toFloat(), currentDotSize2, circlePaints[(i + 1) % circlePaints.size]!!)
        }
    }

    fun getCurrentProgress(): Float {
        return currentProgress
    }

    fun setCurrentProgress(currentProgress: Float) {
        this.currentProgress = currentProgress
        updateInnerDotsPosition()
        updateOuterDotsPosition()
        updateDotsPaints()
        updateDotsAlpha()
        postInvalidate()
    }

    private fun updateInnerDotsPosition() {
        currentRadius2 = if (currentProgress < 0.3f) {
            Utils.mapValueFromRangeToRange(currentProgress.toDouble(), 0.0, 0.3, 0.0, maxInnerDotsRadius.toDouble()).toFloat()
        } else {
            maxInnerDotsRadius
        }
        currentDotSize2 = when {
            currentProgress < 0.2 -> {
                maxDotSize
            }
            currentProgress < 0.5 -> {
                Utils.mapValueFromRangeToRange(currentProgress.toDouble(), 0.2, 0.5, maxDotSize.toDouble(), 0.5 * maxDotSize).toFloat()
            }
            else -> {
                Utils.mapValueFromRangeToRange(currentProgress.toDouble(), 0.5, 1.0, (maxDotSize * 0.5f).toDouble(), 0.0).toFloat()
            }
        }
    }

    private fun updateOuterDotsPosition() {
        currentRadius1 = if (currentProgress < 0.3f) {
            Utils.mapValueFromRangeToRange(currentProgress.toDouble(), 0.0, 0.3, 0.0, (maxOuterDotsRadius * 0.8f).toDouble()).toFloat()
        } else {
            Utils.mapValueFromRangeToRange(currentProgress.toDouble(), 0.3, 1.0, (0.8f * maxOuterDotsRadius).toDouble(), maxOuterDotsRadius.toDouble()).toFloat()
        }
        currentDotSize1 = if (currentProgress < 0.7) {
            maxDotSize
        } else {
            Utils.mapValueFromRangeToRange(currentProgress.toDouble(), 0.7, 1.0, maxDotSize.toDouble(), 0.0).toFloat()
        }
    }

    private fun updateDotsPaints() {
        if (currentProgress < 0.5f) {
            val progress = Utils.mapValueFromRangeToRange(currentProgress.toDouble(), 0.0, 0.5, 0.0, 1.0).toFloat()
            circlePaints[0]!!.color = (argbEvaluator.evaluate(progress, colors[0], colors[1]) as Int)
            circlePaints[1]!!.color = (argbEvaluator.evaluate(progress, colors[1], colors[2]) as Int)
            circlePaints[2]!!.color = (argbEvaluator.evaluate(progress, colors[2], colors[3]) as Int)
            circlePaints[3]!!.color = (argbEvaluator.evaluate(progress, colors[3], colors[0]) as Int)
        } else {
            val progress = Utils.mapValueFromRangeToRange(currentProgress.toDouble(), 0.5, 1.0, 0.0, 1.0).toFloat()
            circlePaints[0]!!.color = (argbEvaluator.evaluate(progress, colors[1], colors[2]) as Int)
            circlePaints[1]!!.color = (argbEvaluator.evaluate(progress, colors[2], colors[3]) as Int)
            circlePaints[2]!!.color = (argbEvaluator.evaluate(progress, colors[3], colors[0]) as Int)
            circlePaints[3]!!.color = (argbEvaluator.evaluate(progress, colors[0], colors[1]) as Int)
        }
    }

    private fun updateDotsAlpha() {
        val progress = Utils.clamp(currentProgress.toDouble(), 0.6, 1.0).toFloat()
        val alpha = Utils.mapValueFromRangeToRange(progress.toDouble(), 0.6, 1.0, 255.0, 0.0).toInt()
        circlePaints[0]!!.alpha = alpha
        circlePaints[1]!!.alpha = alpha
        circlePaints[2]!!.alpha = alpha
        circlePaints[3]!!.alpha = alpha
    }

    companion object {
        @JvmField
        val DOTS_PROGRESS: Property<DotsView, Float> = object : Property<DotsView, Float>(Float::class.java, "dotsProgress") {
            override fun get(`object`: DotsView): Float {
                return `object`.getCurrentProgress()
            }

            override fun set(`object`: DotsView, value: Float) {
                `object`.setCurrentProgress(value)
            }
        }
        const val COLOR_1 = -0x20bd78
        const val COLOR_2 = -0x327408
        const val COLOR_3 = -0xd4620e
        const val COLOR_4 = -0x5b114c
        private const val DOTS_COUNT = 7
        private const val OUTER_DOTS_POSITION_ANGLE = 360 / DOTS_COUNT
    }
}