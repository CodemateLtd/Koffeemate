package com.codemate.brewflop.util.extensions

import android.app.Activity
import android.view.View
import android.view.WindowManager

fun Activity.hideStatusBar() {
    window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
}