package com.danielchabr.koreandiningadvisorapp.model;

import org.parceler.Parcel;

@Parcel
public class User {
    String username;
    String email;
    String password;

    public User() { /*Required empty bean constructor*/ }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}

