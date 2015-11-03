package service;

import com.danielchabr.koreandiningadvisorapp.model.Meal;
import com.danielchabr.koreandiningadvisorapp.rest.service.MealService;
import com.google.gson.Gson;
import com.squareup.okhttp.HttpUrl;
import com.squareup.okhttp.mockwebserver.MockResponse;
import com.squareup.okhttp.mockwebserver.MockWebServer;
import com.squareup.okhttp.mockwebserver.RecordedRequest;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import retrofit.GsonConverterFactory;
import retrofit.Retrofit;

import static org.junit.Assert.assertEquals;


public class MealServiceTest {
    MockWebServer server;
    MealService meals;

    @Before
    public void executedBeforeEach() throws IOException {
        server = new MockWebServer();
        server.start();

        HttpUrl baseUrl = server.url("/v1/chat/");
        Retrofit mealResource = new Retrofit.Builder().baseUrl(baseUrl).addConverterFactory(GsonConverterFactory.create()).build();
        meals = mealResource.create(MealService.class);
    }

    @Test(timeout = 500)
    public void mealServiceSaveMealSendsMealObject () throws InterruptedException, IOException {
        Meal testMeal = new Meal("김치찌개", "Kimchi Stew");
        String json = new Gson().toJson(testMeal);
        server.enqueue(new MockResponse().setBody(json));

        meals.save(testMeal).execute().body();

        RecordedRequest request = server.takeRequest();
        assertEquals(json, request.getBody().readUtf8());
    }

    @Test(timeout = 500)
    public void mealServiceSaveMealReturnsReceivedObject () throws InterruptedException, IOException {
        Meal testMeal = new Meal("김치찌개", "Kimchi Stew");
        String json = new Gson().toJson(testMeal);
        server.enqueue(new MockResponse().setBody(json));

        Meal returnedMeal = meals.save(testMeal).execute().body();

        assertEquals(returnedMeal.getEnglishName(), testMeal.getEnglishName());
        assertEquals(returnedMeal.getKoreanName(), testMeal.getKoreanName());
    }

    @After
    public void tearDown() throws Exception {
        server.shutdown();
    }
}

