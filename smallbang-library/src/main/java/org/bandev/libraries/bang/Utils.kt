package org.bandev.libraries.bang

import android.content.Context

/**
 * Created by Miroslaw Stanek on 21.12.2015.
 */
object Utils {
    fun mapValueFromRangeToRange(value: Double, fromLow: Double, fromHigh: Double, toLow: Double, toHigh: Double): Double {
        return toLow + (value - fromLow) / (fromHigh - fromLow) * (toHigh - toLow)
    }

    fun clamp(value: Double, low: Double, high: Double): Double {
        return Math.min(Math.max(value, low), high)
    }

    fun dp2px(context: Context, dp: Float): Int {
        return Math.round(context.resources.displayMetrics.density * dp)
    }
}