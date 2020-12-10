package org.bandev.libraries.bang

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.util.AttributeSet
import android.util.FloatProperty
import android.util.Property
import android.view.Gravity
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.OvershootInterpolator
import android.widget.FrameLayout
import org.bandev.libraries.R

/**
 * Created by hanks.
 */
class SmallBangView @JvmOverloads constructor(context: Context?, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : FrameLayout(context!!, attrs, defStyleAttr) {
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
        val iconSize = Math.min(scaleView!!.measuredWidth, scaleView!!.measuredHeight)
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
        if (animatorSet != null) {
            animatorSet!!.cancel()
        }
        scaleView!!.scaleX = 1f
        scaleView!!.scaleY = 1f
        vCircle.setProgress(0f)
        vDotsView.setCurrentProgress(0f)
    }

    @JvmOverloads
    fun likeAnimation(listener: Animator.AnimatorListener? = null) {
        if (animatorSet != null) {
            animatorSet!!.cancel()
        }
        scaleView!!.scaleX = 0f
        scaleView!!.scaleY = 0f
        vCircle.setProgress(0f)
        vDotsView.setCurrentProgress(0f)
        animatorSet = AnimatorSet()
        val outerCircleAnimator = ObjectAnimator.ofFloat(vCircle, CircleView.OUTER_CIRCLE_RADIUS_PROGRESS, 0.1f, 1f)
        outerCircleAnimator.duration = 250
        val starScaleAnimator = ObjectAnimator.ofFloat(scaleView, SCALE, 0.2f, 1f)
        starScaleAnimator.duration = 250
        starScaleAnimator.startDelay = 250
        starScaleAnimator.interpolator = OVERSHOOT_INTERPOLATOR
        val dotsAnimator = ObjectAnimator.ofFloat(vDotsView, DotsView.DOTS_PROGRESS, 0f, 1f)
        dotsAnimator.duration = 800
        dotsAnimator.startDelay = 50
        dotsAnimator.interpolator = ACCELERATE_DECELERATE_INTERPOLATOR
        animatorSet!!.playTogether(
                outerCircleAnimator,
                starScaleAnimator,
                dotsAnimator
        )
        animatorSet!!.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationCancel(animation: Animator) {
                vCircle.setProgress(0f)
                vDotsView.setCurrentProgress(0f)
                scaleView!!.scaleX = 1f
                scaleView!!.scaleY = 1f
            }
        })
        animatorSet!!.start()
        if (listener != null) {
            animatorSet!!.addListener(listener)
        }
    }

    companion object {
        val SCALE: Property<View?, Float> = object : FloatProperty<View>("scale") {
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
        val array = getContext().obtainStyledAttributes(attrs, R.styleable.SmallBangView, defStyleAttr, 0)
        circleStartColor = array.getColor(R.styleable.SmallBangView_circle_start_color, CircleView.START_COLOR)
        circleEndColor = array.getColor(R.styleable.SmallBangView_circle_end_color, CircleView.END_COLOR)
        val dotPrimaryColor = array.getColor(R.styleable.SmallBangView_dots_primary_color, DotsView.COLOR_1)
        val dotSecondaryColor = array.getColor(R.styleable.SmallBangView_dots_secondary_color, DotsView.COLOR_2)
        animScaleFactor = array.getColor(R.styleable.SmallBangView_anim_scale_factor, 3)
        val status = array.getBoolean(R.styleable.SmallBangView_liked, false)
        isSelected = status
        array.recycle()
        OVERSHOOT_INTERPOLATOR = OvershootInterpolator(animScaleFactor.toFloat())
        ACCELERATE_DECELERATE_INTERPOLATOR = AccelerateDecelerateInterpolator()
        val dotParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
        dotParams.gravity = Gravity.CENTER
        vDotsView = DotsView(getContext())
        vDotsView.layoutParams = dotParams
        vDotsView.setColors(intArrayOf(dotPrimaryColor, dotSecondaryColor, dotPrimaryColor, dotSecondaryColor))
        val circleParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
        circleParams.gravity = Gravity.CENTER
        vCircle = CircleView(getContext())
        vCircle.setStartColor(circleStartColor)
        vCircle.setEndColor(circleEndColor)
        vCircle.layoutParams = circleParams
        addView(vDotsView)
        addView(vCircle)
    }
}