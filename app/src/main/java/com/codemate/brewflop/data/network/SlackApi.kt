package com.codemate.brewflop.data.network

import com.codemate.brewflop.data.network.model.UserListResponse
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

interface SlackApi {

    @GET("users.list")
    fun getUsers(@Query("token") token: String): Call<UserListResponse>

    @FormUrlEncoded
    @POST("chat.postMessage")
    fun postMessage(
            @Field("token") token: String,
            @Field("channel") channel: String,
            @Field("text") text: String,
            @Field("as_user") asUser: Boolean = false,
            @Field("username") username: String = "MoccaMaster Certifier",
            @Field("icon_emoji") icon: String = ":coffee:"
    ): Call<ResponseBody>

    companion object {
        val BASE_URL = "https://slack.com/api/"
    }
}
