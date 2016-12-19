package com.codemate.koffeemate.data.network.models

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
