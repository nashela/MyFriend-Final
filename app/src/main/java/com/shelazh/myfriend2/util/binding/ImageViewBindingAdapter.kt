package com.shelazh.myfriend2.util.binding

import android.graphics.Bitmap
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.crocodic.core.helper.StringHelper
import com.shelazh.myfriend2.R
import com.shelazh.myfriend2.util.binding.ImageViewBindingAdapter.loadUrlString
import com.shelazh.myfriend2.util.helper.BitmapHelper

object ImageViewBindingAdapter {
    @JvmStatic
    @BindingAdapter("base64Bitmap")
    fun ImageView.loadBase64String(base64Bitmap: String?) {
        base64Bitmap?.let { value ->
            val bitmap = BitmapHelper.decodeBase64(this.context, value)
            setImageBitmap(bitmap)
        }
    }

    @JvmStatic
    @BindingAdapter("urlImage")
    fun ImageView.loadUrlString(urlImage: String?) {
        setImageBitmap(null)
        urlImage?.let { value ->
            Glide.with(context)
                .load(value)
                .into(this)
        }
    }

    @JvmStatic
    @BindingAdapter(value = ["imgBitmap"], requireAll = false)
    fun ImageView.imageBitmap(bitmapPhoto: Bitmap?){
        bitmapPhoto?.let {
            setImageDrawable(null)

            val placeholder = StringHelper.generateTextDrawable(
                StringHelper.getInitial("Profile Picture"),
                ContextCompat.getColor(context, R.color.black),
                14
            )
            Glide.with(context)
                .load(bitmapPhoto)
                .transition(DrawableTransitionOptions.withCrossFade())
                .apply(RequestOptions.centerCropTransform().circleCrop())
                .placeholder(placeholder)
                .into(this)
        }
    }
}