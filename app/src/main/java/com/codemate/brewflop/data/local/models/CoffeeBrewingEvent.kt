package com.codemate.brewflop.data.local.models

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class CoffeeBrewingEvent(
        @PrimaryKey
        open var id: Long = 0,
        open var time: Long = 0,
        open var isSuccessful: Boolean = false,
        open var userId: String = ""
) : RealmObject()