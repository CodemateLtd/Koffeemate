package com.codemate.koffeemate.data.network.models

class UserListResponse {
    var members = listOf<User>()
}

class User {
    lateinit var id: String
    lateinit var name: String
    lateinit var profile: Profile

    var real_name: String? = null
    var is_bot: Boolean = false
    var deleted: Boolean = false
}
