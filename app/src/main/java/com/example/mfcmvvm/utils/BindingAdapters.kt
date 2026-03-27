package com.example.mfcmvvm.utils

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.example.mfcmvvm.R

object BindingAdapters {

    @JvmStatic
    @BindingAdapter("imageUrl")
    fun loadImage(imageView: ImageView, url: String?) {
        Glide.with(imageView.context)
            .load(url)
            .placeholder(R.drawable.ic_person_placeholder)
            .error(R.drawable.ic_person_placeholder)
            .circleCrop()
            .into(imageView)
    }

    @JvmStatic
    @BindingAdapter("visible")
    fun setVisibility(view: android.view.View, isVisible: Boolean) {
        view.visibility = if (isVisible) android.view.View.VISIBLE else android.view.View.GONE
    }
}
