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

package com.codemate.brewflop.data.network.models

class Profile {
    lateinit var first_name: String
    lateinit var last_name: String
    lateinit var real_name: String

    var image_72: String? = null
    var image_192: String? = null
    var image_512: String? = null

    val largestAvailableImage: String
        get() {
            var imageUrl: String? = image_512

            if (imageUrl.isNullOrBlank()) {
                imageUrl = image_192
            }

            if (imageUrl.isNullOrBlank()) {
                imageUrl = image_72
            }

            return imageUrl ?: ""
        }
}
