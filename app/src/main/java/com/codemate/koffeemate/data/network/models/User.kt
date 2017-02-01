package com.codemate.koffeemate.data.network.models

import android.os.Parcel
import android.os.Parcelable
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

class UserListResponse {
    var members = listOf<User>()
}

fun List<User>.isFreshEnough(maxStaleness: Long): Boolean {
    val oldestAcceptedTimestamp = System.currentTimeMillis() - maxStaleness
    val sorted = sortedByDescending(User::last_updated)
    val freshestUser = sorted.first()

    return freshestUser.last_updated > oldestAcceptedTimestamp
}

open class User(
        @PrimaryKey
        open var id: String = "",
        open var name: String = "",
        open var profile: Profile = Profile(),
        open var real_name: String? = null,
        open var is_bot: Boolean = false,
        open var deleted: Boolean = false,
        open var last_updated: Long = 0
) : RealmObject(), Parcelable {
    companion object {
        @JvmField val CREATOR: Parcelable.Creator<User> = object : Parcelable.Creator<User> {
            override fun createFromParcel(source: Parcel): User = User(source)
            override fun newArray(size: Int): Array<User?> = arrayOfNulls(size)
        }
    }

    constructor(source: Parcel) : this(
            source.readString(),
            source.readString(),
            source.readParcelable(ClassLoader.getSystemClassLoader()),
            source.readString(),
            1 == source.readInt(),
            1 == source.readInt(),
            source.readLong()
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel?, flags: Int) {
        dest?.writeString(id)
        dest?.writeString(name)
        dest?.writeParcelable(profile, 0)
        dest?.writeString(real_name)
        dest?.writeInt((if (is_bot) 1 else 0))
        dest?.writeInt((if (deleted) 1 else 0))
        dest?.writeLong(last_updated)
    }
}