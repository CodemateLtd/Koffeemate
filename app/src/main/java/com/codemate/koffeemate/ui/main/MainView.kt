package com.codemate.koffeemate.ui.main

import com.codemate.koffeemate.data.local.models.CoffeeBrewingEvent
import com.codemate.koffeemate.ui.base.MvpView

interface MainView : MvpView {
    fun showNewCoffeeIsComing()
    fun showCancelCoffeeProgressPrompt()

    fun updateLastBrewingEvent(event: CoffeeBrewingEvent)
    fun updateCoffeeProgress(newProgress: Int)
    fun resetCoffeeViewStatus()

    fun showNoAnnouncementChannelSetError()
    fun showNoAccidentChannelSetError()

    fun launchUserSelector()
    fun showPostAccidentAnnouncementPrompt(
            userId: String,
            fullName: String,
            firstName: String,
            largestProfilePicUrl: String
    )
    fun showAccidentPostedSuccessfullyMessage()
    fun showErrorPostingAccidentMessage()
}