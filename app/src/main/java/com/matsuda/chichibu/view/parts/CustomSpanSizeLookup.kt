package com.matsuda.chichibu.view.parts

import androidx.recyclerview.widget.GridLayoutManager

object CustomSpanSizeLookup {
    const val SPAN_COUNT = 6
    object Pickup : GridLayoutManager.SpanSizeLookup() {
        override fun getSpanSize(position: Int): Int {
            return when (position % 6) {
                0 -> 6
                1 -> 2
                2 -> 4
                3, 4 -> 3
                else -> 3
            }
        }
    }

    object Food : GridLayoutManager.SpanSizeLookup() {
        override fun getSpanSize(position: Int): Int {
            return when (position % 6) {
                0 -> 6
                1, 4 -> 2
                2, 3 -> 4
                else -> 3
            }
        }
    }
}