/*
 * Copyright 2016 Codemate Ltd
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.codemate.koffeemate.common

import android.content.Context
import android.graphics.*
import com.codemate.koffeemate.R
import com.codemate.koffeemate.extensions.saveToFile
import java.io.File

interface AwardBadgeCreator {
    fun createBitmapFileWithAward(bitmap: Bitmap, awardCount: Long): File
}

/**
 * Creates a nice little badge with a number on it, stores it to a file
 * and returns the File object that points to it.
 */
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