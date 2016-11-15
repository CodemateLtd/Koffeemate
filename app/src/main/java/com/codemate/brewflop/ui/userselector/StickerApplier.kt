package com.codemate.brewflop.ui.userselector

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import com.bumptech.glide.Glide
import com.bumptech.glide.request.animation.GlideAnimation
import com.bumptech.glide.request.target.SimpleTarget
import com.codemate.brewflop.R
import com.codemate.brewflop.data.network.model.User

class StickerApplier(private val context: Context) {
    fun applySticker(user: User, baseImage: Bitmap, onReadyListener: (Bitmap) -> Unit) {
        Glide.with(context)
                .load(getLargestAvailableProfileImageUrl(user))
                .asBitmap()
                .into(object : SimpleTarget<Bitmap>(512, 512){
                    override fun onResourceReady(resource: Bitmap, glideAnimation: GlideAnimation<in Bitmap>?) {
                        val scaledProfilePic = Bitmap.createScaledBitmap(resource, 512, 512, false)
                        val canvas = Canvas(scaledProfilePic)

                        val sticker = baseImage
                        val x = (canvas.width - sticker.width).toFloat()
                        val y = (canvas.height - sticker.height).toFloat()
                        canvas.drawBitmap(sticker, x, y, null)

                        onReadyListener(scaledProfilePic)
                    }
                })
    }

    private fun getLargestAvailableProfileImageUrl(user: User): String {
        var imageUrl = user.profile.image512

        if (imageUrl.isNullOrEmpty()) {
            imageUrl = user.profile.image192
        }

        if (imageUrl.isNullOrEmpty()) {
            imageUrl = user.profile.image72
        }

        return imageUrl
    }
}