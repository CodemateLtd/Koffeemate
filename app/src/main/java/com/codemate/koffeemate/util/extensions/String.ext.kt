package com.codemate.koffeemate.util.extensions

import okhttp3.MediaType
import okhttp3.RequestBody

fun String.toRequestBody() : RequestBody {
    return RequestBody.create(MediaType.parse("text/plain"), this)
}