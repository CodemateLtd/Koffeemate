package com.codemate.koffeemate.data.network.models

import io.realm.RealmObject

open class Profile(
        open var first_name: String = "",
        open var last_name: String = "",
        open var real_name: String = "",

        open var image_72: String? = null,
        open var image_192: String? = null,
        open var image_512: String? = null
) : RealmObject() {
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

    val smallestAvailableImage: String
        get() {
            var imageUrl: String? = image_72

            if (imageUrl.isNullOrBlank()) {
                imageUrl = image_192
            }

            if (imageUrl.isNullOrBlank()) {
                imageUrl = image_512
            }

            return imageUrl ?: ""
        }
}
