package com.danielchabr.koreandiningadvisorapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.danielchabr.koreandiningadvisorapp.model.Meal;
import com.danielchabr.koreandiningadvisorapp.rest.ApiClient;
import com.danielchabr.koreandiningadvisorapp.rest.service.MealService;
import com.danielchabr.koreandiningadvisorapp.util.Consts;
import com.danielchabr.koreandiningadvisorapp.util.ImageHandler;
import com.danielchabr.koreandiningadvisorapp.util.MemoryCache;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.ResponseBody;

import org.parceler.Parcels;

import java.io.FileNotFoundException;
import java.io.IOException;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

public class InsertMealActivity extends AppCompatActivity {
    private final int REQUEST_CODE = 5;
    private Bitmap bitmap;
    private Uri photoUri;
    private ImageView mealPhotoView;
    private final int MAX_IMAGE_DIMENSION = 180;
    private MealService mealService;
    private Meal meal;
    private Button uploadImageButton;
    private Button transliterateButton;
    private Button translateButton;
    private Button clearIngredients;
    private LinearLayout ingredientsView;
    private LinearLayout categoriesView;
    private EditText koreanName;
    private EditText englishName;
    private EditText transliteratedName;
    private EditText description;
    private ApiClient apiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert_meal);

        Bundle extras = getIntent().getExtras();
        if (savedInstanceState != null) {
            meal = Parcels.unwrap(savedInstanceState.getParcelable("meal"));
        } else if (extras != null) {
            meal = Parcels.unwrap(extras.getParcelable("meal"));
        }

        apiClient = new ApiClient();
        meal = new Meal();
        koreanName = (EditText) findViewById(R.id.inputKoreanName);
        englishName = (EditText) findViewById(R.id.inputEnglishName);
        transliteratedName = (EditText) findViewById(R.id.transliteratedName);
        description = (EditText) findViewById(R.id.description_edit);
        Button chooseImageButton = (Button) findViewById(R.id.choose_photo_button);
        chooseImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT, null);
                galleryIntent.setType("image/*");
                galleryIntent.addCategory(Intent.CATEGORY_OPENABLE);

                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);


                Intent chooser = new Intent(Intent.ACTION_CHOOSER);
                chooser.putExtra(Intent.EXTRA_INTENT, galleryIntent);
                chooser.putExtra(Intent.EXTRA_TITLE, "title");

                Intent[] intentArray = {cameraIntent};
                chooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentArray);
                startActivityForResult(chooser, REQUEST_CODE);
            }
        });
        mealPhotoView = (ImageView) findViewById(R.id.insert_photo_view);
        mealService = apiClient.getMealService();
        uploadImageButton = (Button) findViewById(R.id.upload_photo_button);
        uploadImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (meal.getFile() == null) {
                    Log.v("Upload", "You have not chosen any image to upload");
                    AlertDialog.Builder builder = new AlertDialog.Builder(InsertMealActivity.this);
                    builder.setMessage("You have not chosen any image to upload")
                            .setTitle("No image to upload");
                    AlertDialog dialog = builder.create();
                    dialog.show();
                } else {
                    RequestBody image = RequestBody.create(MediaType.parse("image/*"), meal.getFile());
                    RequestBody filename = RequestBody.create(MediaType.parse("text/plain"), "test.jpg");
                    Call call = mealService.upload(image, filename);
                    Log.v("Upload", "Image file uploading start");
                    final ProgressDialog progress = new ProgressDialog(InsertMealActivity.this);
                    progress.setTitle("Loading");
                    progress.setMessage("Wait while loading...");
                    progress.show();
                    call.enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Response<ResponseBody> response, Retrofit retrofit) {
                            if (!InsertMealActivity.this.isFinishing() && progress.isShowing())
                                progress.dismiss();
                            if (response.isSuccess()) {
                                try {
                                    String url = response.body().string();
                                    meal.setPhotoUrl(url);
                                    Log.v("Upload", "success");
                                    Log.v("Upload", "url: " + url);
                                    uploadImageButton.setText("Uploaded");

                                    uploadImageButton.setEnabled(false);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            } else {
                                Log.v("Upload", "failure: " + response.errorBody());
                                Log.v("Upload", "failure: " + response.message());
                                Log.v("Upload", "failure: " + response.code());
                                Log.v("Upload", "failure: " + response.body());
                            }
                        }

                        @Override
                        public void onFailure(Throwable t) {
                            if (!InsertMealActivity.this.isFinishing() && progress.isShowing())
                                progress.dismiss();
                            Log.e("Upload", t.getMessage());
                            AlertDialog.Builder builder = new AlertDialog.Builder(InsertMealActivity.this);
                            builder.setMessage("Network error")
                                    .setTitle("No network connection");
                            AlertDialog dialog = builder.create();
                            dialog.show();
                        }
                    });
                }
            }
        });
        categoriesView = (LinearLayout) findViewById(R.id.categories);
        int themeId = Resources.getSystem().getIdentifier("btn_check_holo_light", "drawable", "android");
        for (String category : Consts.CATEGORIES) {
            CheckBox checkBox = new CheckBox(getApplicationContext());
            checkBox.setTextColor(Color.BLACK);
            checkBox.setButtonDrawable(themeId);
            checkBox.setText(category);
            categoriesView.addView(checkBox);
        }
        ingredientsView = (LinearLayout) findViewById(R.id.ingredients);
        Button addIngredientButton = (Button) findViewById(R.id.addIngredient);
        addIngredientButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextView ingredient = (TextView) findViewById(R.id.newIngredient);
                if (!ingredient.getText().toString().isEmpty()) {
                    TextView textView = new TextView(getApplicationContext());
                    textView.setTextColor(Color.BLACK);
                    textView.setText(ingredient.getText().toString());
                    ingredientsView.addView(textView);
                    ingredient.setText("");
                }
            }
        });
        clearIngredients = (Button) findViewById(R.id.clearIngredients);
        clearIngredients.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ingredientsView.removeAllViews();
            }
        });
        transliterateButton = (Button) findViewById(R.id.generateTransliteration);
        transliterateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String TAG = "GenerateTransliteration";
                if (koreanName.getText().toString().isEmpty()) {
                    Log.v(TAG, "No korean name");
                    AlertDialog.Builder builder = new AlertDialog.Builder(InsertMealActivity.this);
                    builder.setMessage("No string to transliterate")
                            .setTitle("No string to transliterate");
                    AlertDialog dialog = builder.create();
                    dialog.show();
                } else {
                    Call<ResponseBody> call = apiClient.getMealService().transliterate(koreanName.getText().toString());
                    call.enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Response<ResponseBody> response, Retrofit retrofit) {
                            if (response.isSuccess()) {
                                try {
                                    transliteratedName.setText(response.body().string());
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            } else {
                                Log.v(TAG, "failure: " + response.errorBody());
                                Log.v(TAG, "failure: " + response.message());
                                Log.v(TAG, "failure: " + response.code());
                                Log.v(TAG, "failure: " + response.body());
                            }
                        }

                        @Override
                        public void onFailure(Throwable t) {
                            Log.e("Upload", t.getMessage());
                            AlertDialog.Builder builder = new AlertDialog.Builder(InsertMealActivity.this);
                            builder.setMessage("Network error")
                                    .setTitle("No network connection");
                            AlertDialog dialog = builder.create();
                            dialog.show();
                        }
                    });
                }
            }
        });
        translateButton = (Button) findViewById(R.id.generateTranslation);
        translateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String TAG = "GenerateTranslation";
                if (koreanName.getText().toString().isEmpty()) {
                    Log.v(TAG, "No korean name");
                    AlertDialog.Builder builder = new AlertDialog.Builder(InsertMealActivity.this);
                    builder.setMessage("No string to transliterate")
                            .setTitle("No string to transliterate");
                    AlertDialog dialog = builder.create();
                    dialog.show();
                } else {
                    Call<ResponseBody> call = apiClient.getMealService().translate(koreanName.getText().toString());
                    call.enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Response<ResponseBody> response, Retrofit retrofit) {
                            if (response.isSuccess()) {
                                try {
                                    englishName.setText(response.body().string());
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            } else {
                                Log.v(TAG, "failure: " + response.errorBody());
                                Log.v(TAG, "failure: " + response.message());
                                Log.v(TAG, "failure: " + response.code());
                                Log.v(TAG, "failure: " + response.body());
                            }
                        }

                        @Override
                        public void onFailure(Throwable t) {
                            Log.e("Upload", t.getMessage());
                            AlertDialog.Builder builder = new AlertDialog.Builder(InsertMealActivity.this);
                            builder.setMessage("Network error")
                                    .setTitle("No network connection");
                            AlertDialog dialog = builder.create();
                            dialog.show();
                        }
                    });
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_insert, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            finish();
            return true;
        } else if (id == R.id.action_insert) {
            meal.setKoreanName(koreanName.getText().toString());
            meal.setEnglishName(englishName.getText().toString());
            meal.setTransliteratedName(transliteratedName.getText().toString());
            meal.setDescription(description.getText().toString());
            if (meal.getKoreanName().trim().isEmpty()) {
                AlertDialog.Builder builder = new AlertDialog.Builder(InsertMealActivity.this);
                builder.setMessage("Korean name of a dish needs to be filled")
                        .setTitle("Missing values");
                AlertDialog dialog = builder.create();
                dialog.show();
                return false;
            }
            for (int i = 0; i < categoriesView.getChildCount(); i++) {
                CheckBox checkBox = (CheckBox) categoriesView.getChildAt(i);
                if (checkBox.isChecked()) {
                    meal.addCategory(checkBox.getText().toString());
                }
            }
            for (int i = 0; i < ingredientsView.getChildCount(); i++) {
                TextView textView = (TextView) ingredientsView.getChildAt(i);
                meal.addIngredient(textView.getText().toString());
            }
            RatingBar ratingBar = (RatingBar) findViewById(R.id.insert_meal_ratingBar);
            meal.setRating((int) ratingBar.getRating());
            Spinner spinner = (Spinner) findViewById(R.id.spiciness);
            meal.setSpicyGrade(spinner.getSelectedItemPosition() - 1);

            Call call = apiClient.getMealService().save(meal);
            final ProgressDialog progress = new ProgressDialog(this);
            progress.setTitle("Loading");
            progress.setMessage("Wait while loading...");
            progress.show();
            call.enqueue(new Callback() {
                @Override
                public void onResponse(Response response, Retrofit retrofit) {
                    if (!InsertMealActivity.this.isFinishing() && progress.isShowing())
                        progress.dismiss();
                    if (response.isSuccess()) {
                        Log.v("CreateMeal", "successfully created meal");
                        Log.v("CreateMeal", "code: " + response.code());

                        Intent showDashboard = new Intent();
                        showDashboard.putExtra("createdMeal", Parcels.wrap(meal));
                        setResult(Activity.RESULT_OK, showDashboard);
                        finish();
                    } else {
                        Log.v("CreateMeal", "response: " + response.body());
                        Log.v("CreateMeal", "response: " + response.errorBody().toString());
                        Log.v("CreateMeal", "response: " + response.message());
                        Log.v("CreateMeal", "code: " + response.code());
                    }
                }

                @Override
                public void onFailure(Throwable t) {
                    if (!InsertMealActivity.this.isFinishing() && progress.isShowing())
                        progress.dismiss();
                    Log.v("CreateMeal", "error creating meal");
                    Log.v("CreateMeal", t.getMessage());
                    AlertDialog.Builder builder = new AlertDialog.Builder(InsertMealActivity.this);
                    builder.setMessage("Network error")
                            .setTitle("No network connection");
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            });

        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK)
            try {
                // We need to recyle unused bitmaps
                if (bitmap != null) {
                    bitmap.recycle();
                }
                photoUri = data.getData();
                bitmap = ImageHandler.getBitmap(this, photoUri, MAX_IMAGE_DIMENSION);
                mealPhotoView.setImageBitmap(bitmap);

                MemoryCache memoryCache = new MemoryCache();
                memoryCache.put(meal.getUuid(), bitmap.copy(bitmap.getConfig(), true));
                meal.savePhoto(this, ImageHandler.scaleDownBitmap(this, bitmap.copy(bitmap.getConfig(), true), 200));
                uploadImageButton.setEnabled(true);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        bundle.putParcelable("meal", Parcels.wrap(meal));
    }
}
