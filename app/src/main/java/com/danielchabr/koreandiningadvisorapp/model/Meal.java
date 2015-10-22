package com.danielchabr.koreandiningadvisorapp.model;

import org.parceler.Parcel;

@Parcel
public class Meal {
    String nameKorean;
    String nameEnglish;
    String description;

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
}
