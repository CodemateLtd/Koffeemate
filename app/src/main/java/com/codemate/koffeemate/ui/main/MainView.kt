package com.codemate.koffeemate.ui.main

import com.codemate.koffeemate.data.local.models.CoffeeBrewingEvent
import com.codemate.koffeemate.ui.base.MvpView

interface MainView : MvpView {
    fun newCoffeeIsComing()
    fun updateCoffeeProgress(newProgress: Int)
    fun resetCoffeeViewStatus()
    fun showCancelCoffeeProgressPrompt()
    fun noAnnouncementChannelSet()
    fun setLastBrewingEvent(event: CoffeeBrewingEvent)
    fun noAccidentChannelSet()
    fun launchUserSelector()
}