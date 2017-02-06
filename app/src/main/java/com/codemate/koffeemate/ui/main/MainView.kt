package com.codemate.koffeemate.ui.main

import com.codemate.koffeemate.data.models.CoffeeBrewingEvent
import com.codemate.koffeemate.data.models.User
import com.codemate.koffeemate.ui.base.MvpView

interface MainView : MvpView {
    fun showNewCoffeeIsComing()
    fun showCancelCoffeeProgressPrompt()

    fun updateLastBrewingEvent(event: CoffeeBrewingEvent)
    fun updateCoffeeProgress(newProgress: Int)
    fun resetCoffeeViewStatus()

    fun showNoAnnouncementChannelSetError()
    fun showNoAccidentChannelSetError()

    fun selectCoffeeBrewingPerson()
    fun clearCoffeeBrewingPerson()

    fun launchUserSelector()
    fun showPostAccidentAnnouncementPrompt(user: User)
    fun showAccidentPostedSuccessfullyMessage()
    fun showErrorPostingAccidentMessage()
}