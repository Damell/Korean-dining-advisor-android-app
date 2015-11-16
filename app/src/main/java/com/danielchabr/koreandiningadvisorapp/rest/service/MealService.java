package com.danielchabr.koreandiningadvisorapp.rest.service;

import com.danielchabr.koreandiningadvisorapp.model.Meal;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.ResponseBody;

import java.util.List;

import retrofit.Call;
import retrofit.http.Body;
import retrofit.http.DELETE;
import retrofit.http.GET;
import retrofit.http.Multipart;
import retrofit.http.POST;
import retrofit.http.PUT;
import retrofit.http.Part;
import retrofit.http.Path;


public interface MealService {

    @GET("/meals")
    Call<List<Meal>> getAll();

    @POST("/meals")
    Call<Void> save(@Body Meal meal);

    @PUT("/meals/{id}")
    Call<Void> edit(@Path("id") String id, @Body Meal meal);

    @DELETE("/meals/{id}")
    Call<Void> delete(@Path("id") String id);

    @Multipart
    @POST("/meals/images/upload")
    Call<ResponseBody> upload(
            @Part("file\"; filename=\"image.jpg\" ") RequestBody file,
            @Part("name") RequestBody name);

    @GET("/transliterate/{koreanName}")
    Call<ResponseBody> transliterate(@Path("koreanName") String koreanName);

    @GET("/translate/{koreanName}")
    Call<ResponseBody> translate(@Path("koreanName") String koreanName);
}
