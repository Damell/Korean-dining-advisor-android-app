package com.danielchabr.koreandiningadvisorapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.danielchabr.koreandiningadvisorapp.model.Meal;
import com.danielchabr.koreandiningadvisorapp.util.ImageHandler;

import org.parceler.Parcels;

import java.io.FileNotFoundException;
import java.io.IOException;

public class InsertMeal extends AppCompatActivity {
    private final int REQUEST_CODE = 5;
    private Bitmap bitmap;
    private Uri photoUri;
    private ImageView mealPhotoView;
    private final int MAX_IMAGE_DIMENSION = 180;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert_meal);

        Button chooseImageButton = (Button) findViewById(R.id.choose_photo_button);
        chooseImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT,null);
                galleryIntent.setType("image/*");
                galleryIntent.addCategory(Intent.CATEGORY_OPENABLE);

                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);


                Intent chooser = new Intent(Intent.ACTION_CHOOSER);
                chooser.putExtra(Intent.EXTRA_INTENT, galleryIntent);
                chooser.putExtra(Intent.EXTRA_TITLE, "title");

                Intent[] intentArray =  {cameraIntent};
                chooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentArray);
                startActivityForResult(chooser,REQUEST_CODE);
            }
        });
        mealPhotoView = (ImageView) findViewById(R.id.insert_photo_view);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_insert, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_insert) {
            EditText koreanName = (EditText) findViewById(R.id.inputKoreanName);
            EditText englishName = (EditText) findViewById(R.id.inputEnglishName);
            EditText description = (EditText) findViewById(R.id.description_edit);
            Meal newMeal = new Meal();
            newMeal.setNameKorean(koreanName.getText().toString());
            newMeal.setNameEnglish(englishName.getText().toString());
            newMeal.setDescription(description.getText().toString());
            if (newMeal.getNameKorean().trim().isEmpty()) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("Korean name of a dish needs to be filled")
                        .setTitle("Missing values");
                AlertDialog dialog = builder.create();
                dialog.show();
                return false;
            }
            if (bitmap != null) {
                //newMeal.setPhoto(scaleDownBitmap(bitmap, MAX_IMAGE_DIMENSION, this));
                //newMeal.setPhotoUri(photoUri);
                newMeal.savePhoto(this, bitmap);
                bitmap.recycle();
            }

            Intent showDashboard = new Intent();
            showDashboard.putExtra("createdMeal", Parcels.wrap(newMeal));
            setResult(Activity.RESULT_OK, showDashboard);
            finish();
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
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
