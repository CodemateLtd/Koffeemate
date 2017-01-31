/*
 * Copyright 2017 Codemate Ltd
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

package com.codemate.koffeemate.data.local

import com.codemate.koffeemate.data.network.models.User
import io.realm.Realm

interface UserRepository {
    fun addAll(users: List<User>)
    fun getAll(): List<User>
}

class RealmUserRepository : UserRepository {
    override fun addAll(users: List<User>) {
        with(Realm.getDefaultInstance()) {
            executeTransaction { copyToRealmOrUpdate(users) }
            close()
        }
    }

    override fun getAll() = Realm.getDefaultInstance()
            .where(User::class.java)
            .findAll()
}