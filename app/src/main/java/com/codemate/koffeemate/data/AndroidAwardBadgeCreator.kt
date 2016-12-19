package com.codemate.koffeemate.data

import android.content.Context
import android.graphics.*
import com.codemate.koffeemate.R
import com.codemate.koffeemate.util.extensions.saveToFile
import java.io.File

interface AwardBadgeCreator {
    fun createBitmapFileWithAward(bitmap: Bitmap, awardCount: Long): File
}

class AndroidAwardBadgeCreator(private val ctx: Context) : AwardBadgeCreator {
    private val MARGIN = 5

    override fun createBitmapFileWithAward(bitmap: Bitmap, awardCount: Long): File {
        val scaledBitmap = Bitmap.createScaledBitmap(bitmap, 480, 480, false)
        val scaledBitmapCanvas = Canvas(scaledBitmap)
        val awardBitmap = BitmapFactory.decodeResource(ctx.resources, R.drawable.award)

        val awardX = (scaledBitmap.width - awardBitmap.width - MARGIN).toFloat()
        val awardY = (scaledBitmap.height - awardBitmap.height - MARGIN).toFloat()
        scaledBitmapCanvas.drawBitmap(awardBitmap, awardX, awardY, null)

        val shadowPaint = Paint()
        with(shadowPaint) {
            color = Color.parseColor("#55000000")
            textSize = 50f
            isAntiAlias = true
            typeface = Typeface.DEFAULT_BOLD
            style = Paint.Style.FILL
            textAlign = Paint.Align.CENTER
        }

        val textX = awardX + (awardBitmap.width / 2.05f)
        val textY = awardY + (awardBitmap.height / 2.15f)
        scaledBitmapCanvas.drawText(awardCount.toString(), textX, textY, shadowPaint)

        val foregroundPaint = shadowPaint
        foregroundPaint.color = Color.parseColor("#FAE9D3")
        scaledBitmapCanvas.drawText(awardCount.toString(), textX + 1, textY + 1, foregroundPaint)

        return scaledBitmap.saveToFile(ctx, "koffeemate-temp.png")
    }
}