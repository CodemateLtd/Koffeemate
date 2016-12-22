package com.codemate.koffeemate.data.local

import com.codemate.koffeemate.data.local.models.CoffeeBrewingEvent
import io.realm.Realm
import io.realm.Sort
import java.util.*

class RealmCoffeeEventRepository : CoffeeEventRepository {
    override fun recordBrewingAccident(userId: String): CoffeeBrewingEvent {
        val realm = Realm.getDefaultInstance()
        var event = CoffeeBrewingEvent()

        realm.executeTransaction {
            event = realm.createObject(CoffeeBrewingEvent::class.java, UUID.randomUUID().toString())
            with(event) {
                time = System.currentTimeMillis()
                isSuccessful = false
                event.userId = userId
            }
        }

        realm.close()
        return event
    }

    override fun recordBrewingEvent(): CoffeeBrewingEvent {
        val realm = Realm.getDefaultInstance()
        var event = CoffeeBrewingEvent()

        realm.executeTransaction {
            event = realm.createObject(CoffeeBrewingEvent::class.java, UUID.randomUUID().toString())

            with(event) {
                time = System.currentTimeMillis()
                isSuccessful = true
            }
        }

        realm.close()
        return event
    }

    override fun getAccidentCountForUser(userId: String): Long {
        val realm = Realm.getDefaultInstance()
        val count = realm.where(CoffeeBrewingEvent::class.java)
                .equalTo("isSuccessful", false)
                .equalTo("userId", userId)
                .count()

        realm.close()
        return count
    }

    override fun getLastBrewingEvent(): CoffeeBrewingEvent? {
        val realm = Realm.getDefaultInstance()
        val event = realm.where(CoffeeBrewingEvent::class.java)
                .equalTo("isSuccessful", true)
                .findAllSorted("time", Sort.ASCENDING)
                .lastOrNull()

        realm.close()
        return event
    }

    override fun getLastBrewingAccident(): CoffeeBrewingEvent? {
        val realm = Realm.getDefaultInstance()
        val accident = realm.where(CoffeeBrewingEvent::class.java)
                .equalTo("isSuccessful", false)
                .findAllSorted("time", Sort.ASCENDING)
                .lastOrNull()

        realm.close()
        return accident
    }
}