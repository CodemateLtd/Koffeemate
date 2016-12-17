package com.codemate.brewflop.data.local

interface CoffeeEventRepository {
    fun recordBrewingEvent()
    fun recordBrewingAccident(userId: String)
    fun getAccidentCountForUser(userId: String): Long
}