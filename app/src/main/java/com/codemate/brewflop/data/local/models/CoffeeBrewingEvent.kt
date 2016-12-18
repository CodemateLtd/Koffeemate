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

package com.codemate.brewflop.data.local.models

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class CoffeeBrewingEvent(
        @PrimaryKey
        open var id: String = "",
        open var time: Long = 0,
        open var isSuccessful: Boolean = false,
        open var userId: String = ""
) : RealmObject()