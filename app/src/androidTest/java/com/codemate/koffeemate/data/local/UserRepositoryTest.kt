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

import com.codemate.koffeemate.data.models.User
import org.hamcrest.core.IsEqual.equalTo
import org.junit.Assert.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class UserRepositoryTest {
    val TEST_USERS_UNIQUE = listOf(User(id = "abc123"), User(id = "123abc"), User(id = "a1b2c3"))
    val TEST_USERS_DUPLICATE = listOf(User(id = "abc123"), User(id = "abc123"), User(id = "abc123"))

    lateinit var userRepository: UserRepository

    @Rule @JvmField
    val realmRule = RealmTestRule()

    @Before
    fun setUp() {
        userRepository = RealmUserRepository()
    }

    @Test
    fun addAll_WhenUsersAreUnique_PersistsAllInDatabase() {
        userRepository.addAll(TEST_USERS_UNIQUE)

        val all = userRepository.getAll()
        assertThat(all[0].id, equalTo(TEST_USERS_UNIQUE[0].id))
        assertThat(all[1].id, equalTo(TEST_USERS_UNIQUE[1].id))
        assertThat(all[2].id, equalTo(TEST_USERS_UNIQUE[2].id))
    }

    @Test
    fun addAll_WhenUsersAreDuplicate_PersistsOnlyOne() {
        userRepository.addAll(TEST_USERS_DUPLICATE)

        val all = userRepository.getAll()
        assertThat(all.size, equalTo(1))
    }

    @Test
    fun addAll_WhenTryingToAddExistingUser_UpdatesIt() {
        userRepository.addAll(listOf(User(id = "abc123", name = "John Smith")))
        assertThat(userRepository.getAll().first().name, equalTo("John Smith"))

        userRepository.addAll(listOf(User(id = "abc123", name = "Kevin Doe")))

        val all = userRepository.getAll()
        assertThat(all.size, equalTo(1))
        assertThat(all.first().name, equalTo("Kevin Doe"))
    }
}