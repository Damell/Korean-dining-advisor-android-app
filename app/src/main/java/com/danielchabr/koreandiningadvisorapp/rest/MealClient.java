package com.danielchabr.koreandiningadvisorapp.rest;

import com.danielchabr.koreandiningadvisorapp.rest.service.MealService;

import retrofit.GsonConverterFactory;
import retrofit.Retrofit;

public class MealClient
{
    private static final String BASE_URL = "http://kda-damell.rhcloud.com";
    private MealService mealService;

    public MealClient() {
        Retrofit mealResource = new Retrofit.Builder().baseUrl(BASE_URL).addConverterFactory(GsonConverterFactory.create()).build();
        mealService = mealResource.create(MealService.class);
    }

    public MealService getMealService() {
        return mealService;
    }
}
