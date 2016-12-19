package com.codemate.koffeemate.ui.main

import com.codemate.koffeemate.data.local.RealmCoffeeEventRepository
import com.codemate.koffeemate.data.local.models.CoffeeBrewingEvent
import io.realm.Realm
import io.realm.RealmConfiguration
import org.hamcrest.core.IsEqual.equalTo
import org.junit.Assert.assertThat
import org.junit.Before
import org.junit.Test

class RealmCoffeeEventRepositoryTest {
    lateinit var coffeeEventRepository: RealmCoffeeEventRepository

    @Before
    fun setUp() {
        val realmConfig = RealmConfiguration.Builder()
                .name("test.realm")
                .inMemory()
                .build()

        Realm.setDefaultConfiguration(realmConfig)
        Realm.getDefaultInstance().executeTransaction(Realm::deleteAll)

        coffeeEventRepository = RealmCoffeeEventRepository()
    }

    @Test
    fun recordBrewingEvent_PersistsEventsInDatabase() {
        coffeeEventRepository.recordBrewingEvent()
        coffeeEventRepository.recordBrewingEvent()
        coffeeEventRepository.recordBrewingEvent()

        assertThat(coffeeEventCount(), equalTo(3L))
    }

    @Test
    fun getLastBrewingEvent_ReturnsLastBrewingEvent() {
        coffeeEventRepository.recordBrewingEvent()
        coffeeEventRepository.recordBrewingEvent()

        val lastEvent = coffeeEventRepository.recordBrewingEvent()
        assertThat(coffeeEventRepository.getLastBrewingEvent(), equalTo(lastEvent))
    }

    @Test
    fun getLastBrewingEvent_WhenHavingAccidentsAndSuccessfulEvents_ReturnsOnlyLastBrewingEvent() {
        val lastSuccessfulEvent = coffeeEventRepository.recordBrewingEvent()
        coffeeEventRepository.recordBrewingAccident("test")

        assertThat(coffeeEventRepository.getLastBrewingEvent(), equalTo(lastSuccessfulEvent))
    }

    @Test
    fun getLastBrewingAccident_ReturnsLastBrewingAccident() {
        val userId = "abc123"
        coffeeEventRepository.recordBrewingAccident(userId)
        coffeeEventRepository.recordBrewingAccident(userId)

        val lastAccident = coffeeEventRepository.recordBrewingAccident(userId)
        assertThat(coffeeEventRepository.getLastBrewingAccident(), equalTo(lastAccident))
    }

    @Test
    fun getAccidentCountForUser_ReturnsAccidentCountForThatSpecificUser() {
        val userId = "abc123"
        assertThat(coffeeEventRepository.getAccidentCountForUser(userId), equalTo(0L))

        coffeeEventRepository.recordBrewingAccident(userId)
        coffeeEventRepository.recordBrewingAccident(userId)
        coffeeEventRepository.recordBrewingAccident(userId)

        val otherUserId = "someotherid"
        coffeeEventRepository.recordBrewingAccident(otherUserId)
        coffeeEventRepository.recordBrewingAccident(otherUserId)

        assertThat(coffeeEventRepository.getAccidentCountForUser(userId), equalTo(3L))
    }

    private fun coffeeEventCount() =
            Realm.getDefaultInstance().where(CoffeeBrewingEvent::class.java)
                    .equalTo("isSuccessful", true)
                    .count()
}