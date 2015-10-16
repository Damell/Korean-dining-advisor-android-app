package com.danielchabr.koreandiningadvisorapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.danielchabr.koreandiningadvisorapp.model.Meal;

import java.util.ArrayList;

public class Dashboard extends AppCompatActivity {

    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        listView = (ListView) findViewById(R.id.listView);

        ArrayList<Meal> meals = new ArrayList();
        meals.add(new Meal("김치 찌개", "Kimchi Jigae"));
        meals.add(new Meal("비빔밥", "Bibimbap"));

        ArrayAdapter<Meal> adapter = new MealAdapter(this, android.R.layout.simple_list_item_1, meals);

        // Assign adapter to ListView
        listView.setAdapter(adapter);

        // ListView Item Click Listener
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // ListView Clicked item index
                int itemPosition = position;
                // ListView Clicked item value
                Meal selected = (Meal) listView.getItemAtPosition(position);

                Intent showDetail = new Intent(Dashboard.this, Detail.class);
                showDetail.putExtra("selectedMeal", selected);
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
        if (id == R.id.action_account) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}

class MealAdapter extends ArrayAdapter<Meal> {
    ArrayList<Meal> data;
    public MealAdapter (Context context, int layoutResourceId, ArrayList<Meal> data) {
        super(context, layoutResourceId, data);
        this.data = data;
    }
}
