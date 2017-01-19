package com.codemate.koffeemate.data.local

import com.codemate.koffeemate.data.local.models.CoffeeBrewingEvent

interface CoffeeEventRepository {
    fun recordBrewingEvent(userId: String? = ""): CoffeeBrewingEvent
    fun recordBrewingAccident(userId: String): CoffeeBrewingEvent
    fun getAccidentCountForUser(userId: String): Long
    fun getLastBrewingEvent(): CoffeeBrewingEvent?
    fun getLastBrewingAccident(): CoffeeBrewingEvent?
}