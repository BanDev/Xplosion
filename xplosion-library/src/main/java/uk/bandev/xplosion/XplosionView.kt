package uk.bandev.xplosion

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.annotation.TargetApi
import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.util.FloatProperty
import android.util.Property
import android.view.Gravity
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.OvershootInterpolator
import android.widget.FrameLayout
import org.bandev.libraries.R
import kotlin.math.min

/**
 * Created by hanks
 * Converted to Kotlin by Fennec
 */

class XplosionView @JvmOverloads constructor(context: Context?, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : FrameLayout(context!!, attrs, defStyleAttr) {
    private var circleStartColor: Int
    private var circleEndColor: Int
    private var animScaleFactor: Int
    private val vCircle: CircleView
    private val vDotsView: DotsView
    private var scaleView: View? = null
    private var animatorSet: AnimatorSet? = null
    private val init = false
    fun setCircleEndColor(circleEndColor: Int) {
        this.circleEndColor = circleEndColor
        vCircle.setEndColor(circleEndColor)
    }

    fun setCircleStartColor(circleStartColor: Int) {
        this.circleStartColor = circleStartColor
        vCircle.setStartColor(circleStartColor)
    }

    fun setDotColors(colors: IntArray?) {
        vDotsView.setColors(colors!!)
    }

    fun setAnimScaleFactor(animScaleFactor: Int) {
        this.animScaleFactor = animScaleFactor
        OVERSHOOT_INTERPOLATOR = OvershootInterpolator(animScaleFactor.toFloat())
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val childCount = childCount
        if (childCount != 3) {
            throw RuntimeException("must have one child view")
        }
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        val sizeWidth = MeasureSpec.getSize(widthMeasureSpec)
        val sizeHeight = MeasureSpec.getSize(heightMeasureSpec)
        if (scaleView == null) {
            scaleView = findScaleView()
        }
        val iconSize = min(scaleView!!.measuredWidth, scaleView!!.measuredHeight)
        for (i in 0 until childCount) {
            val child = getChildAt(i)
            if (child is CircleView) {
                measureChild(child,
                        MeasureSpec.makeMeasureSpec((iconSize * 1.5f).toInt(), MeasureSpec.EXACTLY),
                        MeasureSpec.makeMeasureSpec((iconSize * 1.5f).toInt(), MeasureSpec.EXACTLY))
            } else if (child is DotsView) {
                measureChild(child,
                        MeasureSpec.makeMeasureSpec((iconSize * 2.5f).toInt(), MeasureSpec.EXACTLY),
                        MeasureSpec.makeMeasureSpec((iconSize * 2.5f).toInt(), MeasureSpec.EXACTLY))
            }
        }
        var width = sizeWidth
        var height = sizeHeight
        if (widthMode == MeasureSpec.AT_MOST || widthMode == MeasureSpec.UNSPECIFIED) {
            width = (iconSize * 2.5f).toInt()
        }
        if (heightMode == MeasureSpec.AT_MOST || heightMode == MeasureSpec.UNSPECIFIED) {
            height = (iconSize * 2.5f).toInt()
        }
        setMeasuredDimension(width, height)
    }

    private fun findScaleView(): View {
        for (i in 0 until childCount) {
            val child = getChildAt(i)
            if (isScaleView(child)) {
                return child
            }
        }
        throw RuntimeException("must have one child in SmallBangView")
    }

    private fun isScaleView(child: View?): Boolean {
        return child != null && child !is DotsView && child !is CircleView
    }

    fun stopAnimation() {
        animatorSet?.cancel()
        scaleX = 1f
        scaleY = 1f
        vCircle.setProgress(0f)
        vDotsView.setCurrentProgress(0f)
    }

    @JvmOverloads
    fun likeAnimation(listener: Animator.AnimatorListener? = null) {
        animatorSet?.cancel()
        scaleX = 0f
        scaleY = 0f
        vCircle.setProgress(0f)
        vDotsView.setCurrentProgress(0f)
        val outerCircleAnimator = ObjectAnimator.ofFloat(vCircle, CircleView.OUTER_CIRCLE_RADIUS_PROGRESS, 0.1f, 1f).apply {
            duration = 250
        }
        val starScaleAnimator = ObjectAnimator.ofFloat(scaleView, SCALE, 0.2f, 1f).apply {
            duration = 250
            startDelay = 250
            interpolator = OVERSHOOT_INTERPOLATOR
        }
        val dotsAnimator = ObjectAnimator.ofFloat(vDotsView, DotsView.DOTS_PROGRESS, 0f, 1f).apply {
            duration = 800
            startDelay = 50
            interpolator = ACCELERATE_DECELERATE_INTERPOLATOR
        }
        animatorSet = AnimatorSet().apply {
            playTogether(
                outerCircleAnimator,
                starScaleAnimator,
                dotsAnimator
            )
            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationCancel(animation: Animator) {
                    vCircle.setProgress(0f)
                    vDotsView.setCurrentProgress(0f)
                    scaleX = 1f
                    scaleY = 1f
                }
            })
            start()
            if (listener != null) {
                addListener(listener)
            }
        }
    }

    companion object {
        val SCALE: Property<View?, Float> = @TargetApi(Build.VERSION_CODES.N)
        object : FloatProperty<View>("scale") {
            override fun setValue(`object`: View, value: Float) {
                `object`.scaleX = value
                `object`.scaleY = value
            }

            override fun get(`object`: View): Float {
                return `object`.scaleY
            }
        }
        private lateinit var ACCELERATE_DECELERATE_INTERPOLATOR: AccelerateDecelerateInterpolator
        private lateinit var OVERSHOOT_INTERPOLATOR: OvershootInterpolator
    }

    init {
        setWillNotDraw(false)
        val array = getContext().obtainStyledAttributes(attrs, R.styleable.XplosionView, defStyleAttr, 0)
        circleStartColor = array.getColor(R.styleable.XplosionView_circle_start_color,
            CircleView.START_COLOR
        )
        circleEndColor = array.getColor(R.styleable.XplosionView_circle_end_color,
            CircleView.END_COLOR
        )
        val dotPrimaryColor = array.getColor(R.styleable.XplosionView_dots_primary_color,
            DotsView.COLOR_1
        )
        val dotSecondaryColor = array.getColor(R.styleable.XplosionView_dots_secondary_color,
            DotsView.COLOR_2
        )
        animScaleFactor = array.getColor(R.styleable.XplosionView_anim_scale_factor, 3)
        val status = array.getBoolean(R.styleable.XplosionView_liked, false)
        isSelected = status
        array.recycle()
        OVERSHOOT_INTERPOLATOR = OvershootInterpolator(animScaleFactor.toFloat())
        ACCELERATE_DECELERATE_INTERPOLATOR = AccelerateDecelerateInterpolator()
        val dotParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT).apply {
            gravity = Gravity.CENTER
        }
        vDotsView = DotsView(getContext()).apply {
            layoutParams = dotParams
            setColors(intArrayOf(dotPrimaryColor, dotSecondaryColor, dotPrimaryColor, dotSecondaryColor))
        }
        val circleParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT).apply {
            gravity = Gravity.CENTER
        }
        vCircle = CircleView(getContext()).apply {
            setStartColor(circleStartColor)
            setEndColor(circleEndColor)
            layoutParams = circleParams
        }
        addView(vDotsView)
        addView(vCircle)
    }
}