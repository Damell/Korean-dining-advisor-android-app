package com.danielchabr.koreandiningadvisorapp.rest;

import com.danielchabr.koreandiningadvisorapp.rest.service.MealService;

import retrofit.JacksonConverterFactory;
import retrofit.Retrofit;

public class MealClient
{
    private static String BASE_URL = "https://kda-damell.rhcloud.com/meals";
    private MealService mealService;

    public MealClient() {
        mealService = mealServiceConstructor();
    }

    public MealService getMealService() {
        return mealService;
    }

    private MealService mealServiceConstructor () {
        Retrofit mealResource = new Retrofit.Builder().baseUrl(BASE_URL).addConverterFactory(JacksonConverterFactory.create()).build();
        return mealResource.create(MealService.class);
    }

    private MealService mockMealServiceConstructor () {
        BASE_URL = "http://www.mocky.io";
        Retrofit mealResource = new Retrofit.Builder().baseUrl(BASE_URL).addConverterFactory(JacksonConverterFactory.create()).build();
        return mealResource.create(MealService.class);
    }
}
