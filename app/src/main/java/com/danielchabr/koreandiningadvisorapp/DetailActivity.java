package com.danielchabr.koreandiningadvisorapp;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.danielchabr.koreandiningadvisorapp.model.Meal;
import com.danielchabr.koreandiningadvisorapp.util.MemoryCache;

import org.parceler.Parcels;

public class DetailActivity extends AppCompatActivity {

    Meal meal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        getSupportActionBar().setDisplayUseLogoEnabled(true);

        Bundle extras = getIntent().getExtras();
        meal = Parcels.unwrap(extras.getParcelable("selectedMeal"));

        TextView nameKorean = (TextView) findViewById(R.id.nameKorean);
        TextView nameEnglish = (TextView) findViewById(R.id.nameEnglish);
        TextView transliteration = (TextView) findViewById(R.id.transliteration);
        ImageView photo = (ImageView) findViewById(R.id.meal_image);
        TextView description = (TextView) findViewById(R.id.description);
        RatingBar ratingBar = (RatingBar) findViewById(R.id.meal_ratingBar);
        TextView spiciness = (TextView) findViewById(R.id.spiciness);
        TextView ingredients = (TextView) findViewById(R.id.ingredients);
        TextView category = (TextView) findViewById(R.id.categories);

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
        if (meal.hasPhoto()) {
            MemoryCache memoryCache = new MemoryCache();
            Bitmap bitmap = memoryCache.get(meal.getUuid());
            if (bitmap == null) {
                photo.setImageBitmap(meal.loadPhoto(this));
                //String url = "http://www.gettyimages.co.uk/gi-resources/images/Homepage/Category-Creative/UK/UK_Creative_462809583.jpg";
                //Picasso.with(this).load(url).into(photo);
            } else {
                photo.setImageBitmap(bitmap);
            }
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

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_edit) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
