package com.danielchabr.koreandiningadvisorapp.rest;

import com.danielchabr.koreandiningadvisorapp.rest.service.MealService;
import com.danielchabr.koreandiningadvisorapp.util.LoggingInterceptor;
import com.squareup.okhttp.OkHttpClient;

import retrofit.JacksonConverterFactory;
import retrofit.Retrofit;

public class MealClient
{
    private static String BASE_URL = "http://kdadev-damell.rhcloud.com";
    private static String IMAGE_URL = "/meals/images";
    private MealService mealService;

    public MealClient() {
        mealService = mealServiceConstructor();
    }

    public MealService getMealService() {
        return mealService;
    }

    private MealService mealServiceConstructor () {
        LoggingInterceptor logging = new LoggingInterceptor();

        OkHttpClient httpClient = new OkHttpClient();

        httpClient.interceptors().add(logging);

        Retrofit mealResource = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(JacksonConverterFactory.create())
                .client(httpClient)
                .build();
        return mealResource.create(MealService.class);
    }

    private MealService mockMealServiceConstructor () {
        BASE_URL = "http://www.mocky.io";
        Retrofit mealResource = new Retrofit.Builder().baseUrl(BASE_URL).addConverterFactory(JacksonConverterFactory.create()).build();
        return mealResource.create(MealService.class);
    }

    public static String getBaseUrl() {
        return BASE_URL;
    }

    public static String getImageUrl() {
        return BASE_URL + IMAGE_URL + "/";
    }
}
