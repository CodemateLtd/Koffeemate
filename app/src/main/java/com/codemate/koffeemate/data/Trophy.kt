package com.codemate.koffeemate.data

import android.content.Context
import android.graphics.*
import com.codemate.koffeemate.R

object Trophy {
    fun make(ctx: Context, value: Int): Bitmap {
        val canvasBitmap = Bitmap.createBitmap(36, 36, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(canvasBitmap)

        val trophy = BitmapFactory.decodeResource(ctx.resources, R.drawable.trophy)
        val shadowPaint = Paint()

        with(shadowPaint) {
            color = Color.parseColor("#77000000")
            textSize = 12f
            typeface = Typeface.DEFAULT_BOLD
            strokeWidth = 2.0f
            style = Paint.Style.FILL
        }

        canvas.drawBitmap(trophy, 0f, 0f, null)
        canvas.drawText(value.toString(), 14f, 15f, shadowPaint)

        return canvasBitmap
    }
}