package com.codemate.koffeemate.extensions

import android.graphics.Bitmap
import com.bumptech.glide.RequestManager
import com.bumptech.glide.request.animation.GlideAnimation
import com.bumptech.glide.request.target.SimpleTarget

fun RequestManager.loadBitmap(url: String, completeListener: (Bitmap) -> Unit) {
    load(url).asBitmap()
            .into(object : SimpleTarget<Bitmap>(512, 512) {
                override fun onResourceReady(resource: Bitmap, glideAnimation: GlideAnimation<in Bitmap>?) {
                    completeListener(resource)
                }
            })
}