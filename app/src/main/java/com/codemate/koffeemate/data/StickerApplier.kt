package com.codemate.koffeemate.data

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import java.io.File
import java.io.FileOutputStream

open class StickerApplier(private val context: Context, stickerResource: Int) {
    private val sticker: Bitmap

    init {
        sticker = BitmapFactory.decodeResource(context.resources, stickerResource)
    }

    fun applySticker(profilePicture: Bitmap): File {
        val scaledProfilePic = Bitmap.createScaledBitmap(profilePicture, 512, 512, false)
        val canvas = Canvas(scaledProfilePic)

        val stickerX = (canvas.width - sticker.width).toFloat()
        val stickerY = (canvas.height - sticker.height).toFloat()
        canvas.drawBitmap(sticker, stickerX, stickerY, null)

        /*val trophy = Trophy.make(context, 1)
        val trophyX = (canvas.width - trophy.width - 10).toFloat()
        canvas.drawBitmap(trophy, trophyX, 10f, null)*/

        return savebitmap(scaledProfilePic, "temp-stickered-profile-image.png")
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