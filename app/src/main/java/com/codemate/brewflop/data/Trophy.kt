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

package com.codemate.brewflop.data

import android.content.Context
import android.graphics.*
import com.codemate.brewflop.R

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