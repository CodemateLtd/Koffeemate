package com.codemate.brewflop.ui.userselector

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import com.bumptech.glide.Glide
import com.bumptech.glide.request.animation.GlideAnimation
import com.bumptech.glide.request.target.SimpleTarget
import com.codemate.brewflop.data.network.model.User
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream


class StickerApplier(private val context: Context, stickerResource: Int) {
    private val sticker: Bitmap

    init {
        sticker = BitmapFactory.decodeResource(context.resources, stickerResource)
    }

    fun applySticker(user: User, onReadyListener: (File) -> Unit) {
        Glide.with(context)
                .load(getLargestAvailableProfileImageUrl(user))
                .asBitmap()
                .into(object : SimpleTarget<Bitmap>(512, 512){
                    override fun onResourceReady(resource: Bitmap, glideAnimation: GlideAnimation<in Bitmap>?) {
                        val scaledProfilePic = Bitmap.createScaledBitmap(resource, 512, 512, false)
                        val canvas = Canvas(scaledProfilePic)

                        val x = (canvas.width - sticker.width).toFloat()
                        val y = (canvas.height - sticker.height).toFloat()
                        canvas.drawBitmap(sticker, x, y, null)

                        val file = savebitmap(scaledProfilePic, "temp-stickered-profile-image.png")
                        onReadyListener(file)
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

    private fun savebitmap(bitmap: Bitmap, filename: String): File {
        val extStorageDirectory = context.filesDir

        val file = File(extStorageDirectory, filename)

        try {
            val outStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outStream)

            outStream.flush()
            outStream.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return file
    }
}