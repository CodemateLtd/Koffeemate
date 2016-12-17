package com.codemate.brewflop.data.network.model

class User {
    lateinit var id: String
    lateinit var name: String
    lateinit var profile: Profile

    var real_name: String? = null
    var is_bot: Boolean = false

    val largestAvailableProfileImageUrl: String
        get() {
            var imageUrl: String? = profile.image_512

            if (imageUrl.isNullOrBlank()) {
                imageUrl = profile.image_192
            }

            if (imageUrl.isNullOrBlank()) {
                imageUrl = profile.image_72
            }

            return imageUrl ?: ""
        }
}
