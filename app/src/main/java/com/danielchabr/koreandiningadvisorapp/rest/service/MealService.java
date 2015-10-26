package com.danielchabr.koreandiningadvisorapp.rest.service;

import com.danielchabr.koreandiningadvisorapp.model.Meal;

import java.util.List;

import retrofit.Call;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;


public interface MealService {

    @POST("/meals")
    Call<Meal> save(@Body Meal meal);

    @GET("/v2/562de56d1100002a0f933a82")
    Call<List<Meal>> getAll();
}
