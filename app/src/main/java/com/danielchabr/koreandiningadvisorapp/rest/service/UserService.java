package com.danielchabr.koreandiningadvisorapp.rest.service;

import com.danielchabr.koreandiningadvisorapp.model.User;

import retrofit.Call;
import retrofit.http.Body;
import retrofit.http.POST;

/**
 * Created by damell on 16/11/15.
 */
public interface UserService {

    @POST("/users/authenticate")
    Call<Boolean> authenticate(@Body User user);

    @POST("/users")
    Call<Void> create(@Body User user);
}
