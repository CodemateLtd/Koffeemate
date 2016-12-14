package com.codemate.brewflop.ui.main

import com.codemate.brewflop.data.local.CoffeePreferences
import com.codemate.brewflop.data.network.SlackApi
import com.codemate.brewflop.ui.base.BasePresenter
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainPresenter(
        private val coffeePreferences: CoffeePreferences,
        private val brewingProgressUpdater: BrewingProgressUpdater,
        private val slackApi: SlackApi
) : BasePresenter<MainView>() {
    fun startCountDownForNewCoffee(newCoffeeMessage: String) {
        ensureViewIsAttached()

        if (!coffeePreferences.isChannelNameSet()) {
            getView()?.noChannelNameSet()
            return
        }

        if (!brewingProgressUpdater.isUpdating) {
            getView()?.newCoffeeIsComing()

            brewingProgressUpdater.startUpdating(
                    updateListener = { progress ->
                        getView()?.updateCoffeeProgress(progress)
                    },
                    completeListener = {
                        val channel = coffeePreferences.getChannelName()

                        slackApi.postMessage(
                                channel,
                                newCoffeeMessage
                        ).enqueue(object : Callback<ResponseBody>{
                            override fun onResponse(call: Call<ResponseBody>?, response: Response<ResponseBody>?) {
                                // TODO: Something. Do nothing for now.
                            }

                            override fun onFailure(call: Call<ResponseBody>?, t: Throwable?) {
                                t?.printStackTrace()
                            }
                        })

                        getView()?.updateCoffeeProgress(0)
                        getView()?.noCoffeeAnyMore()
                    }
            )
        } else {
            getView()?.showCancelCoffeeProgressPrompt()
        }
    }

    fun cancelCoffeeCountDown() {
        ensureViewIsAttached()

        getView()?.updateCoffeeProgress(0)
        getView()?.noCoffeeAnyMore()

        brewingProgressUpdater.reset()
    }
}