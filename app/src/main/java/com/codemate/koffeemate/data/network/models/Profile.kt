package com.codemate.koffeemate.data.network.models

import android.os.Parcel
import android.os.Parcelable
import io.realm.RealmObject

open class Profile(
        open var first_name: String = "",
        open var last_name: String = "",
        open var real_name: String = "",
        open var image_72: String? = null,
        open var image_192: String? = null,
        open var image_512: String? = null
) : RealmObject(), Parcelable {
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

    companion object {
        @JvmField val CREATOR: Parcelable.Creator<Profile> = object : Parcelable.Creator<Profile> {
            override fun createFromParcel(source: Parcel): Profile = Profile(source)
            override fun newArray(size: Int): Array<Profile?> = arrayOfNulls(size)
        }
    }

    constructor(source: Parcel) : this(
            source.readString(),
            source.readString(),
            source.readString()
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel?, flags: Int) {
        dest?.writeString(first_name)
        dest?.writeString(last_name)
        dest?.writeString(real_name)
    }
}
