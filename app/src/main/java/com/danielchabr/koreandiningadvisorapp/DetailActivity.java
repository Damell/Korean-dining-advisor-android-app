package com.danielchabr.koreandiningadvisorapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.danielchabr.koreandiningadvisorapp.model.Meal;
import com.danielchabr.koreandiningadvisorapp.rest.ApiClient;
import com.danielchabr.koreandiningadvisorapp.util.MemoryCache;
import com.squareup.picasso.Picasso;

import org.parceler.Parcels;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * @author Daniel Chabr
 *         Detail activity with all the details about a meal
 */
public class DetailActivity extends AppCompatActivity {

    private Meal meal;
    private final int EDIT_MEAL_CODE = 56;
    private TextView nameKorean;
    private TextView nameEnglish;
    private TextView transliteration;
    private ImageView photo;
    private TextView description;
    private RatingBar ratingBar;
    private TextView spiciness;
    private TextView ingredients;
    private TextView category;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        getSupportActionBar().setDisplayUseLogoEnabled(true);

        Bundle extras = getIntent().getExtras();
        if (savedInstanceState != null) {
            meal = Parcels.unwrap(savedInstanceState.getParcelable("selectedMeal"));
        } else if (extras != null) {
            meal = Parcels.unwrap(extras.getParcelable("selectedMeal"));
        }

        nameKorean = (TextView) findViewById(R.id.nameKorean);
        nameEnglish = (TextView) findViewById(R.id.nameEnglish);
        transliteration = (TextView) findViewById(R.id.transliteration);
        photo = (ImageView) findViewById(R.id.meal_image);
        description = (TextView) findViewById(R.id.description);
        ratingBar = (RatingBar) findViewById(R.id.meal_ratingBar);
        spiciness = (TextView) findViewById(R.id.spiciness);
        ingredients = (TextView) findViewById(R.id.ingredients);
        category = (TextView) findViewById(R.id.categories);

        if (meal != null) {
            loadMealIntoView();
        }
    }

    private void loadMealIntoView() {
        nameKorean.setText(meal.getKoreanName());
        transliteration.setText(meal.getTransliteratedName());
        nameEnglish.setText(meal.getEnglishName());
        description.setText(meal.getDescription());
        ratingBar.setRating(meal.getRating());
        spiciness.setText("" + getResources().getStringArray(R.array.spiciness_levels)[meal.getSpicyGrade() + 1]);
        if (meal.getIngredients() != null) {
            ingredients.setText(TextUtils.join(", ", meal.getIngredients()));
        }
        if (meal.getCategory() != null) {
            category.setText(TextUtils.join(", ", meal.getCategory()));
        }
        if (meal.hasPhotoLocal()) {
            MemoryCache memoryCache = new MemoryCache();
            Bitmap bitmap = memoryCache.get(meal.getUuid());
            if (bitmap == null) {
                photo.setImageBitmap(meal.loadPhoto(this));
            } else {
                photo.setImageBitmap(bitmap);
            }
        } else if (meal.hasPhoto()) {
            Picasso.with(this).load(ApiClient.getImageUrl() + meal.getPhotoUrl()).into(photo);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == android.R.id.home) {
            finish();
            return true;
        } else if (id == R.id.action_edit) {
            Intent editMeal = new Intent(DetailActivity.this, EditMealActivity.class);
            editMeal.putExtra("meal", Parcels.wrap(meal));
            startActivityForResult(editMeal, EDIT_MEAL_CODE);
        } else if (id == R.id.action_delete) {
            final String TAG = "DeleteMeal";
            Call call = new ApiClient().getMealService().delete(meal.getId());
            final ProgressDialog progress = new ProgressDialog(this);
            progress.setTitle("Loading");
            progress.setMessage("Wait while loading...");
            progress.show();
            call.enqueue(new Callback() {
                @Override
                public void onResponse(Response response, Retrofit retrofit) {
                    if (!DetailActivity.this.isFinishing() && progress.isShowing())
                        progress.dismiss();
                    if (response.isSuccess()) {
                        Log.v(TAG, "code: " + response.code());

                        Intent showDashboard = new Intent();
                        setResult(Activity.RESULT_OK, showDashboard);
                        finish();
                    } else {
                        Log.v(TAG, "response: " + response.body());
                        Log.v(TAG, "response: " + response.errorBody().toString());
                        Log.v(TAG, "response: " + response.message());
                        Log.v(TAG, "code: " + response.code());
                    }
                }

                @Override
                public void onFailure(Throwable t) {
                    if (!DetailActivity.this.isFinishing() && progress.isShowing())
                        progress.dismiss();
                    Log.v(TAG, "error");
                    Log.v(TAG, t.getMessage());
                    AlertDialog.Builder builder = new AlertDialog.Builder(DetailActivity.this);
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
        Log.v("ResultEdit", "result");
        if (requestCode == EDIT_MEAL_CODE && resultCode == Activity.RESULT_OK) {
            meal = Parcels.unwrap(data.getExtras().getParcelable("editedMeal"));
            Log.v("ResultEdit", "success");
            loadMealIntoView();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        bundle.putParcelable("selectedMeal", Parcels.wrap(meal));
    }
}
