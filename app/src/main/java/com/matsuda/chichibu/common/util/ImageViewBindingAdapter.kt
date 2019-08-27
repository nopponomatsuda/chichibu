package com.matsuda.chichibu.common.util

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.matsuda.chichibu.common.GlideApp

object ImageViewBindingAdapter {
    @BindingAdapter("imageResource")
    @JvmStatic
    fun setImageViewResource(imageView: ImageView, resource: Int) {
        imageView.setImageResource(resource)
    }

    @BindingAdapter("imageUrl")
    @JvmStatic
    fun setImageViewUrl(imageView: ImageView, imageUrl: String?) {
        imageUrl?:return
        GlideApp.with(imageView.context)
            .load(imageUrl)
//            .placeholder(R.drawable.placeholder)
            .fitCenter()
            .into(imageView)
    }
}