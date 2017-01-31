package com.codemate.koffeemate.data.local

import com.codemate.koffeemate.data.local.models.CoffeeBrewingEvent
import com.codemate.koffeemate.data.network.models.User
import io.realm.Realm
import io.realm.Sort
import java.util.*

interface CoffeeEventRepository {
    fun recordBrewingEvent(user: User? = null): CoffeeBrewingEvent
    fun recordBrewingAccident(user: User): CoffeeBrewingEvent
    fun getAccidentCountForUser(user: User): Long

    fun getLastBrewingEvent(): CoffeeBrewingEvent?
    fun getLastBrewingAccident(): CoffeeBrewingEvent?
}

class RealmCoffeeEventRepository : CoffeeEventRepository {
    override fun recordBrewingEvent(user: User?) = with(Realm.getDefaultInstance()) {
        var event: CoffeeBrewingEvent? = null
        executeTransaction {
            event = newEvent(it).apply {
                time = System.currentTimeMillis()
                isSuccessful = true
                this.user = if (user != null) copyToRealmOrUpdate(user) else null
            }
        }

        close()
        return@with event!!
    }

    override fun recordBrewingAccident(user: User) = with(Realm.getDefaultInstance()) {
        var event: CoffeeBrewingEvent? = null
        executeTransaction {
            event = newEvent(it).apply {
                time = System.currentTimeMillis()
                isSuccessful = false
                this.user = copyToRealmOrUpdate(user)
            }
        }

        close()
        return@with event!!
    }

    override fun getAccidentCountForUser(user: User) =
            Realm.getDefaultInstance()
                    .where(CoffeeBrewingEvent::class.java)
                    .equalTo("isSuccessful", false)
                    .equalTo("user.id", user.id)
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