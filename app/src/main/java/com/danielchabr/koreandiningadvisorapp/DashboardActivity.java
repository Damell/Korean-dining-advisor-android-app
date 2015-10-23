package com.danielchabr.koreandiningadvisorapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.danielchabr.koreandiningadvisorapp.model.Meal;

import org.parceler.Parcels;

import java.util.ArrayList;

public class DashboardActivity extends AppCompatActivity {

    private ListView mealListView;
    private ArrayList<Meal> meals;
    private int INSERT_MEAL_CODE = 8;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.drawable.logo_full_scaled);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.rgb(100, 100, 100)));

        mealListView = (ListView) findViewById(R.id.listView);

        meals = new ArrayList();
        meals.add(new Meal("김치 찌개", "Kimchi Jigae"));
        meals.add(new Meal("비빔밥", "Bibimbap"));

        ArrayAdapter<Meal> adapter = new MealAdapter(this, meals);

        // Assign adapter to ListView
        mealListView.setAdapter(adapter);

        // ListView Item Click Listener
        mealListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // ListView Clicked item index
                int itemPosition = position;
                // ListView Clicked item value
                Meal selected = (Meal) mealListView.getItemAtPosition(position);

                Intent showDetail = new Intent(DashboardActivity.this, DetailActivity.class);
                showDetail.putExtra("selectedMeal", Parcels.wrap(selected));
                startActivity(showDetail);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_dashboard, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_add_meal) {
            Intent insertMeal = new Intent(DashboardActivity.this, InsertMeal.class);
            startActivityForResult(insertMeal, INSERT_MEAL_CODE);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == INSERT_MEAL_CODE && resultCode == Activity.RESULT_OK) {
            Meal newMeal = Parcels.unwrap(data.getExtras().getParcelable("createdMeal"));
            meals.add(newMeal);
            mealListView.deferNotifyDataSetChanged();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}

class MealAdapter extends ArrayAdapter<Meal> {
    ArrayList<Meal> meals;
    public MealAdapter (Context context, ArrayList<Meal> meals) {
        super(context, 0, meals);
        this.meals = meals;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Meal meal = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_meal, parent, false);
        }
        // Lookup view for data population
        TextView koreanName = (TextView) convertView.findViewById(R.id.koreanName);
        TextView englishName = (TextView) convertView.findViewById(R.id.englishName);
        ImageView photo = (ImageView) convertView.findViewById(R.id.mealPhoto);
        // Populate the data into the template view using the data object
        koreanName.setText(meal.getNameKorean());
        englishName.setText(meal.getNameEnglish());
        if (meal.getPhoto() != null) {
            photo.setImageBitmap(meal.getPhoto());
        }
        // Return the completed view to render on screen
        return convertView;
    }
}
