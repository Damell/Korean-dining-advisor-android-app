package com.danielchabr.koreandiningadvisorapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.danielchabr.koreandiningadvisorapp.model.Meal;

public class Detail extends AppCompatActivity {

    Meal meal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        Bundle extras = getIntent().getExtras();
        meal = extras.getParcelable("selectedMeal");

        TextView nameKorean = (TextView) findViewById(R.id.nameKorean);
        nameKorean.setText(meal.getNameKorean());

        TextView nameEnglish = (TextView) findViewById(R.id.nameEnglish);
        nameEnglish.setText(meal.getNameEnglish());
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
