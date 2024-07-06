package com.example.myapplication2.api;

import com.example.myapplication2.model.User;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface ApiInterface {
    @GET("users")
    Call<List<User>> getAllUser();
}