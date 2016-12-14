package com.codemate.brewflop.data.network

import com.codemate.brewflop.BuildConfig
import com.codemate.brewflop.data.network.model.UserListResponse
import okhttp3.HttpUrl
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

interface SlackApi {

    @GET("users.list")
    fun getUsers(@Query("token") token: String): Call<UserListResponse>

    @FormUrlEncoded
    @POST("chat.postMessage")
    fun postMessage(
            @Field("channel") channel: String,
            @Field("text") text: String,
            @Field("token") token: String = BuildConfig.SLACK_AUTH_TOKEN,
            @Field("as_user") asUser: Boolean = false,
            @Field("username") username: String = "MoccaMaster Certifier",
            @Field("icon_emoji") icon: String = ":coffee:"
    ): Call<ResponseBody>

    @Multipart
    @POST("files.upload")
    fun postImage(
            @Part("token") token: RequestBody,
            @Part("channels") channels: RequestBody,
            @Part("initial_comment") comment: RequestBody,
            @Part("filename") filename: RequestBody,
            @Part file: MultipartBody.Part
    ): Call<ResponseBody>

    companion object {
        val BASE_URL = HttpUrl.parse("https://slack.com/api/")!!
    }
}
