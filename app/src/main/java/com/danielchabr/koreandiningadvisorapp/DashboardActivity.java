package com.danielchabr.koreandiningadvisorapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.danielchabr.koreandiningadvisorapp.model.Meal;
import com.danielchabr.koreandiningadvisorapp.model.User;
import com.danielchabr.koreandiningadvisorapp.rest.ApiClient;
import com.danielchabr.koreandiningadvisorapp.rest.service.MealService;
import com.danielchabr.koreandiningadvisorapp.util.FileCache;
import com.danielchabr.koreandiningadvisorapp.util.MemoryCache;
import com.squareup.picasso.Picasso;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Retrofit;

/**
 * @author Daniel Chabr
 *         The activity for main dashboard listing all meals
 */
public class DashboardActivity extends AppCompatActivity {

    private User user;
    private ListView mealListView;
    private ArrayAdapter<Meal> mealAdapter;
    private ArrayList<Meal> meals;
    private int INSERT_MEAL_CODE = 8;
    private int SHOW_MEAL_CODE = 13;
    private MealService mealService;
    private EditText searchInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle extras = getIntent().getExtras();
        // try to restore user
        if (savedInstanceState != null) {
            user = Parcels.unwrap(savedInstanceState.getParcelable("user"));
        } else if (extras != null) {
            Parcelable p = extras.getParcelable("user");
            if (p != null) {
                user = Parcels.unwrap(p);
            }
        }

        // if the user is not restored start log in activity
        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0); // 0 - for private mode
        if (user == null && !pref.getBoolean("authenticated", false)) {
            //authenticate
            Intent authenticate = new Intent(DashboardActivity.this, LoginActivity.class);
            startActivity(authenticate);
            finish();
        }

        mealService = new ApiClient().getMealService();
        setContentView(R.layout.activity_dashboard);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.drawable.logo_full_scaled);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.rgb(100, 100, 100)));

        FileCache fileCache = new FileCache(this);
        fileCache.clear();

        searchInput = (EditText) findViewById(R.id.search);
        searchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                mealAdapter.getFilter().filter(charSequence);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        if (savedInstanceState != null) {
            meals = Parcels.unwrap(savedInstanceState.getParcelable("meals"));
        } else {
            meals = new ArrayList();
            // mock meals
            /**
             meals.add(new Meal("김치 찌개", "Kimchi Jigae"));
             meals.add(new Meal("비빔밥", "Bibimbap"));
             */
        }

        mealListView = (ListView) findViewById(R.id.listView);
        mealAdapter = new MealAdapter(this, meals);
        mealListView.setAdapter(mealAdapter);

        loadMeals(mealService, meals, mealAdapter);

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
                startActivityForResult(showDetail, SHOW_MEAL_CODE);
            }
        });
    }

    @Override
    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        bundle.putParcelable("meals", Parcels.wrap(meals));
        bundle.putParcelable("user", Parcels.wrap(user));
    }

    private void loadMeals (MealService mealService, final ArrayList<Meal> meals, final ArrayAdapter<Meal> mealAdapter) {
        final String TAG = "GetMeals";
        Call<List<Meal>> call = mealService.getAll();
        Log.v(TAG, "Sent request");
        final ProgressDialog progress = new ProgressDialog(this);
        progress.setTitle("Loading");
        progress.setMessage("Wait while loading...");
        progress.show();
        call.enqueue(new Callback<List<Meal>>() {
            @Override
            public void onResponse(retrofit.Response<List<Meal>> response, Retrofit retrofit) {
                if (!DashboardActivity.this.isFinishing() && progress.isShowing())
                    progress.dismiss();
                if (response.isSuccess()) {
                    Log.v(TAG, "received response");
                    Log.v(TAG, "" + response.body());
                    meals.clear();
                    meals.addAll(new ArrayList<>(response.body()));
                    mealAdapter.notifyDataSetChanged();
                    //mealListView.deferNotifyDataSetChanged();
                } else {
                    Log.v(TAG, "failure: " + response.errorBody());
                    Log.v(TAG, "failure: " + response.message());
                    Log.v(TAG, "failure: " + response.code());
                    Log.v(TAG, "failure: " + response.body());
                }
            }

            @Override
            public void onFailure(Throwable t) {
                if (!DashboardActivity.this.isFinishing() && progress.isShowing())
                    progress.dismiss();
                Log.v(TAG, "error " + t.getMessage());
                Log.v(TAG, "error " + t.getStackTrace());
                AlertDialog.Builder builder = new AlertDialog.Builder(DashboardActivity.this);
                builder.setMessage("Network error")
                        .setTitle("No network connection");
                AlertDialog dialog = builder.create();
                dialog.show();
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
            Intent insertMeal = new Intent(DashboardActivity.this, InsertMealActivity.class);
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
            mealAdapter.notifyDataSetChanged();
            mealListView.deferNotifyDataSetChanged();
            loadMeals(mealService, meals, mealAdapter);
        } else if (requestCode == SHOW_MEAL_CODE && resultCode == Activity.RESULT_OK) {
            loadMeals(mealService, meals, mealAdapter);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}

class MealAdapter extends ArrayAdapter<Meal> implements Filterable {
    ArrayList<Meal> meals;
    ArrayList<Meal> filteredMeals;
    private Filter mFilter = new MealFilter();

    public MealAdapter (Context context, ArrayList<Meal> meals) {
        super(context, 0, meals);
        this.meals = meals;
        this.filteredMeals = meals;
    }

    public void setData(ArrayList<Meal> meals) {
        this.meals = meals;
    }

    @Override
    public int getCount() {
        return filteredMeals.size();
    }

    @Override
    public Meal getItem(int position) {
        return filteredMeals.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
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
        koreanName.setText(meal.getKoreanName());
        englishName.setText(meal.getEnglishName());

        if (meal.hasPhotoLocal()) {
            Log.v("GetView", "Has photo local");
            MemoryCache memoryCache = new MemoryCache();
            Bitmap bitmap = memoryCache.get(meal.getUuid());
            if (bitmap == null) {
                String url = "http://www.gettyimages.co.uk/gi-resources/images/Homepage/Category-Creative/UK/UK_Creative_462809583.jpg";
                Picasso.with(getContext()).load(url).into(photo);
            } else {
                photo.setImageBitmap(bitmap);
            }
        } else if (meal.hasPhoto()) {
            Log.v("GetView", "Loading over picasso with url: " + ApiClient.getImageUrl() + meal.getPhotoUrl());
            Picasso.with(getContext()).load(ApiClient.getImageUrl() + meal.getPhotoUrl()).into(photo);
        } else {
            Log.v("GetView", "No photo to load");
            photo.setImageResource(R.drawable.logo);
        }
        // Return the completed view to render on screen
        return convertView;
    }

    @Override
    public Filter getFilter() {
        return mFilter;
    }

    private class MealFilter extends Filter {
        @Override
        protected Filter.FilterResults performFiltering(CharSequence constraint) {

            String filterString = constraint.toString().toLowerCase();

            FilterResults results = new FilterResults();

            final ArrayList<Meal> list = meals;

            int count = list.size();
            final ArrayList<Meal> nlist = new ArrayList<Meal>(count);

            String filterableString ;

            for (int i = 0; i < count; i++) {
                Meal item = list.get(i);
                if (isContainsHangul(filterString)) {
                    filterableString = convertToHangulJasoString(item.getKoreanName());
                    filterString = convertToHangulJasoString(filterString);
                } else {
                    filterableString = item.getEnglishName();
                }
                if (filterableString.toLowerCase().contains(filterString)) {
                    nlist.add(list.get(i));
                }
            }

            results.values = nlist;
            results.count = nlist.size();

            return results;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            filteredMeals = (ArrayList<Meal>) results.values;
            notifyDataSetChanged();
        }

    }

    /**
     *  Following code taken from https://github.com/Bhb2011/HangulFilteredArrayAdapter
     */
    final static char[] ChoSung = { 0x3131, 0x3132, 0x3134, 0x3137, 0x3138,
            0x3139, 0x3141, 0x3142, 0x3143, 0x3145, 0x3146, 0x3147, 0x3148,
            0x3149, 0x314a, 0x314b, 0x314c, 0x314d, 0x314e };

    final static char[] JwungSung = { 0x314f, 0x3150, 0x3151, 0x3152, 0x3153,
            0x3154, 0x3155, 0x3156, 0x3157, 0x3158, 0x3159, 0x315a, 0x315b,
            0x315c, 0x315d, 0x315e, 0x315f, 0x3160, 0x3161, 0x3162, 0x3163 };

    final static char[] JongSung = { 0, 0x3131, 0x3132, 0x3133, 0x3134, 0x3135,
            0x3136, 0x3137, 0x3139, 0x313a, 0x313b, 0x313c, 0x313d, 0x313e,
            0x313f, 0x3140, 0x3141, 0x3142, 0x3144, 0x3145, 0x3146, 0x3147,
            0x3148, 0x314a, 0x314b, 0x314c, 0x314d, 0x314e };

    public boolean isHangul(Character arg) {
        Character.UnicodeBlock unicodeBlock = Character.UnicodeBlock.of(arg);
        if(Character.UnicodeBlock.HANGUL_SYLLABLES.equals(unicodeBlock) ||
                Character.UnicodeBlock.HANGUL_COMPATIBILITY_JAMO.equals(unicodeBlock) ||
                Character.UnicodeBlock.HANGUL_JAMO.equals(unicodeBlock)) {
            return true;
        }
        return false;
    }

    public boolean isContainsHangul(String str)
    {
        for(int i = 0 ; i < str.length() ; i++)
        {
            char ch = str.charAt(i);
            if(isHangul(ch))
                return true;
        }
        return false;
    }

    public String convertToHangulJasoString(String str) {
        List<String> result = new ArrayList<String>();
        String[] array = str.split("");
        for (int i = 1 ; i < array.length ; i++ ) {
            result.add(array[i]);
        }

        for (int i = 0; i < result.size(); i++) {
            char ch = result.get(i).charAt(0);
            if(isHangul(ch)) {
                result.remove(i);
                result.add(i, hangulToJaso(ch));
            }
        }

        StringBuffer strBuf = new StringBuffer();
        for(String e : result) {
            strBuf.append(e);
        }
        return strBuf.toString();
    }

    public static String hangulToJaso(char s) {
        int a, b, c;
        String result = "";

        char ch = s;

        if (ch >= 0xAC00 && ch <= 0xD7A3) {
            c = ch - 0xAC00;
            a = c / (21 * 28);
            c = c % (21 * 28);
            b = c / 28;
            c = c % 28;

            result = result + ChoSung[a] + JwungSung[b];
            if (c != 0)
                result = result + JongSung[c];
        } else {
            result = result + ch;
        }
        return result;
    }

}
