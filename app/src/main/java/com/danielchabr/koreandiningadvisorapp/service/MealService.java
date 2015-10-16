package com.danielchabr.koreandiningadvisorapp.service;

import com.danielchabr.koreandiningadvisorapp.model.Meal;

import java.util.ArrayList;

import retrofit.Call;
import retrofit.http.Body;
import retrofit.http.POST;


public interface MealService {

    @POST("/meals")
    Call<Meal> save(@Body Meal meal);

    ArrayList<Meal> getAll();
}
