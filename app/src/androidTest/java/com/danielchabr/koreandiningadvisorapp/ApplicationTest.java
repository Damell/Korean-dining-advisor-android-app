package com.danielchabr.koreandiningadvisorapp;

import android.app.Application;
import android.test.ApplicationTestCase;

public class ApplicationTest extends ApplicationTestCase<Application> {
    public ApplicationTest() {
        super(Application.class);
    }

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

        assertEquals(returnedMeal.getNameEnglish(), testMeal.getNameEnglish());
        assertEquals(returnedMeal.getNameKorean(), testMeal.getNameKorean());
    }

    @After
    public void tearDown() throws Exception {
        server.shutdown();
    }
}