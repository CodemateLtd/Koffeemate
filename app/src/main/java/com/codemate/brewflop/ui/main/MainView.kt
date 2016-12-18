package com.codemate.brewflop.ui.main

import com.codemate.brewflop.data.local.models.CoffeeBrewingEvent
import com.codemate.brewflop.ui.base.MvpView

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