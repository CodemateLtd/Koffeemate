package com.codemate.brewflop.data.local

import com.codemate.brewflop.data.local.models.CoffeeBrewingEvent
import io.realm.Realm

class RealmCoffeeEventRepository : CoffeeEventRepository {
    override fun recordBrewingAccident(userId: String) {
        val realm = Realm.getDefaultInstance()
        realm.executeTransaction {
            val event = realm.createObject(CoffeeBrewingEvent::class.java)
            event.time = System.currentTimeMillis()
            event.isSuccessful = false
            event.userId = userId
        }

        realm.close()
    }

    override fun recordCoffeeBrewingEvent() {
        val realm = Realm.getDefaultInstance()
        realm.executeTransaction {
            val event = realm.createObject(CoffeeBrewingEvent::class.java)
            event.time = System.currentTimeMillis()
            event.isSuccessful = true
        }

        realm.close()
    }
}