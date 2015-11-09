package com.danielchabr.koreandiningadvisorapp.model;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;

import com.danielchabr.koreandiningadvisorapp.util.FileCache;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import org.parceler.Parcel;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@JsonIgnoreProperties({"id", "mealPic", "rating"})
@Parcel
public class Meal {
    String koreanName;
    String englishName;
    String transliteratedName;
    String description;
    Uri photoUri;
    String photoUrl;
    String id;
    File file;
    List<String> ingredients;
    List<String> category;
    String uuid = UUID.randomUUID().toString();
    int rating;
    int spicyGrade;
    int viewNum;

    public Meal() { /*Required empty bean constructor*/ }

    public Meal(String nameKorean, String nameEnglish) {
        this.koreanName = nameKorean;
        this.englishName = nameEnglish;
    }

    public String getKoreanName() {
        return koreanName;
    }

    public void setKoreanName(String koreanName) {
        this.koreanName = koreanName;
    }

    public String getEnglishName() {
        return englishName;
    }

    public void setEnglishName(String englishName) {
        this.englishName = englishName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String toString() {
        return koreanName + '\n' + englishName;
    }

    /**
    public Bitmap getPhoto() {
        if (this.photo != null) {
            return BitmapFactory.decodeByteArray(this.photo, 0, this.photo.length);
        } else {
            return null;
        }
    }

    public void setPhoto(Bitmap photo) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        photo.compress(Bitmap.CompressFormat.JPEG, 90, stream);
        this.photo = stream.toByteArray();
    }
     */

    public void savePhoto(Context context, Bitmap photo) {
        FileCache fileCache = new FileCache(context);
        this.photoUri = Uri.parse("localFile://" + this.getEnglishName() + photo.getGenerationId());
        FileOutputStream out = null;
        file = fileCache.getFile(photoUri.toString());
        try {
            out = new FileOutputStream(fileCache.getFile(photoUri.toString()));
            photo.compress(Bitmap.CompressFormat.JPEG, 85, out); // bmp is your Bitmap instance
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            photo.recycle();
        }
    }

    public Bitmap loadPhoto(Context context) {
        FileCache fileCache = new FileCache(context);
        try {
            return BitmapFactory.decodeStream(new FileInputStream(fileCache.getFile(photoUri.toString())));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean hasPhoto() {
        return photoUri != null;
    }

    public String getUuid() {
        return uuid;
    }

    public File getFile() {
        return file;
    }

    public List<String> getIngredients() {
        return ingredients;
    }

    public void addIngredient(String ingredient) {
        if (this.ingredients == null) {
            this.ingredients = new ArrayList<>();
        }
        this.ingredients.add(ingredient);
    }

    public List<String> getCategory() {
        return category;
    }

    public void addCategory(String category) {
        if (this.category == null) {
            this.category = new ArrayList<>();
        }
        this.category.add(category);
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public int getSpicyGrade() {
        return spicyGrade;
    }

    public void setSpicyGrade(int spicyGrade) {
        if (spicyGrade >= 0 && spicyGrade <= 5) {
            this.spicyGrade = spicyGrade;
        }
    }

    public int getViewNum() {
        return viewNum;
    }

    public void setViewNum(int viewNum) {
        this.viewNum = viewNum;
    }

    public String getTransliteratedName() {
        return transliteratedName;
    }

    public void setTransliteratedName(String transliteratedName) {
        this.transliteratedName = transliteratedName;
    }


    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }
}
