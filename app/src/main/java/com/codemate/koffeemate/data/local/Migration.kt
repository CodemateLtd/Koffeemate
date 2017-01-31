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

import io.realm.DynamicRealm
import io.realm.DynamicRealmObject
import io.realm.FieldAttribute
import io.realm.RealmMigration
import io.realm.exceptions.RealmPrimaryKeyConstraintException

class Migration : RealmMigration {
    override fun migrate(realm: DynamicRealm, oldVersion: Long, newVersion: Long) {
        val schema = realm.schema

        /*************************************************************
            // Version 0
            class CoffeeBrewingEvent
                @PrimaryKey
                id:             String
                time:           Long
                isSuccessful:   Boolean
                userId:         String

            // Version 1
            Changed Tables:
                class CoffeeBrewingEvent
                    @PrimaryKey
                    id:             String
                    time:           Long
                    isSuccessful:   Boolean
                    user:           User

            New Tables:
                class User
                    @PrimaryKey
                    id:             String
                    name:           String
                    profile:        Profile
                    real_name:      String
                    is_bot:         Boolean
                    deleted:        Boolean

                class Profile
                    first_name:     String
                    last_name:      String
                    real_name:      String
                    image_72:       String
                    image_192:      String
                    image_512:      String
         *************************************************************/
        if (oldVersion == 0L) {
            val profileSchema = schema.create("Profile")
                    .addField("first_name", String::class.java)
                    .addField("last_name", String::class.java)
                    .addField("real_name", String::class.java)
                    .addField("image_72", String::class.java)
                    .addField("image_192", String::class.java)
                    .addField("image_512", String::class.java)

            val userSchema = schema.create("User")
                    .addField("id", String::class.java, FieldAttribute.PRIMARY_KEY)
                    .addField("name", String::class.java)
                    .addRealmObjectField("profile", profileSchema)
                    .addField("real_name", String::class.java)
                    .addField("is_bot", Boolean::class.java)
                    .addField("deleted", Boolean::class.java)

            schema.get("CoffeeBrewingEvent")
                    .addRealmObjectField("user", userSchema)
                    .transform { brewingEvent ->
                        brewingEvent.getString("userId")?.let { previousUserId ->
                            if (previousUserId.isNotBlank()) {
                                var user: DynamicRealmObject?

                                // DynamicRealm doesn't allow us create duplicate User objects,
                                // since the id field is a primary key. insertOrUpdate() and similar
                                // methods are unavailable when using DynamicRealms.
                                //
                                // Try-catch is the only way to handle the migration of userIds to
                                // users in this case.
                                try {
                                    user = realm.createObject("User", previousUserId)
                                } catch (e: RealmPrimaryKeyConstraintException) {
                                    user = realm.where("User")
                                            .equalTo("id", previousUserId)
                                            .findFirst()
                                }

                                brewingEvent.setObject("user", user!!)
                            }
                        }
                    }
                    .removeField("userId")
        }
    }
}