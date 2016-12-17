package com.codemate.brewflop.data.local

interface CoffeeEventRepository {
    fun recordCoffeeBrewingEvent()
    fun recordBrewingAccident(userId: String)
}