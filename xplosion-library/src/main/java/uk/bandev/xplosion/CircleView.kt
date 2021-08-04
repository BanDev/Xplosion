package uk.bandev.xplosion

import android.animation.ArgbEvaluator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.util.Property
import android.view.View
import kotlin.math.min

/**
 * Created by hanks
 * Converted to Kotlin by Fennec
 */

class CircleView : View {
    private var startColor = START_COLOR
    private var endColor = START_COLOR
    private val argbEvaluator = ArgbEvaluator()
    private val circlePaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val ringPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var progress = 0f

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
        circlePaint.apply {
            style = Paint.Style.FILL
            color = START_COLOR
        }
        ringPaint.apply {
            style = Paint.Style.STROKE
            color = START_COLOR
        }
    }

    override fun onDraw(canvas: Canvas) {
        if (progress == 0f) {
            canvas.drawColor(Color.TRANSPARENT)
            return
        }
        val width = width
        val height = height
        val radius = min(width, height) / 2
        if (progress <= 0.5) {
            canvas.drawCircle((width / 2).toFloat(), (height / 2).toFloat(), radius * 2 * progress, circlePaint)
            return
        }
        val strokeWidth = 2f * radius * (1f - progress)
        if (strokeWidth <= 0) {
            canvas.drawColor(Color.TRANSPARENT)
            return
        }
        ringPaint.strokeWidth = strokeWidth
        canvas.drawCircle((width / 2).toFloat(), (height / 2).toFloat(), radius - strokeWidth / 2, ringPaint)
    }

    private fun updateCircleColor() {
        if (progress <= 0.5) {
            val color = argbEvaluator.evaluate(progress * 2f, startColor, endColor) as Int
            circlePaint.color = color
        } else {
            ringPaint.color = endColor
        }
    }

    fun setStartColor(startColor: Int) {
        this.startColor = startColor
    }

    fun setEndColor(endColor: Int) {
        this.endColor = endColor
    }

    fun getProgress(): Float {
        return progress
    }

    fun setProgress(progress: Float) {
        this.progress = progress
        updateCircleColor()
        postInvalidate()
    }

    companion object {
        @JvmField
        val OUTER_CIRCLE_RADIUS_PROGRESS: Property<CircleView, Float> = object : Property<CircleView, Float>(Float::class.java, "progress") {
            override fun get(`object`: CircleView): Float {
                return `object`.getProgress()
            }

            override fun set(`object`: CircleView, value: Float) {
                `object`.setProgress(value)
            }
        }
        @JvmField
        var START_COLOR = -0x20bd78
        @JvmField
        var END_COLOR = -0x327408
    }
}