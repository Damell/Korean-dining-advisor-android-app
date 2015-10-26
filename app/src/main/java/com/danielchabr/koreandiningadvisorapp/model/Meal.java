package com.danielchabr.koreandiningadvisorapp.model;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;

import org.parceler.Parcel;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Random;

@Parcel
public class Meal {
    String nameKorean;
    String nameEnglish;
    String description;
    byte[] photo;
    Uri photoUri;
    String filename;

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
        this.filename = this.getNameEnglish() + photo.getGenerationId() + new Random().nextInt();
        FileOutputStream out = null;
        try {
            File file = new File(context.getFilesDir(), filename);
            out = new FileOutputStream(file);
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
        File file = new File(context.getFilesDir(), this.filename);
        try {
            return BitmapFactory.decodeStream(new FileInputStream(file));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean hasPhoto() {
        return filename != null;
    }
}
