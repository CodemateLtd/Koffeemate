package com.codemate.brewflop.ui.main

import com.codemate.brewflop.data.local.RealmCoffeeEventRepository
import com.codemate.brewflop.data.local.models.CoffeeBrewingEvent
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
    fun testRecordingBrewingEvents() {
        assertThat(coffeeEventCount(), equalTo(0L))

        val firstEvent = coffeeEventRepository.recordBrewingEvent()
        coffeeEventRepository.recordBrewingEvent()
        coffeeEventRepository.recordBrewingEvent()

        assertThat(coffeeEventCount(), equalTo(3L))

        assertThat(coffeeEventRepository.getLastBrewingEvent(), equalTo(firstEvent))
    }

    @Test
    fun testRecordingBrewingAccidents() {
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