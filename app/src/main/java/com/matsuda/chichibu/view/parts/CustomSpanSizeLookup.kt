package com.matsuda.chichibu.view.parts

import androidx.recyclerview.widget.GridLayoutManager

object CustomSpanSizeLookup {
    const val SPAN_COUNT = 6

    object Default : GridLayoutManager.SpanSizeLookup() {
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

    object News : GridLayoutManager.SpanSizeLookup() {
        override fun getSpanSize(position: Int): Int {
            return when (position % 6) {
                1 -> 6
                0, 4 -> 2
                2, 3 -> 4
                else -> 3
            }
        }
    }

    object Event : GridLayoutManager.SpanSizeLookup() {
        override fun getSpanSize(position: Int): Int {
            return when (position % 6) {
                0 -> 6
                1, 4 -> 2
                2, 3 -> 4
                else -> 6
            }
        }
    }

    object MyPage : GridLayoutManager.SpanSizeLookup() {
        override fun getSpanSize(position: Int): Int {
            return when (position) {
                0, 1 -> 3
                else -> 6
            }
        }
    }

    object AreaPpage : GridLayoutManager.SpanSizeLookup() {
        override fun getSpanSize(position: Int): Int {
            return 6
        }
    }
}