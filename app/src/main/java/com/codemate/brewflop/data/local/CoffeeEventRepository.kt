package com.codemate.brewflop.data.local

import com.codemate.brewflop.data.local.models.CoffeeBrewingEvent

interface CoffeeEventRepository {
    fun recordBrewingEvent(): CoffeeBrewingEvent
    fun recordBrewingAccident(userId: String): CoffeeBrewingEvent
    fun getAccidentCountForUser(userId: String): Long
    fun getLastBrewingEvent(): CoffeeBrewingEvent?
    fun getLastBrewingAccident(): CoffeeBrewingEvent?
}