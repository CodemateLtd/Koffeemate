package com.codemate.brewflop.data.local

import com.codemate.brewflop.data.local.models.CoffeeBrewingEvent
import io.realm.Realm
import java.util.*

class RealmCoffeeEventRepository : CoffeeEventRepository {
    override fun recordBrewingAccident(userId: String) {
        val realm = Realm.getDefaultInstance()
        realm.executeTransaction {
            val event = realm.createObject(CoffeeBrewingEvent::class.java, UUID.randomUUID().toString())
            event.time = System.currentTimeMillis()
            event.isSuccessful = false
            event.userId = userId
        }

        realm.close()
    }

    override fun recordBrewingEvent() {
        val realm = Realm.getDefaultInstance()
        realm.executeTransaction {
            val event = realm.createObject(CoffeeBrewingEvent::class.java, UUID.randomUUID().toString())
            event.time = System.currentTimeMillis()
            event.isSuccessful = true
        }

        realm.close()
    }

    override fun getAccidentCountForUser(userId: String) =
            Realm.getDefaultInstance()
                .where(CoffeeBrewingEvent::class.java)
                .equalTo("isSuccessful", false)
                .equalTo("userId", userId)
                .count()
}