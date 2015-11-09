package com.danielchabr.koreandiningadvisorapp.rest.service;

import com.danielchabr.koreandiningadvisorapp.model.Meal;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.ResponseBody;

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
    Call<Void> save(@Body Meal meal);

    @Multipart
    @POST("/meals/images/upload")
    Call<ResponseBody> upload(
            @Part("file\"; filename=\"image.jpg\" ") RequestBody file,
            @Part("name") RequestBody name);
}
