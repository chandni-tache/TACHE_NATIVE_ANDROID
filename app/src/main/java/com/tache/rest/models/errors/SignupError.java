package com.tache.rest.models.errors;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by ujjwal on 8/13/16.
 */

public class SignupError extends BaseError{
    @SerializedName("name")
    @Expose
    private ArrayList<String> name = new ArrayList<>();

    @SerializedName("email")
    @Expose
    private ArrayList<String> email = new ArrayList<>();

    @SerializedName("mobile")
    @Expose
    private ArrayList<String> mobile = new ArrayList<>();

    @SerializedName("password")
    @Expose
    private ArrayList<String> password = new ArrayList<>();

    @SerializedName("username")
    @Expose
    private ArrayList<String> username = new ArrayList<>();

    public ArrayList<String> getName() {
        return name;
    }

    public void setName(ArrayList<String> name) {
        this.name = name;
    }

    public ArrayList<String> getEmail() {
        return email;
    }

    public void setEmail(ArrayList<String> email) {
        this.email = email;
    }

    public ArrayList<String> getMobile() {
        return mobile;
    }

    public void setMobile(ArrayList<String> mobile) {
        this.mobile = mobile;
    }

    public ArrayList<String> getPassword() {
        return password;
    }

    public void setPassword(ArrayList<String> password) {
        this.password = password;
    }

    public ArrayList<String> getUsername() {
        return username;
    }

    public void setUsername(ArrayList<String> username) {
        this.username = username;
    }
}
