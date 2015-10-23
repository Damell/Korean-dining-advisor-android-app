package com.danielchabr.koreandiningadvisorapp.model;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import org.parceler.Parcel;

import java.io.ByteArrayOutputStream;

@Parcel
public class Meal {
    String nameKorean;
    String nameEnglish;
    String description;
    byte[] photo;

    public Meal() { /*Required empty bean constructor*/ }

    public Meal(String nameKorean, String nameEnglish) {
        this.nameKorean = nameKorean;
        this.nameEnglish = nameEnglish;
    }

    public String getNameEnglish() {
        return nameEnglish;
    }

    public void setNameEnglish(String nameEnglish) {
        this.nameEnglish = nameEnglish;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getNameKorean() {

        return nameKorean;
    }

    public void setNameKorean(String nameKorean) {
        this.nameKorean = nameKorean;
    }

    public String toString() {
        return nameKorean + '\n' + nameEnglish;
    }

    public Bitmap getPhoto() {
        if (this.photo != null) {
            return BitmapFactory.decodeByteArray(this.photo, 0, this.photo.length);
        } else {
            return null;
        }
    }

    public void setPhoto(Bitmap photo) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        photo.compress(Bitmap.CompressFormat.PNG, 90, stream);
        this.photo = stream.toByteArray();
    }
}
