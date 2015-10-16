package com.danielchabr.koreandiningadvisorapp.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Meal implements Parcelable {
    private String nameKorean;
    private String nameEnglish;
    private String description;

    public Meal(String nameKorean) {
        this.nameKorean = nameKorean;
    }

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

    @Override
    public int describeContents() {
        return 0;
    }

    public Meal(Parcel in) {
        setNameKorean(in.readString());
        setNameEnglish(in.readString());
        setDescription(in.readString());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(getNameKorean());
        dest.writeString(getNameEnglish());
        dest.writeString(getDescription());
    }

    public static final Parcelable.Creator<Meal> CREATOR
            = new Parcelable.Creator<Meal>() {
        public Meal createFromParcel(Parcel in) {
            return new Meal(in);
        }

        public Meal[] newArray(int size) {
            return new Meal[size];
        }
    };

    @Override
    public String toString() {
        return nameKorean + '\n' + nameEnglish;
    }
}
