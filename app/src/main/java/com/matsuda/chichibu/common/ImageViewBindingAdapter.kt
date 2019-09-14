package com.matsuda.chichibu.common

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.request.RequestOptions
import kotlin.math.roundToInt

object ImageViewBindingAdapter {
    @BindingAdapter("imageResource")
    @JvmStatic
    fun setImageViewResource(imageView: ImageView, resource: Int) {
        imageView.setImageResource(resource)
    }

    @BindingAdapter("imageUrl", "size", requireAll = false)
    @JvmStatic
    fun setImageViewUrl(imageView: ImageView, imageUrl: String?, size: Int?) {
        imageUrl ?: return

        //TODO
        val length = DimenUtils.convertDp2Px(
            size?.toFloat() ?: 150F,
            imageView.context
        )
        GlideApp.with(imageView.context)
            .load(imageUrl)
            .apply(RequestOptions().override(length.roundToInt(), length.roundToInt()))
//            .placeholder(R.drawable.com_facebook_profile_picture_blank_square) //TODO
            .fitCenter()
            .into(imageView)
    }
}