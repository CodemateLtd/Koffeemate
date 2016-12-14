package com.codemate.brewflop.ui.main

import com.codemate.brewflop.ui.base.MvpView

interface MainView : MvpView {
    fun newCoffeeIsComing()
    fun updateCoffeeProgress(newProgress: Int)
    fun newCoffeeAvailable()
    fun noCoffeeAnyMore()
    fun showCancelCoffeeProgressPrompt()
    fun noChannelNameSet()
}