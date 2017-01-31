package com.codemate.koffeemate.data.network.models

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

class UserListResponse {
    var members = listOf<User>()
}

open class User(
        @PrimaryKey
        open var id: String = "",
        open var name: String = "",
        open var profile: Profile = Profile(),
        open var real_name: String? = null,
        open var is_bot: Boolean = false,
        open var deleted: Boolean = false
) : RealmObject()