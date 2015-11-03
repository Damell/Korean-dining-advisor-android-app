package com.danielchabr.koreandiningadvisorapp.rest.service;

import com.danielchabr.koreandiningadvisorapp.model.Meal;
import com.squareup.okhttp.RequestBody;

import java.util.List;

import retrofit.Call;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.Multipart;
import retrofit.http.POST;
import retrofit.http.Part;


public interface MealService {

    @GET("/meals")
    Call<List<Meal>> getAll();

    @POST("/meals")
    Call<Meal> save(@Body Meal meal);

    @Multipart
    @POST("/meals/upload")
    Call<String> upload(
            @Part("myfile\"; filename=\"image.png\" ") RequestBody file,
            @Part("description") String description);
}
