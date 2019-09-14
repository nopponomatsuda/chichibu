package com.matsuda.chichibu.common

import android.content.Context

object DimenUtils {

    /**
     * to pixel from dp
     * @param dp
     * @param context
     * @return float pixel
     */
    fun convertDp2Px(dp: Float, context: Context): Float {
        val metrics = context.resources.displayMetrics
        return dp * metrics.density
    }

    /**
     * to dp from pixel
     * @param px
     * @param context
     * @return float dp
     */
    fun convertPx2Dp(px: Int, context: Context): Float {
        val metrics = context.resources.displayMetrics
        return px / metrics.density
    }
}