package com.github.polarisink.hi.api;

import retrofit2.Call;
import retrofit2.http.GET;

public interface RetrofitApi {
    @GET("/api/hello")
    Call<String> hello();
}
