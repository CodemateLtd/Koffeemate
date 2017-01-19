package com.codemate.koffeemate.data.local

import com.codemate.koffeemate.data.local.models.CoffeeBrewingEvent
import io.realm.Realm
import io.realm.Sort
import java.util.*

class RealmCoffeeEventRepository : CoffeeEventRepository {
    override fun recordBrewingAccident(userId: String) = with(Realm.getDefaultInstance()) {
        var event: CoffeeBrewingEvent? = null
        executeTransaction {
            event = newEvent(it).apply {
                time = System.currentTimeMillis()
                isSuccessful = false
                this.userId = userId
            }
        }

        close()
        return@with event!!
    }

    override fun recordBrewingEvent(userId: String?) = with(Realm.getDefaultInstance()) {
        var event: CoffeeBrewingEvent? = null
        executeTransaction {
            event = newEvent(it).apply {
                time = System.currentTimeMillis()
                isSuccessful = true
                this.userId = userId ?: ""
            }
        }

        close()
        return@with event!!
    }

    override fun getAccidentCountForUser(userId: String) =
            Realm.getDefaultInstance()
                    .where(CoffeeBrewingEvent::class.java)
                    .equalTo("isSuccessful", false)
                    .equalTo("userId", userId)
                    .count()

    override fun getLastBrewingEvent(): CoffeeBrewingEvent? {
        return Realm.getDefaultInstance()
                .where(CoffeeBrewingEvent::class.java)
                .equalTo("isSuccessful", true)
                .findAllSorted("time", Sort.ASCENDING)
                .lastOrNull()
    }

    override fun getLastBrewingAccident(): CoffeeBrewingEvent? {
        return Realm.getDefaultInstance()
                .where(CoffeeBrewingEvent::class.java)
                .equalTo("isSuccessful", false)
                .findAllSorted("time", Sort.ASCENDING)
                .lastOrNull()
    }

    private fun newEvent(realm: Realm) =
            realm.createObject(
                    CoffeeBrewingEvent::class.java,
                    UUID.randomUUID().toString()
            )
}