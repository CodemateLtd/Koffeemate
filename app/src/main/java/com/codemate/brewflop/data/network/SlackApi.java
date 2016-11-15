package com.codemate.brewflop.data.network;

import com.codemate.brewflop.data.network.model.UserListResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface SlackApi {
    String BASE_URL = "https://slack.com/api/";

    @GET("users.list")
    Call<UserListResponse> getUsers(@Query("token") String token);
}
