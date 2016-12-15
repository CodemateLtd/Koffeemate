package com.codemate.brewflop.data.local

import com.codemate.brewflop.data.network.model.User

interface CoffeeStatisticLogger {
    fun recordCoffeeBrewingEvent()
    fun recordBrewingAccident(user: User)
}

class CoffeeBrewingEvent {

}
