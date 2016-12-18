/*
 * Copyright 2016 Codemate Ltd
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.codemate.brewflop.data.local

import com.codemate.brewflop.data.local.models.CoffeeBrewingEvent
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

    override fun getAccidentCountForUser(userId: String) =
            Realm.getDefaultInstance()
                .where(CoffeeBrewingEvent::class.java)
                .equalTo("isSuccessful", false)
                .equalTo("userId", userId)
                .count()

    override fun getLastBrewingEvent(): CoffeeBrewingEvent? {
        return Realm.getDefaultInstance()
                .where(CoffeeBrewingEvent::class.java)
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
}