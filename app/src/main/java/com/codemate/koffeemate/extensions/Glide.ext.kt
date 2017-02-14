package com.codemate.koffeemate.extensions

import android.graphics.Bitmap
import com.bumptech.glide.RequestManager
import com.bumptech.glide.request.animation.GlideAnimation
import com.bumptech.glide.request.target.SimpleTarget
import com.codemate.koffeemate.R

fun RequestManager.loadBitmap(url: String, completeListener: (Bitmap) -> Unit) {
    load(url).asBitmap()
            .error(R.drawable.ic_user_unknown)
            .into(object : SimpleTarget<Bitmap>(512, 512) {
                override fun onResourceReady(resource: Bitmap, glideAnimation: GlideAnimation<in Bitmap>?) {
                    completeListener(resource)
                }
            })
}