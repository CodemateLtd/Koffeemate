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

import com.codemate.koffeemate.data.local.models.CoffeeBrewingEvent
import io.realm.DynamicRealm
import io.realm.FieldAttribute
import io.realm.Realm
import io.realm.RealmConfiguration
import org.hamcrest.core.IsEqual.equalTo
import org.hamcrest.core.IsNull.nullValue
import org.junit.Assert.assertThat
import org.junit.Before
import org.junit.Test

class RealmMigrationTest {
    /*************************************************************
     * Migration tests from schema version 0 to 1
     *************************************************************/
    @Test
    fun recordedBrewingAccidentsPersist() {
        // TODO: These don't work yet
        // val realm = getRealmForSchemaVersion(1)
        // val all = realm.where(CoffeeBrewingEvent::class.java).findAll()
        // assertThat(all.size, equalTo(3))
    }

    /*************************************************************
     * Helper functions
     *************************************************************/
    private fun getRealmForSchemaVersion(schemaVersion: Long): Realm {
        val config = buildRealmConfig(schemaVersion)
        return Realm.getInstance(config)
    }

    private fun buildRealmConfig(schemaVersion: Long) =
            RealmConfiguration.Builder()
                    .assetFile("schema-v0.realm")
                    .name("test.realm")
                    .schemaVersion(schemaVersion)
                    .migration(Migration())
                    .build()
}
