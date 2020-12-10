package org.bandev.libraries.xplosion

import android.content.Context
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToLong

/**
 * Created by Miroslaw Stanek on 21.12.2015
 * Converted to Kotlin by Fennec on 10.12.2020
 */

object Utils {
    fun mapValueFromRangeToRange(value: Double, fromLow: Double, fromHigh: Double, toLow: Double, toHigh: Double): Double {
        return toLow + (value - fromLow) / (fromHigh - fromLow) * (toHigh - toLow)
    }

    fun clamp(value: Double, low: Double, high: Double): Double {
        return min(max(value, low), high)
    }

    fun dp2px(context: Context, dp: Float): Int {
        return (context.resources.displayMetrics.density * dp).roundToLong().toInt()
    }
}